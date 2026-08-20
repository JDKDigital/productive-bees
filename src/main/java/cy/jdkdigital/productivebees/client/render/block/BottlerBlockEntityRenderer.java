package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.client.render.block.state.BottlerRenderState;
import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class BottlerBlockEntityRenderer implements BlockEntityRenderer<BottlerBlockEntity, BottlerRenderState>
{
    private final ItemModelResolver itemModelResolver;

    public BottlerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public BottlerRenderState createRenderState() {
        return new BottlerRenderState();
    }

    @Override
    public void extractRenderState(BottlerBlockEntity be, BottlerRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.hasBottle = be.getBlockState().getValue(Bottler.HAS_BOTTLE);
        if (state.hasBottle) {
            this.itemModelResolver.updateForTopItem(state.bottleStackState, new ItemStack(Items.GLASS_BOTTLE), ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(BottlerRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.hasBottle) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.0625D, 0.5D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.375F, 0.375F, 0.375F);
            state.bottleStackState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
