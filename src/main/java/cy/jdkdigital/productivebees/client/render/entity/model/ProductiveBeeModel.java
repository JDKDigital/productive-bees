package cy.jdkdigital.productivebees.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ProductiveBeeModel<T extends ProductiveBeeEntity> extends AgeableModel<T>
{
    protected float FAKE_PI = 3.1415927F;
    protected ModelRenderer body;
    protected ModelRenderer torso;
    protected ModelRenderer rightWing;
    protected ModelRenderer leftWing;
    protected ModelRenderer frontLegs;
    protected ModelRenderer middleLegs;
    protected ModelRenderer backLegs;
    protected ModelRenderer stinger;
    protected ModelRenderer leftAntenna;
    protected ModelRenderer rightAntenna;
    protected ModelRenderer crystals;
    protected ModelRenderer innards;
    protected ModelRenderer santaHat;
    protected float bodyPitch;

    public float beeSize = 1.0f;

    public ProductiveBeeModel() {
        // 24 is childHeadOffsetY
        this(false, 24.0F, 0.0F);
    }

    public ProductiveBeeModel(boolean isChildHeadScaled, float childHeadOffsetY, float childHeadOffsetZ) {
        super(isChildHeadScaled, childHeadOffsetY, childHeadOffsetZ);

        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this);
        torso = new ModelRenderer(this);
        stinger = new ModelRenderer(this);
        leftAntenna = new ModelRenderer(this);
        rightAntenna = new ModelRenderer(this);
        rightWing = new ModelRenderer(this);
        leftWing = new ModelRenderer(this);
        middleLegs = new ModelRenderer(this);
        frontLegs = new ModelRenderer(this);
        backLegs = new ModelRenderer(this);
        crystals = new ModelRenderer(this);
        innards = new ModelRenderer(this);
        santaHat = new ModelRenderer(this);
    }

    public ProductiveBeeModel(String modelType) {
        this(false, 24.0F, 0.0F);

        PartialBeeModel partialModel;

        switch (modelType) {
            case "thicc":
                partialModel = new ThiccBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "small":
                partialModel = new SmallBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "slim":
                partialModel = new SlimBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "tiny":
                partialModel = new TinyBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "default_shell":
                partialModel = new MediumShellBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "default_foliage":
                partialModel = new MediumFoliageBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "default_crystal":
                partialModel = new MediumCrystalBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "translucent_with_center":
                partialModel = new SlimyBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
                break;
            case "default":
            default:
                partialModel = new MediumBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
        }

        partialModel.addBodyParts(true);
    }

    @Override
    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        bodyPitch = entity.getBodyPitch(partialTicks);
        stinger.showModel = !entity.hasStung();
        if (entity instanceof ConfigurableBeeEntity && ((ConfigurableBeeEntity) entity).isStingless()) {
            stinger.showModel = false;
        }
    }

    @Override
    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftAntenna.rotateAngleX = 0.0F;
        rightAntenna.rotateAngleX = 0.0F;
        body.rotateAngleX = 0.0F;
        body.rotationPointY = 19.0F;
        boolean grounded = entity.isOnGround() && entity.getMotion().lengthSquared() < 1.0E-7D;
        if (grounded) {
            setRotationAngle(rightWing, 0, -0.2618F, 0);
            setRotationAngle(leftWing, 0, 0.2618F, 0);
            frontLegs.rotateAngleX = 0.0F;
            middleLegs.rotateAngleX = 0.0F;
            backLegs.rotateAngleX = 0.0F;
        }
        else {
            // maxSpeed - (sizeMod - minSize)/(maxSize - minSize) * (maxSpeed - minSpeed)
            setRotationAngle(rightWing, 0, 0, MathHelper.cos(ageInTicks % 98000 * 2.1F) * FAKE_PI * 0.15F);
            setRotationAngle(leftWing, rightWing.rotateAngleX, rightWing.rotateAngleY, -rightWing.rotateAngleZ);
            frontLegs.rotateAngleX = 0.7853982F;
            middleLegs.rotateAngleX = 0.7853982F;
            backLegs.rotateAngleX = 0.7853982F;
            setRotationAngle(body, 0, 0, 0);
        }

        if (!entity.func_233678_J__()) {
            body.rotateAngleX = 0.0F;
            body.rotateAngleY = 0.0F;
            body.rotateAngleZ = 0.0F;
            if (!grounded) {
                float angle = MathHelper.cos(ageInTicks * 0.18F);
                body.rotateAngleX = 0.1F + angle * FAKE_PI * 0.025F;
                leftAntenna.rotateAngleX = angle * FAKE_PI * 0.03F;
                rightAntenna.rotateAngleX = angle * FAKE_PI * 0.03F;
                frontLegs.rotateAngleX = -angle * FAKE_PI * 0.1F + 0.3926991F;
                backLegs.rotateAngleX = -angle * FAKE_PI * 0.05F + 0.7853982F;
                body.rotationPointY = 19.0F - angle * 0.9F;
            }
        }

        if (bodyPitch > 0.0F) {
            body.rotateAngleX = ModelUtils.func_228283_a_(body.rotateAngleX, 3.0915928F, bodyPitch);
        }

        beeSize = 1.0f;
        if (entity instanceof ConfigurableBeeEntity) {
            beeSize = beeSize * ((ConfigurableBeeEntity) entity).getSizeModifier();
        }

        if (isChild) {
            beeSize /= 2;
        }
    }

    public ModelRenderer getBody() {
        return body;
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(body);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder renderBuffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        matrixStackIn.translate(0, 1.5 - beeSize * 1.5, 0);
        matrixStackIn.scale(beeSize, beeSize, beeSize);
        super.render(matrixStackIn, renderBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }

    protected void addBodyParts(boolean withTorso) {
        PartialBeeModel partialModel = new MediumBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
        partialModel.addBodyParts(withTorso);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
