package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.block.Bottler;
import cy.jdkdigital.productivebees.tileentity.BottlerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BottlerTileEntityRenderer extends TileEntityRenderer<BottlerTileEntity>
{
    public BottlerTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public void render(BottlerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean hasBottle = tileEntityIn.getBlockState().get(Bottler.HAS_BOTTLE);
        if (hasBottle) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(Items.GLASS_BOTTLE), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
    }
}