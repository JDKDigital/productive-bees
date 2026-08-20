package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestHelmetLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M>
{
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private static final BlockState NEST_STATE = Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.HONEY_LEVEL, 5);

    private final BlockModelResolver blockModelResolver;
    private final BlockModelRenderState renderState = new BlockModelRenderState();

    public BeeNestHelmetLayer(RenderLayerParent<S, M> parent, BlockModelResolver blockModelResolver) {
        super(parent);
        this.blockModelResolver = blockModelResolver;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
        if (state.headEquipment == null || !state.headEquipment.is(ModItems.BEE_NEST_DIAMOND_HELMET.get())) {
            return;
        }
        blockModelResolver.update(renderState, NEST_STATE, BLOCK_DISPLAY_CONTEXT);
        if (renderState.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        getParentModel().head.translateAndRotate(poseStack);
        poseStack.translate(0.0F, -0.3F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.825F, -0.825F, -0.825F);
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(5.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(1.0F));
        renderState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
