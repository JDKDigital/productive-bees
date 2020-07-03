package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.tileentity.BottlerTileEntity;
import cy.jdkdigital.productivebees.tileentity.InventoryHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

@OnlyIn(Dist.CLIENT)
public class BottlerTileEntityRenderer extends TileEntityRenderer<BottlerTileEntity>
{
    public BottlerTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public void render(BottlerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        tileEntityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
            ItemStack stack = itemHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
            if (!stack.isEmpty()) {
                matrixStackIn.push();
                matrixStackIn.translate(0.5D, 0.9375D, 0.5D);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.scale(0.375F, 0.375F, 0.375F);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.pop();
            }
        });
    }
}