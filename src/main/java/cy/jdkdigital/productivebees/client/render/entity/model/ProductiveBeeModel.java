package cy.jdkdigital.productivebees.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
    protected ModelRenderer innards;
    protected ModelRenderer santaHat;
    protected float bodyPitch;

    private float beeSize = 1.0f;

    public ProductiveBeeModel() {
        this(true);
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
        innards = new ModelRenderer(this);
        santaHat = new ModelRenderer(this);
    }

    public ProductiveBeeModel(String modelType) {
        this(false, 24.0F, 0.0F);

        PartialBeeModel partialModel;

        switch (modelType) {
            case "thicc":
                partialModel = new ThiccBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
                break;
            case "small":
                partialModel = new SmallBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
                break;
            case "slim":
                partialModel = new SlimBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
                break;
            case "tiny":
                partialModel = new TinyBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
                break;
            case "translucent_with_center":
            case "default":
            default:
                partialModel = new MediumBeeModel(this, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
        }

        partialModel.addBodyParts(true);
    }

    public ProductiveBeeModel(boolean addBodyParts) {
        // 24 is childHeadOffsetY
        this(false, 24.0F, 0.0F);
//
//        if (addBodyParts) {
//            addBodyParts(true);
//        }
    }

    public void setLivingAnimations(T entity, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
        super.setLivingAnimations(entity, p_212843_2_, p_212843_3_, p_212843_4_);
        bodyPitch = entity.getBodyPitch(p_212843_4_);
        stinger.showModel = !entity.hasStung();
    }

    public void setRotationAngles(T entity, float p_225597_2_, float p_225597_3_, float ageInTicks, float p_225597_5_, float p_225597_6_) {
        leftAntenna.rotateAngleX = 0.0F;
        rightAntenna.rotateAngleX = 0.0F;
        body.rotateAngleX = 0.0F;
        body.rotationPointY = 19.0F;
        boolean grounded = entity.onGround && entity.getMotion().lengthSquared() < 1.0E-7D;
        if (grounded) {
            setRotationAngle(rightWing, 0, -0.2618F, 0);
            setRotationAngle(leftWing, 0, 0.2618F, 0);
            frontLegs.rotateAngleX = 0.0F;
            middleLegs.rotateAngleX = 0.0F;
            backLegs.rotateAngleX = 0.0F;
        } else {
            // maxSpeed - (sizeMod - minSize)/(maxSize - minSize) * (maxSpeed - minSpeed)
            setRotationAngle(rightWing, 0, 0, MathHelper.cos(ageInTicks % 98000 * 2.1F) * FAKE_PI * 0.15F);
            setRotationAngle(leftWing, rightWing.rotateAngleX, rightWing.rotateAngleY, -rightWing.rotateAngleZ);
            frontLegs.rotateAngleX = 0.7853982F;
            middleLegs.rotateAngleX = 0.7853982F;
            backLegs.rotateAngleX = 0.7853982F;
            setRotationAngle(body, 0, 0, 0);
        }

        if (!entity.isAngry()) {
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

    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

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
        addTorso(withTorso);
        addAntenna();
        addWings();
        addLegs();
        addInnards();
        addSantaHat();
    }

    protected void addTorso(boolean withTorso) {
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        if (withTorso) {
            torso.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        }
        stinger.setTextureOffset(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        torso.addChild(stinger);
    }

    protected void addAntenna() {
        leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        leftAntenna.setTextureOffset(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        rightAntenna.setTextureOffset(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    protected void addWings() {
        rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        setRotationAngle(rightWing, 0.0F, -0.2617999870103947F, 0.0F);
        rightWing.setTextureOffset(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        setRotationAngle(leftWing, 0.0F, 0.2617999870103947F, 0.0F);
        leftWing.mirror = true;
        leftWing.setTextureOffset(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        body.addChild(rightWing);
        body.addChild(leftWing);
    }

    protected void addLegs() {
        frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        frontLegs.setTextureOffset(26, 1).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);
        middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        middleLegs.setTextureOffset(26, 3).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);
        backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        backLegs.setTextureOffset(26, 5).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        body.addChild(backLegs);
    }

    protected void addInnards() {
        innards.setRotationPoint(0.0F, 0.0F, 0.0F);
        innards.setTextureOffset(34, 0).addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
        body.addChild(innards);
    }

    protected void addSantaHat() {
        santaHat.setRotationPoint(.5F, 5.0F, 0.0F);
        santaHat.setTextureOffset(0, 54).addBox(-5.0F, -10.0F, -6.0F, 9.0F, 1.0F, 9.0F, 0.0F, false);
        santaHat.setTextureOffset(36, 54).addBox(-4.0F, -13.0F, -5.0F, 7.0F, 3.0F, 7.0F, 0.0F, false);

        ModelRenderer box2 = new ModelRenderer(this);
        box2.setRotationPoint(7.0F, 0.0F, 0.0F);
        santaHat.addChild(box2);
        setRotationAngle(box2, 0.1309F, 0.1309F, 0.0F);
        box2.setTextureOffset(39, 54).addBox(-10.0F, -16.0F, -3.5F, 5.0F, 4.0F, 5.0F, 0.0F, false);

        ModelRenderer box3 = new ModelRenderer(this);
        box3.setRotationPoint(2.0F, 2.0F, 3.0F);
        santaHat.addChild(box3);
        setRotationAngle(box3, 0.3054F, 0.0873F, 0.0436F);
        box3.setTextureOffset(41, 58).addBox(-5.0F, -20.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);

        ModelRenderer box4 = new ModelRenderer(this);
        box4.setRotationPoint(0.0F, -3.0F, 7.0F);
        santaHat.addChild(box4);
        setRotationAngle(box4,0.3927F, 0.0F, 0.0F);
        box4.setTextureOffset(18, 60).addBox(-2.5F, -18.5F, -4.4224F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        torso.addChild(santaHat);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
