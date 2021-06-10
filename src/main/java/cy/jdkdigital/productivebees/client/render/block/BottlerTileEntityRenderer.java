package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.tileentity.BottlerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class BottlerTileEntityRenderer extends TileEntityRenderer<BottlerTileEntity>
{
    public BottlerTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public void render(BottlerTileEntity tileEntityIn, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean hasBottle = tileEntityIn.getBlockState().getValue(Bottler.HAS_BOTTLE);
        if (hasBottle) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GLASS_BOTTLE), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.popPose();
        }
    }
}