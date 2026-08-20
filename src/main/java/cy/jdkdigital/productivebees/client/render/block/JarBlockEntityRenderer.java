package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.block.state.EntityHolderRenderState;
import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class JarBlockEntityRenderer implements BlockEntityRenderer<JarBlockEntity, EntityHolderRenderState>
{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public JarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.entityRenderer();
    }

    @Override
    public EntityHolderRenderState createRenderState() {
        return new EntityHolderRenderState();
    }

    @Override
    public void extractRenderState(JarBlockEntity be, EntityHolderRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.entityRenderState = null;
        if (!ProductiveBeesConfig.CLIENT.renderBeesInJars.get()) return;

        Entity cached = be.getCachedEntity();
        if (cached == null) {
            if (be.inventoryHandler.getResource(0).isEmpty()) return;
            ItemStack cage = be.inventoryHandler.getStackInSlot(0);
            if (!(cage.getItem() instanceof BeeCage) || !BeeCage.isFilled(cage)) return;
            cached = be.getCachedEntity(cage);
        }
        if (!(cached instanceof Bee bee)) return;

        bee.yBodyRot = -20;
        bee.yBodyRotO = -20;
        bee.yHeadRot = -20;
        bee.yHeadRotO = -20;
        bee.setYRot(-20);
        bee.yRotO = -20;
        bee.setXRot(0);
        bee.xRotO = 0;
        long gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        bee.tickCount = (int) (gameTime & 0x7FFFFFFFL);
        float t = Math.floorMod(gameTime, 360L) + partialTick;
        state.facingAngle = (t + be.tickCount) % 360.0f;

        float f = 0.47F;
        float f1 = Math.max(bee.getBbWidth(), bee.getBbHeight());
        if (f1 > 1.0D) {
            f /= f1;
        }
        state.entityBbScale = f;
        state.entityRenderState = this.entityRenderDispatcher.extractEntity(bee, partialTick);
    }

    @Override
    public void submit(EntityHolderRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.entityRenderState != null) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.4f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.facingAngle));
            poseStack.translate(0.0f, -0.2f, 0.0f);
            poseStack.scale(state.entityBbScale, state.entityBbScale, state.entityBbScale);
            this.entityRenderDispatcher.submit(state.entityRenderState, cameraState, 0, 0, 0, poseStack, collector);
            poseStack.popPose();
        }
    }
}
