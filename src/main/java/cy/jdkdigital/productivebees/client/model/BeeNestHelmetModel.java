package cy.jdkdigital.productivebees.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.util.Lazy;

public class BeeNestHelmetModel<T extends LivingEntity> extends HumanoidModel<T>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "bee_nest_diamond"), "main");
    public static final Lazy<HumanoidModel<?>> INSTANCE = Lazy.of(() -> new BeeNestHelmetModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(LAYER_LOCATION)));

    public BeeNestHelmetModel(ModelPart modelRoot) {
        super(modelRoot);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.5F), 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (ClientProxy.buffer != null) {
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
            BlockState nest = Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.HONEY_LEVEL, 5);
            poseStack.pushPose();
            head.translateAndRotate(poseStack);
            poseStack.translate(0.0D, -0.3D, 0.0D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.scale(0.825F, -0.825F, -0.825F);
            poseStack.translate(-0.5F, 0, -0.5F);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(5));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(1));
            blockRenderer.renderSingleBlock(nest, poseStack, ClientProxy.buffer, packedLight, packedOverlay, EmptyModelData.INSTANCE);
            poseStack.popPose();
        }
    }
}
