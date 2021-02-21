package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class GlowingInnardsLayer extends LayerRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>>
{
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/magmatic/innards.png"));
//    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/magmatic/spider_eyes.png"));
//    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation("textures/entity/spider_eyes.png"));
    private static Map<String, RenderType> renderTypeMap = new HashMap<>();

    public GlowingInnardsLayer(IEntityRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>> rendererIn) {
        super(rendererIn);
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, ProductiveBeeEntity bee, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (bee instanceof ConfigurableBeeEntity && ((ConfigurableBeeEntity) bee).hasGlowingInnards()) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(getRenderType((ConfigurableBeeEntity) bee));
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private RenderType getRenderType(ConfigurableBeeEntity bee) {
        return RENDER_TYPE;
//        if (!renderTypeMap.containsKey(bee.getBeeType())) {
//            ResourceLocation location = getEntityTexture(bee);
//            ResourceLocation innardsLocation = new ResourceLocation(location.getNamespace(), location.getPath().replaceAll("/bee[\\w_]*?\\.png", "\\/innards.png"));
//
//            renderTypeMap.put(bee.getBeeType(), RenderType.getEyes(innardsLocation));
//        }
//        return renderTypeMap.get(bee.getBeeType());
    }
}
