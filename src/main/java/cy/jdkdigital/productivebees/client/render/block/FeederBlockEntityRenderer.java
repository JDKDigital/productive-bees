package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.client.render.block.state.FeederRenderState;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper.BlockEntityItemStackHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeederBlockEntityRenderer implements BlockEntityRenderer<FeederBlockEntity, FeederRenderState>
{
    public static final HashMap<Integer, List<Pair<Float, Float>>> POSITIONS = new HashMap<>()
    {{
        put(1, new ArrayList<>()
        {{
            add(Pair.of(0.5f, 0.5f));
            add(Pair.of(0.5f, 0.5f));
            add(Pair.of(0.5f, 0.5f));
        }});
        put(2, new ArrayList<>()
        {{
            add(Pair.of(0.3f, 0.3f));
            add(Pair.of(0.5f, 0.5f));
            add(Pair.of(0.7f, 0.7f));
        }});
        put(3, new ArrayList<>()
        {{
            add(Pair.of(0.3f, 0.3f));
            add(Pair.of(0.5f, 0.7f));
            add(Pair.of(0.7f, 0.4f));
        }});
    }};

    private static final BlockDisplayContext SLAB_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;

    public FeederBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public FeederRenderState createRenderState() {
        return new FeederRenderState();
    }

    @Override
    public void extractRenderState(FeederBlockEntity be, FeederRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);

        state.slabType = be.getBlockState().getValue(SlabBlock.TYPE);
        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        state.facingAngle = switch (facing) {
            case NORTH -> 180f;
            case EAST -> 90f;
            case WEST -> 270f;
            default -> 0f;
        };

        state.slots.clear();
        if (be.getLevel() != null) {
            var invHandler = be.getLevel().getCapability(Capabilities.Item.BLOCK, be.getBlockPos(), null);
            if (invHandler instanceof BlockEntityItemStackHandler stackHandler) {
                List<ItemStack> filledSlots = new ArrayList<>();
                for (int slot = 0; slot < stackHandler.size(); ++slot) {
                    var stack = stackHandler.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        filledSlots.add(stack);
                    }
                }

                if (!filledSlots.isEmpty()) {
                    for (int slot = 0; slot < Math.min(3, stackHandler.size()); ++slot) {
                        ItemStack slotStack = stackHandler.getStackInSlot(slot);
                        if (slotStack.isEmpty()) continue;

                        FeederRenderState.Slot slotState = new FeederRenderState.Slot();
                        slotState.isFlower = slotStack.is(ItemTags.FLOWERS);
                        slotState.position = POSITIONS.get(Math.min(3, filledSlots.size())).get(slot);
                        slotState.slotIndex = slot;
                        this.itemModelResolver.updateForTopItem(slotState.stackState, slotStack, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
                        state.slots.add(slotState);
                    }
                }
            }
        }

        BlockState slabState;
        if (be.baseBlock != null) {
            slabState = be.baseBlock.defaultBlockState();
        } else {
            slabState = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();
        }
        if (slabState.getBlock() instanceof SlabBlock) {
            slabState = slabState.setValue(SlabBlock.TYPE, state.slabType);
        }
        state.slabState = slabState;
        this.blockModelResolver.update(state.slabModelState, slabState, SLAB_DISPLAY_CONTEXT);
    }

    @Override
    public void submit(FeederRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        state.slabModelState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        for (FeederRenderState.Slot slot : state.slots) {
            float rotation = slot.isFlower ? 90F : 35.0F * slot.slotIndex;
            float scale = slot.isFlower ? 0.775F : 0.575F;

            poseStack.pushPose();
            poseStack.translate(slot.position.getFirst(), 0.52D + (state.slabType.equals(SlabType.TOP) || state.slabType.equals(SlabType.DOUBLE) ? 0.5d : 0) + (slot.slotIndex * 0.01f), slot.position.getSecond());
            poseStack.mulPose(Axis.YP.rotationDegrees(state.facingAngle));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation));
            poseStack.scale(scale, scale, scale);
            slot.stackState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
