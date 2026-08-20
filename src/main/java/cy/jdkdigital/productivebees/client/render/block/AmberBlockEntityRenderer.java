package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.block.state.EntityHolderRenderState;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class AmberBlockEntityRenderer implements BlockEntityRenderer<AmberBlockEntity, EntityHolderRenderState>
{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public AmberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.entityRenderer();
    }

    @Override
    public EntityHolderRenderState createRenderState() {
        return new EntityHolderRenderState();
    }

    @Override
    public void extractRenderState(AmberBlockEntity be, EntityHolderRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.entityRenderState = null;
        if (ProductiveBeesConfig.CLIENT.renderEntitiesInAmber.get()) {
            Entity entity = be.getCachedEntity();
            if (entity != null) {
                state.entityRenderState = this.entityRenderDispatcher.extractEntity(entity, partialTick);

                float f = 0.72F;
                float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
                if (f1 > 1.5D) {
                    f /= (f1 / 2);
                }
                state.entityBbScale = f;

                Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                state.facingAngle = switch (facing) {
                    case NORTH -> 180f;
                    case EAST -> 90f;
                    case WEST -> 270f;
                    default -> 0f;
                };
            }
        }
    }

    @Override
    public void submit(EntityHolderRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.entityRenderState != null) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.facingAngle));
            poseStack.scale(state.entityBbScale, state.entityBbScale, state.entityBbScale);
            this.entityRenderDispatcher.submit(state.entityRenderState, cameraState, 0, 0, 0, poseStack, collector);
            poseStack.popPose();
        }
    }
}
