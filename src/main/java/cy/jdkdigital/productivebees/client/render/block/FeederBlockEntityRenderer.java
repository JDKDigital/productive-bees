package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

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

    public void render(FeederBlockEntity tileEntityIn, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        SlabType slabType = tileEntityIn.getBlockState().getValue(SlabBlock.TYPE);
        tileEntityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            int filledSlots = 0;
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                if (!handler.getStackInSlot(slot).isEmpty()) {
                    filledSlots++;
                }
            }

            if (filledSlots > 0) {
                for (int slot = 0; slot < handler.getSlots(); ++slot) {
                    ItemStack slotStack = handler.getStackInSlot(slot);

                    if (slotStack.isEmpty()) {
                        continue;
                    }

                    boolean isFlower = slotStack.is(ItemTags.FLOWERS);
                    Pair<Float, Float> pos = POSITIONS.get(filledSlots).get(slot);
                    float rotation = isFlower ? 90F : 35.0F * slot;
                    float zScale = isFlower ? 0.775F : 0.575F;

                    poseStack.pushPose();
                    poseStack.translate(pos.getFirst(), 0.52D + (slabType.equals(SlabType.TOP) || slabType.equals(SlabType.DOUBLE) ? 0.5d : 0), pos.getSecond());
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation));
                    poseStack.scale(0.575F, zScale, 0.575F);
                    Minecraft.getInstance().getItemRenderer().renderStatic(slotStack, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, 0);
                    poseStack.popPose();
                }
            }
        });

        BlockState slabState;
        if (tileEntityIn.baseBlock != null) {
            slabState = tileEntityIn.baseBlock.defaultBlockState();
        } else {
            slabState = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();
        }
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(slabState.setValue(SlabBlock.TYPE, tileEntityIn.getBlockState().getValue(SlabBlock.TYPE)).setValue(SlabBlock.TYPE, slabType), poseStack, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
    }
}