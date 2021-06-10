package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.tileentity.JarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.CapabilityItemHandler;

public class JarTileEntityRenderer extends TileEntityRenderer<JarTileEntity>
{
    public JarTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(JarTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean shouldRender = ProductiveBeesConfig.CLIENT.renderBeesInJars.get();
        if (shouldRender) {
            tileEntityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                if (!handler.getStackInSlot(0).isEmpty()) {
                    ItemStack cage = handler.getStackInSlot(0);
                    if (cage.getItem() instanceof BeeCage && BeeCage.isFilled(cage)) {
                        Entity bee = tileEntityIn.getCachedEntity(cage);
                        if (bee instanceof BeeEntity) {
                            renderBee(bee, partialTicks, matrixStack);
                        }
                    }
                }
            });
        }
    }

    public static void renderBee(Entity bee, float partialTicks, MatrixStack matrixStack) {
        bee.tickCount = bee.tickCount + Math.round(partialTicks);
        ((BeeEntity) bee).yBodyRot = -20;

        float angle = bee.tickCount % 360;

        float f = 0.47F;
        float f1 = Math.max(bee.getBbWidth(), bee.getBbHeight());
        if ((double) f1 > 1.0D) {
            f /= f1;
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.4f, 0.5f);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
        matrixStack.translate(0.0f, -0.2f, 0.0f);
        matrixStack.scale(f, f, f);

        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        entityrenderermanager.setRenderShadow(false);
        entityrenderermanager.render(bee, 0, 0, 0., Minecraft.getInstance().getFrameTime(), 1, matrixStack, buffer, 15728880);
        buffer.endBatch();

        matrixStack.popPose();
    }
}