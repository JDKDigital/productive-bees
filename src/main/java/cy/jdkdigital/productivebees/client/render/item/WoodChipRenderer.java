package cy.jdkdigital.productivebees.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.item.model.WoodChipModel;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.item.WoodChip;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WoodChipRenderer extends ItemStackTileEntityRenderer
{
    private static HashMap<String, ResourceLocation> woodTextureLocations = new HashMap<>();
    private final WoodChipModel model = new WoodChipModel();

    @Override
    public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228364_4_, int p_228364_5_) {
        Item item = itemStack.getItem();
        if (item == ModItems.WOOD_CHIP.get()) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);

            IVertexBuilder vertexBuilder = ItemRenderer.getBuffer(renderTypeBuffer, this.model.getRenderType(WoodChipModel.TEXTURE_LOCATION), false, itemStack.hasEffect());

            this.model.render(matrixStack, vertexBuilder, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);

            Block woodBlock = WoodChip.getWoodBlock(itemStack);

            if (woodBlock != null && woodBlock != Blocks.AIR) {
                ProductiveBees.LOGGER.info(getWoodTexture(woodBlock));

//                vertexBuilder.
            }

            matrixStack.pop();
        }
    }

    private static ResourceLocation getWoodTexture(@Nonnull Block block) {
        String blockId = block.toString();
//        if (woodTextureLocations.get(blockId) != null) {
//            return woodTextureLocations.get(blockId);
//        }

        BlockRendererDispatcher manager = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel model = manager.getModelForState(block.getDefaultState());

        List<BakedQuad> quads = model.getQuads(block.getDefaultState(), Direction.NORTH, new Random());
        ProductiveBees.LOGGER.info("sprite: " + quads.iterator().next().func_187508_a());
//        ResourceLocation resource = quads.iterator().next().func_187508_a().getName();
//
//        woodTextureLocations.put(blockId, resource);

        return woodTextureLocations.get(blockId);
    }
}
