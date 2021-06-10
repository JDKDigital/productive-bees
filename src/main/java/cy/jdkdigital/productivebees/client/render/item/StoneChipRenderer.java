package cy.jdkdigital.productivebees.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.common.item.StoneChip;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StoneChipRenderer extends WoodChipRenderer
{
    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn, int packedUV) {
        Item item = itemStack.getItem();

        if (item instanceof StoneChip) {
            IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.cutout());

            Block block = StoneChip.getBlock(itemStack);
            if (block != null && block != Blocks.AIR) {
                TextureAtlasSprite sprite = getBlockSprite(block);

                if (sprite != null) {
                    matrixStack.pushPose();
                    matrixStack.translate(0.5, 0.5, 0.5);
                    if (!itemStack.isFramed()) {
                        matrixStack.scale(.5f, .5f, .5f);
                    }
                    matrixStack.translate(-.5, -.5, -1.0f);

                    // Hello visitor, welcome to render hell. If you have an idea for a better way to render a blocks texture
                    // onto an item by using a model instead of this mess, let me know.
                    addBox(builder, matrixStack, sprite, 6, 5, 11, 11);
                    addBox(builder, matrixStack, sprite, 7, 4, 10, 5);
                    addBox(builder, matrixStack, sprite, 5, 5, 6, 10);
                    addBox(builder, matrixStack, sprite, 4, 6, 5, 9);
                    addBox(builder, matrixStack, sprite, 11, 8, 12, 10);
                    addBox(builder, matrixStack, sprite, 6, 11, 9, 12);

                    matrixStack.popPose();
                }
            }
        }
    }
}
