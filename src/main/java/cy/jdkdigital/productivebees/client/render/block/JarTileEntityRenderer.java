package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.JarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class JarTileEntityRenderer extends TileEntityRenderer<JarTileEntity>
{
    public JarTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public void render(JarTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        tileEntityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            if (!handler.getStackInSlot(0).isEmpty()) {
                ItemStack cage = handler.getStackInSlot(0);
                if (cage.getItem() instanceof BeeCage && BeeCage.isFilled(cage)) {
                    Entity bee = tileEntityIn.getCachedEntity(cage);
                    if (bee instanceof BeeEntity) {
                        bee.ticksExisted = ++tileEntityIn.ticksExisted;
                        ((BeeEntity) bee).renderYawOffset = -20;

                        float angle = bee.ticksExisted % 360;

                        float f = 0.47F;
                        float f1 = Math.max(bee.getWidth(), bee.getHeight());
                        if ((double)f1 > 1.0D) {
                            f /= f1;
                        }

                        matrixStack.push();
                        matrixStack.translate(0.5f, 0.4f, 0.5f);
                        matrixStack.rotate(Vector3f.YP.rotationDegrees(angle));
                        matrixStack.translate(0.0f, -0.2f, 0.0f);
                        matrixStack.scale(f, f, f);

                        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
                        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                        entityrenderermanager.setRenderShadow(false);
                        entityrenderermanager.renderEntityStatic(bee, 0, 0, 0., Minecraft.getInstance().getRenderPartialTicks(), 1, matrixStack, buffer, 15728880);
                        buffer.finish();

                        matrixStack.pop();
                    }
                }
            }
        });
    }
}