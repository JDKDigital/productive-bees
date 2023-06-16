package cy.jdkdigital.productivebees.client.render.ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

public class BeeRenderer
{
    public static void render(GuiGraphics guiGraphics, BeeIngredient beeIngredient, Minecraft minecraft) {
        render(guiGraphics, 0, 0, beeIngredient, minecraft);
    }

    public static void render(GuiGraphics guiGraphics, int xPosition, int yPosition, BeeIngredient beeIngredient, Minecraft minecraft) {
        Entity bee = beeIngredient.getCachedEntity(minecraft.level);

        if (minecraft.player != null && bee != null) {
            if (bee instanceof ConfigurableBee) {
                ((ConfigurableBee) bee).setBeeType(beeIngredient.getBeeType().toString());
            }

            if (bee instanceof ProductiveBee) {
                ((ProductiveBee) bee).setRenderStatic();
            }

            bee.tickCount = minecraft.player.tickCount;
            bee.setYBodyRot(-20);

            float scaledSize = 18;

            PoseStack postStack = guiGraphics.pose();
            postStack.pushPose();
            postStack.translate(7D + xPosition, 12D + yPosition, 1.5);
            postStack.mulPose(Axis.ZP.rotationDegrees(190.0F));
            postStack.mulPose(Axis.YP.rotationDegrees(20.0F));
            postStack.mulPose(Axis.XP.rotationDegrees(20.0F));
            postStack.translate(0.0F, -0.2F, 1);
            postStack.scale(scaledSize, scaledSize, scaledSize);

            EntityRenderDispatcher entityRendererManager = minecraft.getEntityRenderDispatcher();
            MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
            entityRendererManager.render(bee, 0, 0, 0.0D, minecraft.getFrameTime(), 1, postStack, buffer, 15728880);
            buffer.endBatch();
            postStack.popPose();
        }
    }
}
