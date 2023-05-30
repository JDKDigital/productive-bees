package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class BottlerBlockEntityRenderer implements BlockEntityRenderer<BottlerBlockEntity>
{
    public BottlerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(BottlerBlockEntity tileEntityIn, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean hasBottle = tileEntityIn.getBlockState().getValue(Bottler.HAS_BOTTLE);
        if (hasBottle) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GLASS_BOTTLE), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, tileEntityIn.getLevel(), 0);
            matrixStackIn.popPose();
        }
    }
}