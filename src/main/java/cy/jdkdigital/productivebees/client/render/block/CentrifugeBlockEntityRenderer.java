package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.block.state.CentrifugeRenderState;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper.BlockEntityItemStackHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nonnull;

public class CentrifugeBlockEntityRenderer implements BlockEntityRenderer<CentrifugeBlockEntity, CentrifugeRenderState>
{
    private final ItemModelResolver itemModelResolver;

    public CentrifugeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CentrifugeRenderState createRenderState() {
        return new CentrifugeRenderState();
    }

    @Override
    public void extractRenderState(CentrifugeBlockEntity be, CentrifugeRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.hasComb = false;
        state.stackCount = 0;
        if (ProductiveBeesConfig.CLIENT.renderCombsInCentrifuge.get() && be.getLevel() != null) {
            var invHandler = be.getLevel().getCapability(Capabilities.Item.BLOCK, be.getBlockPos(), null);
            if (invHandler instanceof BlockEntityItemStackHandler stackHandler) {
                ItemStack stack = stackHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                if (!stack.isEmpty()) {
                    state.hasComb = true;
                    state.stackCount = stack.getCount();
                    this.itemModelResolver.updateForTopItem(state.combStackState, stack, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
                }
            }
            state.animationTime = (float) Math.floorMod(be.getLevel().getGameTime(), 360L) + partialTick;
        }
    }

    @Override
    public void submit(CentrifugeRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.hasComb) {
            double d = state.animationTime;
            double shownItemCount = state.stackCount < 20 ? state.stackCount : 20 + Math.ceil((state.stackCount - 20) / 4F);
            for (int i = 0; i < shownItemCount; ++i) {
                double angle = -d + 360D / shownItemCount * i;
                double dX = Math.sin(Math.toRadians(angle)) * 0.25D;
                double dZ = Math.cos(Math.toRadians(angle)) * 0.25D;

                poseStack.pushPose();
                poseStack.translate(0.5D + dX, 0.6375D, 0.5D + dZ);
                poseStack.mulPose(Axis.YP.rotationDegrees((float) angle + 90F));
                poseStack.scale(0.35F, 0.35F, 0.35F);
                state.combStackState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }
}
