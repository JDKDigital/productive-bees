package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeederBlockEntityRenderer implements BlockEntityRenderer<FeederBlockEntity>
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

    public FeederBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(FeederBlockEntity blockEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        SlabType slabType = blockEntity.getBlockState().getValue(SlabBlock.TYPE);

        if (blockEntity.getLevel() != null) {
            IItemHandler invHandler = blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
            if (invHandler instanceof ItemStackHandler) {
                List<ItemStack> filledSlots = new ArrayList<>();
                for (int slot = 0; slot < invHandler.getSlots(); ++slot) {
                    var stack = invHandler.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        filledSlots.add(stack);
                    }
                }

                if (filledSlots.size() > 0) {
                    for (int slot = 0; slot < Math.min(3, invHandler.getSlots()); ++slot) {
                        ItemStack slotStack = invHandler.getStackInSlot(slot);

                        if (slotStack.isEmpty()) {
                            continue;
                        }

                        boolean isFlower = slotStack.is(ItemTags.FLOWERS);
                        Pair<Float, Float> pos = POSITIONS.get(Math.min(3, filledSlots.size())).get(slot);
                        float rotation = isFlower ? 90F : 35.0F * slot;
                        float scale = isFlower ? 0.775F : 0.575F;

                        poseStack.pushPose();
                        poseStack.translate(pos.getFirst(), 0.52D + (slabType.equals(SlabType.TOP) || slabType.equals(SlabType.DOUBLE) ? 0.5d : 0), pos.getSecond());
                        poseStack.mulPose(Axis.XP.rotationDegrees(rotation));
                        poseStack.scale(scale, scale, scale);
                        Minecraft.getInstance().getItemRenderer().renderStatic(slotStack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, blockEntity.getLevel(), 0);
                        poseStack.popPose();
                    }
                }
            }
        }

        BlockState slabState;
        if (blockEntity.baseBlock != null) {
            slabState = blockEntity.baseBlock.defaultBlockState();
        } else {
            slabState = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();
        }

        if (slabState.getBlock() instanceof SlabBlock) {
            slabState = slabState.setValue(SlabBlock.TYPE, blockEntity.getBlockState().getValue(SlabBlock.TYPE)).setValue(SlabBlock.TYPE, slabType);
        }

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(slabState, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}