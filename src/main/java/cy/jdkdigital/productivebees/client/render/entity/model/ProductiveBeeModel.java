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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProductiveBeeModel<T extends ProductiveBeeEntity> extends AgeableModel<T>
{
    protected float FAKE_PI = 3.1415927F;
    protected final ModelRenderer body;
    protected final ModelRenderer torso;
    protected final ModelRenderer rightWing;
    protected final ModelRenderer leftWing;
    protected final ModelRenderer frontLegs;
    protected final ModelRenderer middleLegs;
    protected final ModelRenderer backLegs;
    protected final ModelRenderer stinger;
    protected final ModelRenderer leftAntenna;
    protected final ModelRenderer rightAntenna;
    protected final ModelRenderer innards;
    protected final ModelRenderer santaHat;
    protected float bodyPitch;

    private float beeSize = 1.0f;

    public ProductiveBeeModel() {
        this(true);
    }

    public ProductiveBeeModel(boolean addBodyParts) {
        super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.torso = new ModelRenderer(this, 0, 0);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.frontLegs = new ModelRenderer(this);
        this.middleLegs = new ModelRenderer(this);
        this.backLegs = new ModelRenderer(this);
        this.innards = new ModelRenderer(this, 34, 0);
        this.santaHat = new ModelRenderer(this);

        if (addBodyParts) {
            addBodyParts();
        }
    }

    protected void addBodyParts() {
        addBodyParts(true);
    }

    protected void addBodyParts(boolean withTorso) {
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        if (withTorso) {
            this.torso.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        }
        this.stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);

        addAntenna();
        addWings();
        addLegs();
        addInnards();
        addSantaHat();
    }

    public void setLivingAnimations(T entity, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
        super.setLivingAnimations(entity, p_212843_2_, p_212843_3_, p_212843_4_);
        this.bodyPitch = entity.getBodyPitch(p_212843_4_);
        this.stinger.showModel = !entity.hasStung();
    }

    public void setRotationAngles(T entity, float p_225597_2_, float p_225597_3_, float ageInTicks, float p_225597_5_, float p_225597_6_) {
        this.rightWing.rotateAngleX = 0.0F;
        this.leftAntenna.rotateAngleX = 0.0F;
        this.rightAntenna.rotateAngleX = 0.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotationPointY = 19.0F;
        boolean grounded = entity.onGround && entity.getMotion().lengthSquared() < 1.0E-7D;
        if (grounded) {
            this.rightWing.rotateAngleY = -0.2618F;
            this.rightWing.rotateAngleZ = 0.0F;
            this.leftWing.rotateAngleX = 0.0F;
            this.leftWing.rotateAngleY = 0.2618F;
            this.leftWing.rotateAngleZ = 0.0F;
            this.frontLegs.rotateAngleX = 0.0F;
            this.middleLegs.rotateAngleX = 0.0F;
            this.backLegs.rotateAngleX = 0.0F;
        } else {
            this.rightWing.rotateAngleY = 0.0F;
            // maxSpeed - (sizeMod - minSize)/(maxSize - minSize) * (maxSpeed - minSpeed)
            this.rightWing.rotateAngleZ = MathHelper.cos(ageInTicks % 98000 * 2.1F) * FAKE_PI * 0.15F;
            this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
            this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
            this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
            this.frontLegs.rotateAngleX = 0.7853982F;
            this.middleLegs.rotateAngleX = 0.7853982F;
            this.backLegs.rotateAngleX = 0.7853982F;
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
        }

        if (!entity.isAngry()) {
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
            if (!grounded) {
                float angle = MathHelper.cos(ageInTicks * 0.18F);
                this.body.rotateAngleX = 0.1F + angle * FAKE_PI * 0.025F;
                this.leftAntenna.rotateAngleX = angle * FAKE_PI * 0.03F;
                this.rightAntenna.rotateAngleX = angle * FAKE_PI * 0.03F;
                this.frontLegs.rotateAngleX = -angle * FAKE_PI * 0.1F + 0.3926991F;
                this.backLegs.rotateAngleX = -angle * FAKE_PI * 0.05F + 0.7853982F;
                this.body.rotationPointY = 19.0F - angle * 0.9F;
            }
        }

        if (this.bodyPitch > 0.0F) {
            this.body.rotateAngleX = ModelUtils.func_228283_a_(this.body.rotateAngleX, 3.0915928F, this.bodyPitch);
        }

        beeSize = 1.0f;
        if (entity instanceof ConfigurableBeeEntity) {
            beeSize = beeSize * ((ConfigurableBeeEntity) entity).getSizeModifier();
        }

        if (this.isChild) {
            beeSize /= 2;
        }
    }

    public ModelRenderer getBody() {
        return this.body;
    }

    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder renderBuffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        matrixStackIn.translate(0, 1.5 - beeSize * 1.5, 0);
        matrixStackIn.scale(beeSize, beeSize, beeSize);
        super.render(matrixStackIn, renderBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }

    private void addAntenna() {
        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
    }

    private void addWings() {
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.body.addChild(this.rightWing);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
    }

    private void addLegs() {
        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.addBox("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.addBox("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.addBox("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
    }

    private void addInnards() {
        this.innards.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.innards.addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
        this.body.addChild(this.innards);
    }

    private void addSantaHat() {
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

        this.torso.addChild(this.santaHat);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
