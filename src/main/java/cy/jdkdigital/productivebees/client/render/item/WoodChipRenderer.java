package cy.jdkdigital.productivebees.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WoodChipRenderer extends ItemStackTileEntityRenderer
{
    private final TridentModel trident = new TridentModel();

    @Override
    public void render(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228364_4_, int p_228364_5_) {
        Item item = itemStack.getItem();
        ProductiveBees.LOGGER.info("WoodChipRenderer render " + item);
        if (item == ModItems.WOOD_CHIP.get()) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);

            IVertexBuilder lvt_7_3_ = ItemRenderer.getBuffer(renderTypeBuffer, this.trident.getRenderType(TridentModel.TEXTURE_LOCATION), false, itemStack.hasEffect());
            this.trident.render(matrixStack, lvt_7_3_, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);

            matrixStack.pop();
        }
    }
}
