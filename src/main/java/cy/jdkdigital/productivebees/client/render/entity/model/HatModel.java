package cy.jdkdigital.productivebees.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class HatModel<T extends ProductiveBeeEntity> extends EntityModel<T>
{
    protected final ModelRenderer santaHat;

    public HatModel() {
        super(RenderType::getEntitySolid);

        santaHat = new ModelRenderer(this);
        addSantaHat();
    }

    @Override
    public void setRotationAngles(T t, float v, float v1, float v2, float v3, float v4) {

    }

    public void setdRotationAngles(T entity, float v1, float v2, float v3, float v4, float v5) {
        this.santaHat.rotateAngleX = 0.0F;
        this.santaHat.rotationPointY = 19.0F;
        boolean flag = entity.onGround && entity.getMotion().lengthSquared() < 1.0E-7D;
        if (!flag) {
            this.santaHat.rotateAngleX = 0.0F;
            this.santaHat.rotateAngleY = 0.0F;
            this.santaHat.rotateAngleZ = 0.0F;
        }

        float lvt_8_2_;
        if (!entity.isAngry()) {
            this.santaHat.rotateAngleX = 0.0F;
            this.santaHat.rotateAngleY = 0.0F;
            this.santaHat.rotateAngleZ = 0.0F;
            if (!flag) {
                lvt_8_2_ = MathHelper.cos(v3 * 0.18F);
                this.santaHat.rotateAngleX = 0.1F + lvt_8_2_ * 3.1415927F * 0.025F;
                this.santaHat.rotationPointY = 19.0F - MathHelper.cos(v3 * 0.18F) * 0.9F;
            }
        }

//        if (this.bodyPitch > 0.0F) {
//            this.hat.rotateAngleX = ModelUtils.func_228283_a_(this.hat.rotateAngleX, 3.0915928F, this.bodyPitch);
//        }

    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {

    }

    private void addSantaHat() {
        santaHat.setRotationPoint(0.0F, 24.0F, 0.0F);
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
        box4.setTextureOffset(45, 60).addBox(-2.0F, -18.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        box4.setTextureOffset(18, 60).addBox(-2.5F, -19.5F, -4.4224F, 2.0F, 2.0F, 2.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
