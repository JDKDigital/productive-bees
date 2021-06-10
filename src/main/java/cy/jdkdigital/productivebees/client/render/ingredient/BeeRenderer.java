package cy.jdkdigital.productivebees.client.render.ingredient;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class BeeRenderer
{
    private BeeRenderer() {
    }

    public static void render(MatrixStack matrixStack, int xPosition, int yPosition, BeeIngredient beeIngredient, Minecraft minecraft) {
        if (ProductiveBeesConfig.CLIENT.renderBeeIngredientAsEntity.get()) {
            BeeEntity bee = beeIngredient.getCachedEntity(minecraft.level);

            if (minecraft.player != null && bee != null) {
                if (bee instanceof ConfigurableBeeEntity) {
                    ((ConfigurableBeeEntity) bee).setBeeType(beeIngredient.getBeeType().toString());
                }

                if (bee instanceof ProductiveBeeEntity) {
                    ((ProductiveBeeEntity) bee).setRenderStatic();
                }

                bee.tickCount = minecraft.player.tickCount;
                bee.yBodyRot = -20;

                float scaledSize = 18;

                matrixStack.pushPose();
                matrixStack.translate(7D + xPosition, 12D + yPosition, 1.5);
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(190.0F));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(20.0F));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(scaledSize, scaledSize, scaledSize);

                EntityRendererManager entityrenderermanager = minecraft.getEntityRenderDispatcher();
                IRenderTypeBuffer.Impl buffer = minecraft.renderBuffers().bufferSource();
                entityrenderermanager.render(bee, 0, 0, 0.0D, minecraft.getFrameTime(), 1, matrixStack, buffer, 15728880);
                buffer.endBatch();
                matrixStack.popPose();
            }
        }
        else {
            renderBeeFace(xPosition, yPosition, beeIngredient, minecraft.level);
        }
    }

    private static void renderBeeFace(int xPosition, int yPosition, BeeIngredient beeIngredient, World world) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        ResourceLocation resLocation = getBeeTexture(beeIngredient, world);
        Minecraft.getInstance().getTextureManager().getTexture(resLocation);

        float[] color = colorCache.get(beeIngredient.getBeeType().toString());

        float scale = 1F / 128F;
        float iconX = 14F;
        float iconY = 14F;
        float iconU = 20F;
        float iconV = 20F;

        if (color == null) {
            color = new float[]{1.0f, 1.0f, 1.0f};
        }
        RenderSystem.color4f(color[0], color[1], color[2], 1.0f);
        BufferBuilder renderBuffer = Tessellator.getInstance().getBuilder();

        renderBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderBuffer.vertex(xPosition, yPosition + iconY, 0D).uv((iconU) * scale, (iconV + iconY) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
        renderBuffer.vertex(xPosition + iconX, yPosition + iconY, 0D).uv((iconU + iconX) * scale, (iconV + iconY) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
        renderBuffer.vertex(xPosition + iconX, yPosition, 0D).uv((iconU + iconX) * scale, (iconV) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
        renderBuffer.vertex(xPosition, yPosition, 0D).uv((iconU) * scale, (iconV) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();

        Tessellator.getInstance().end();

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    private static HashMap<String, ResourceLocation> beeTextureLocations = new HashMap<>();
    private static HashMap<String, float[]> colorCache = new HashMap<>();

    public static ResourceLocation getBeeTexture(@Nonnull BeeIngredient ingredient, World world) {
        String beeId = ingredient.getBeeType().toString();
        if (beeTextureLocations.get(beeId) != null) {
            return beeTextureLocations.get(beeId);
        }

        BeeEntity bee = ingredient.getCachedEntity(world);
        if (bee != null) {
            if (bee instanceof ConfigurableBeeEntity) {
                ((ConfigurableBeeEntity) bee).setBeeType(ingredient.getBeeType().toString());
                colorCache.put(beeId, ((ConfigurableBeeEntity) bee).getColor(0).getComponents(null));
            }

            EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super BeeEntity> renderer = manager.getRenderer(bee);

            ResourceLocation resource = renderer.getTextureLocation(bee);
            beeTextureLocations.put(beeId, resource);

            return beeTextureLocations.get(beeId);
        }
        return new ResourceLocation("textures/entity/bee/bee.png");
    }
}
