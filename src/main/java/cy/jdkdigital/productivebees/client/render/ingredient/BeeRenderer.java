package cy.jdkdigital.productivebees.client.render.ingredient;

import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Renders a {@link BeeIngredient}'s entity into a 16x16 GUI slot.
 * <p>
 * Mirrors {@code InventoryScreen.renderEntityInInventoryFollowsAngle} with zero mouse-angle so
 * the bee appears upright and facing the viewer: a Z=π rotation flips the entity into the
 * GUI's upright space, and the LivingEntityRenderState's bodyRot is pointed at the camera.
 */
public class BeeRenderer
{
    private static final Quaternionf ROTATION = new Quaternionf()
            .rotateZ((float) Math.toRadians(190.0))
            .rotateY((float) Math.toRadians(20.0))
            .rotateX((float) Math.toRadians(20.0));
    private static final Quaternionf CAMERA_ANGLE = new Quaternionf();

    public static void render(GuiGraphicsExtractor guiGraphics, BeeIngredient beeIngredient, Minecraft minecraft) {
        render(guiGraphics, 0, 0, beeIngredient, minecraft);
    }

    public static void render(GuiGraphicsExtractor guiGraphics, int xPosition, int yPosition, BeeIngredient beeIngredient, Minecraft minecraft) {
        if (beeIngredient == null || minecraft == null || minecraft.level == null) {
            return;
        }
        Entity entity = beeIngredient.getCachedEntity(minecraft.level);
        if (entity == null) {
            return;
        }
        if (minecraft.player != null) {
            entity.tickCount = minecraft.player.tickCount;
        }
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        EntityRenderState renderState = minecraft.getEntityRenderDispatcher().extractEntity(entity, partialTick);
        if (renderState instanceof LivingEntityRenderState living) {
            living.bodyRot = -120.0F;
            living.yRot = 0.0F;
            living.xRot = 0.0F;
            living.boundingBoxWidth = living.boundingBoxWidth / living.scale;
            living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
            living.scale = 1.0F;
        }

        float bbMax = Math.max(renderState.boundingBoxWidth, renderState.boundingBoxHeight);
        float scale = 16.0F / Math.max(bbMax, 0.001F) * 0.85F;

        Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F, 0.0F);

        guiGraphics.entity(renderState, scale, translation, ROTATION, CAMERA_ANGLE,
                xPosition, yPosition, xPosition + 16, yPosition + 16);
    }
}
