package cy.jdkdigital.productivebees.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProductiveBeeModel<T extends ProductiveBeeEntity> extends AgeableModel<T>
{
    protected final ModelRenderer beeModel;
    protected final ModelRenderer body;
    protected final ModelRenderer rightWing;
    protected final ModelRenderer leftWing;
    protected final ModelRenderer frontLegs;
    protected final ModelRenderer midLegs;
    protected final ModelRenderer backLegs;
    protected final ModelRenderer stinger;
    protected final ModelRenderer leftAntenae;
    protected final ModelRenderer rightAntenae;
    protected float field_228241_n_;

    public ProductiveBeeModel() {
        this(true);
    }

    public ProductiveBeeModel(boolean addBodyParts) {
        super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.beeModel = new ModelRenderer(this);
        this.body = new ModelRenderer(this, 0, 0);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.leftAntenae = new ModelRenderer(this, 2, 0);
        this.rightAntenae = new ModelRenderer(this, 2, 3);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.frontLegs = new ModelRenderer(this);
        this.midLegs = new ModelRenderer(this);
        this.backLegs = new ModelRenderer(this);

        if (addBodyParts) {
            addBodyParts();
        }
    }
    protected void addBodyParts() {
        addBodyParts(true);
    }

    protected void addBodyParts(boolean withBody) {
        this.beeModel.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.beeModel.addChild(this.body);
        if (withBody) {
            this.body.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        }
        this.stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.body.addChild(this.stinger);
        this.leftAntenae.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenae.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.rightAntenae.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenae.addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.body.addChild(this.leftAntenae);
        this.body.addChild(this.rightAntenae);
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.beeModel.addChild(this.rightWing);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.beeModel.addChild(this.leftWing);
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.beeModel.addChild(this.frontLegs);
        this.frontLegs.addBox("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
        this.midLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.beeModel.addChild(this.midLegs);
        this.midLegs.addBox("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.beeModel.addChild(this.backLegs);
        this.backLegs.addBox("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
    }

    public void setLivingAnimations(T entity, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
        super.setLivingAnimations(entity, p_212843_2_, p_212843_3_, p_212843_4_);
        this.field_228241_n_ = entity.getBodyPitch(p_212843_4_);
        this.stinger.showModel = !entity.hasStung();
    }

    public void setRotationAngles(T entity, float var2, float var3, float var4, float var5, float var6) {
        this.rightWing.rotateAngleX = 0.0F;
        this.leftAntenae.rotateAngleX = 0.0F;
        this.rightAntenae.rotateAngleX = 0.0F;
        this.beeModel.rotateAngleX = 0.0F;
        this.beeModel.rotationPointY = 19.0F;
        boolean lvt_7_1_ = entity.isOnGround() && entity.getMotion().lengthSquared() < 1.0E-7D;
        float lvt_8_2_;
        if (lvt_7_1_) {
            this.rightWing.rotateAngleY = -0.2618F;
            this.rightWing.rotateAngleZ = 0.0F;
            this.leftWing.rotateAngleX = 0.0F;
            this.leftWing.rotateAngleY = 0.2618F;
            this.leftWing.rotateAngleZ = 0.0F;
            this.frontLegs.rotateAngleX = 0.0F;
            this.midLegs.rotateAngleX = 0.0F;
            this.backLegs.rotateAngleX = 0.0F;
        }
        else {
            lvt_8_2_ = var4 * 2.1F;
            this.rightWing.rotateAngleY = 0.0F;
            this.rightWing.rotateAngleZ = MathHelper.cos(lvt_8_2_) * 3.1415927F * 0.15F;
            this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
            this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
            this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
            this.frontLegs.rotateAngleX = 0.7853982F;
            this.midLegs.rotateAngleX = 0.7853982F;
            this.backLegs.rotateAngleX = 0.7853982F;
            this.beeModel.rotateAngleX = 0.0F;
            this.beeModel.rotateAngleY = 0.0F;
            this.beeModel.rotateAngleZ = 0.0F;
        }

        if (!entity.func_233678_J__()) {
            this.beeModel.rotateAngleX = 0.0F;
            this.beeModel.rotateAngleY = 0.0F;
            this.beeModel.rotateAngleZ = 0.0F;
            if (!lvt_7_1_) {
                lvt_8_2_ = MathHelper.cos(var4 * 0.18F);
                this.beeModel.rotateAngleX = 0.1F + lvt_8_2_ * 3.1415927F * 0.025F;
                this.leftAntenae.rotateAngleX = lvt_8_2_ * 3.1415927F * 0.03F;
                this.rightAntenae.rotateAngleX = lvt_8_2_ * 3.1415927F * 0.03F;
                this.frontLegs.rotateAngleX = -lvt_8_2_ * 3.1415927F * 0.1F + 0.3926991F;
                this.backLegs.rotateAngleX = -lvt_8_2_ * 3.1415927F * 0.05F + 0.7853982F;
                this.beeModel.rotationPointY = 19.0F - MathHelper.cos(var4 * 0.18F) * 0.9F;
            }
        }

        if (this.field_228241_n_ > 0.0F) {
            this.beeModel.rotateAngleX = ModelUtils.func_228283_a_(this.beeModel.rotateAngleX, 3.0915928F, this.field_228241_n_);
        }

    }

    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.beeModel);
    }
}
