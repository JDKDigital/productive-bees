package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class AmberBlockEntityRenderer implements BlockEntityRenderer<AmberBlockEntity>
{
    public AmberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AmberBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Entity entity = tileEntityIn.getCachedEntity();
        if (entity != null) {
            renderEntity(tileEntityIn, entity, matrixStack, combinedLightIn);
        }
    }

    public static void renderEntity(AmberBlockEntity tileEntityIn, Entity entity, PoseStack matrixStack, int combinedLightIn) {
        float angle = 0;
        if (tileEntityIn.getLevel() != null) {
            Direction facing = tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            if (facing == Direction.NORTH) {
                angle = 180f;
            } else if (facing == Direction.SOUTH) {
                angle = 0f;
            } else if (facing == Direction.EAST) {
                angle = 90f;
            } else if (facing == Direction.WEST) {
                angle = 270f;
            }
        }

        float f = 0.72F;
        float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if ((double) f1 > 1.5D) {
            f /= (f1/2);
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(angle));
        matrixStack.scale(f, f, f);

        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        entityRenderDispatcher.setRenderShadow(false);
        entityRenderDispatcher.render(entity, 0, 0, 0., Minecraft.getInstance().getFrameTime(), 1, matrixStack, buffer, combinedLightIn);
        buffer.endBatch();

        matrixStack.popPose();
    }
}