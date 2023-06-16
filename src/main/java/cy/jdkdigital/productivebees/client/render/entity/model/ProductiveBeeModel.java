package cy.jdkdigital.productivebees.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ProductiveBeeModel<T extends ProductiveBee> extends AgeableListModel<T>
{
    protected float FAKE_PI = 3.1415927F;
    public static final String BONE = "bone";
    public static final String BODY = "body";
    public static final String STINGER = "stinger";
    public static final String LEFT_ANTENNA = "left_antenna";
    public static final String RIGHT_ANTENNA = "right_antenna";
    public static final String RIGHT_WING = "right_wing";
    public static final String LEFT_WING = "left_wing";
    public static final String FRONT_LEGS = "front_legs";
    public static final String MIDDLE_LEGS = "middle_legs";
    public static final String BACK_LEGS = "back_legs";
    public static final String EXTERNALS = "externals";
    public static final String INNARDS = "innards";
    public static final String SANTA_HAT = "santa_hat";
    protected ModelPart bone;
    protected ModelPart body;
    protected ModelPart rightWing;
    protected ModelPart leftWing;
    protected ModelPart frontLegs;
    protected ModelPart middleLegs;
    protected ModelPart backLegs;
    protected ModelPart stinger;
    protected ModelPart leftAntenna;
    protected ModelPart rightAntenna;
    protected ModelPart externals;
    protected ModelPart innards;
    protected ModelPart santaHat;
    protected PartialBeeModel partialModel;
    protected float rollAmount;

    public float beeSize = 1.0f;

    public ProductiveBeeModel(ModelPart modelPart) {
        this(modelPart, false, 24.0F, 0.0F);
    }

    public ProductiveBeeModel(ModelPart modelPart, boolean isChildHeadScaled, float childHeadOffsetY, float childHeadOffsetZ) {
        super(isChildHeadScaled, childHeadOffsetY, childHeadOffsetZ);

        bone = modelPart.getChild(BONE);
        body = bone.getChild(BODY);
        stinger = body.getChild(STINGER);
        leftAntenna = body.getChild(LEFT_ANTENNA);
        rightAntenna = body.getChild(RIGHT_ANTENNA);
        rightWing = bone.getChild(RIGHT_WING);
        leftWing = bone.getChild(LEFT_WING);
        frontLegs = bone.getChild(FRONT_LEGS);
        middleLegs = bone.getChild(MIDDLE_LEGS);
        backLegs = bone.getChild(BACK_LEGS);
        externals = body.getChild(EXTERNALS);
        innards = body.getChild(INNARDS);
        santaHat = body.getChild(SANTA_HAT);
    }
    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMeshDefinition(), 64, 64);
    }

    protected static MeshDefinition createMeshDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition bone = root.addOrReplaceChild(ProductiveBeeModel.BONE, CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition body = bone.addOrReplaceChild(ProductiveBeeModel.BODY, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.STINGER, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.LEFT_ANTENNA, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.RIGHT_ANTENNA, CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild(ProductiveBeeModel.RIGHT_WING, CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild(ProductiveBeeModel.LEFT_WING, CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild(ProductiveBeeModel.FRONT_LEGS, CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild(ProductiveBeeModel.MIDDLE_LEGS, CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild(ProductiveBeeModel.BACK_LEGS, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.INNARDS, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.EXTERNALS, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.SANTA_HAT, CubeListBuilder.create(), PartPose.ZERO);

        return meshDefinition;
    }

    public ProductiveBeeModel(ModelPart modelPart, String modelType) {
        this(modelPart, false, 24.0F, 0.0F);

        partialModel = switch (modelType) {
            case "thicc" -> new ThiccBeeModel(modelPart);
            case "small" -> new SmallBeeModel(modelPart);
            case "slim" -> new SlimBeeModel(modelPart);
            case "tiny" -> new TinyBeeModel(modelPart);
            case "elvis" -> new MediumElvisBeeModel(modelPart);
            case "default_shell" -> new MediumShellBeeModel(modelPart);
            case "default_foliage" -> new MediumFoliageBeeModel(modelPart);
            case "default_crystal" -> new MediumCrystalBeeModel(modelPart);
            case "translucent_with_center" -> new SlimyBeeModel(modelPart);
            default -> new MediumBeeModel(modelPart);
        };
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        rollAmount = entity.getRollAmount(partialTicks);
        stinger.visible = !entity.hasStung();
        if (entity instanceof ConfigurableBee && ((ConfigurableBee) entity).isStingless()) {
            stinger.visible = false;
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftAntenna.xRot = 0.0F;
        rightAntenna.xRot = 0.0F;
        bone.xRot = 0.0F;
        bone.y = 19.0F;
        boolean grounded = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7D;
        if (grounded) {
            setRotationAngle(rightWing, 0, -0.2618F, 0);
            setRotationAngle(leftWing, 0, 0.2618F, 0);
            frontLegs.xRot = 0.0F;
            middleLegs.xRot = 0.0F;
            backLegs.xRot = 0.0F;
        }
        else {
            // maxSpeed - (sizeMod - minSize)/(magetXSize() - minSize) * (maxSpeed - minSpeed)
            setRotationAngle(rightWing, 0, 0, Mth.cos(ageInTicks % 98000 * 2.1F) * FAKE_PI * 0.15F);
            setRotationAngle(leftWing, rightWing.xRot, rightWing.yRot, -rightWing.zRot);
            frontLegs.xRot = 0.7853982F;
            middleLegs.xRot = 0.7853982F;
            backLegs.xRot = 0.7853982F;
            setRotationAngle(bone, 0, 0, 0);
        }

        if (!entity.isAngry()) {
            bone.xRot = 0.0F;
            bone.yRot = 0.0F;
            bone.zRot = 0.0F;
            if (!grounded) {
                float angle = Mth.cos(ageInTicks * 0.18F);
                bone.xRot = 0.1F + angle * FAKE_PI * 0.025F;
                leftAntenna.xRot = angle * FAKE_PI * 0.03F;
                rightAntenna.xRot = angle * FAKE_PI * 0.03F;
                frontLegs.xRot = -angle * FAKE_PI * 0.1F + 0.3926991F;
                if (!entity.getRenderer().equals("thicc")) {
                    backLegs.xRot = -angle * FAKE_PI * 0.05F + 0.7853982F;
                }
                bone.y = 19.0F - angle * 0.9F;
            }
        }

        if (rollAmount > 0.0F) {
            bone.xRot = ModelUtils.rotlerpRad(bone.xRot, 3.0915928F, rollAmount);
        }

        beeSize = entity.getSizeModifier();

        if (young) {
            beeSize /= 2;
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(bone);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer renderBuffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 1.5 - beeSize * 1.5, 0);
        matrixStackIn.scale(beeSize, beeSize, beeSize);
        super.renderToBuffer(matrixStackIn, renderBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
