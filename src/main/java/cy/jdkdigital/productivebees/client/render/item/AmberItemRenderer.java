package cy.jdkdigital.productivebees.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class AmberItemRenderer extends BlockEntityWithoutLevelRenderer {
    AmberBlockEntity blockEntity = null;

    public AmberItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext transformType, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int packedLightIn, int packedUV) {
        ProductiveBees.LOGGER.info("stack tag: " + stack.getTag());
        if (blockEntity == null) {
            blockEntity = new AmberBlockEntity(BlockPos.ZERO, Blocks.GLASS.defaultBlockState());
        }

        blockEntity.loadPacketNBT(stack.getTag());

        matrixStack.pushPose();

        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, matrixStack, buffer, packedLightIn, packedUV);

        matrixStack.popPose();
    }
}
