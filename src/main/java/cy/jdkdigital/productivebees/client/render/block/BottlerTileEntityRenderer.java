package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class BottlerTileEntityRenderer implements BlockEntityRenderer<BottlerBlockEntity>
{
    public BottlerTileEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(BottlerBlockEntity tileEntityIn, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean hasBottle = tileEntityIn.getBlockState().getValue(Bottler.HAS_BOTTLE);
        if (hasBottle) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GLASS_BOTTLE), ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
    }
}