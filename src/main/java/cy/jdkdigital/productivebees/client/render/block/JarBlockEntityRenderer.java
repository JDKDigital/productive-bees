package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class JarBlockEntityRenderer implements BlockEntityRenderer<JarBlockEntity>
{
    public JarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(JarBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean shouldRender = ProductiveBeesConfig.CLIENT.renderBeesInJars.get();
        if (shouldRender) {
            if (blockEntity.inventoryHandler instanceof ItemStackHandler invHandler) {
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    ItemStack cage = invHandler.getStackInSlot(0).copy();
                    if (cage.getItem() instanceof BeeCage && BeeCage.isFilled(cage)) {
                        if (blockEntity.getCachedEntity(cage) instanceof Bee bee) {
                            renderBee(bee, partialTicks, matrixStack);
                        }
                    }
                }
            }
        }
    }

    public static void renderBee(Entity bee, float partialTicks, PoseStack matrixStack) {
        bee.tickCount = bee.tickCount + Math.min(2, Math.round(partialTicks/1.3f));
        ((Bee) bee).yBodyRot = -20;

        float angle = bee.tickCount % 360;

        float f = 0.47F;
        float f1 = Math.max(bee.getBbWidth(), bee.getBbHeight());
        if ((double) f1 > 1.0D) {
            f /= f1;
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.4f, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(angle));
        matrixStack.translate(0.0f, -0.2f, 0.0f);
        matrixStack.scale(f, f, f);

        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        entityrenderermanager.setRenderShadow(false);
        entityrenderermanager.render(bee, 0, 0, 0., Minecraft.getInstance().getFrameTimeNs(), 1, matrixStack, buffer, 15728880);
        buffer.endBatch();

        matrixStack.popPose();
    }
}