package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoarderBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T>
{
    protected final ModelRenderer abdomen;

    public HoarderBeeModel()  {
        super(false);

        abdomen = new ModelRenderer(this);

        addBodyParts(false);

        body.setTextureOffset(3, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.0F, false);

        abdomen.setTextureOffset(38, 0).addBox(-3.5F, -4.0F, 1.0F, 7.0F, 7.0F, 4.0F, 0.0F, false);

        beeModel.addChild(abdomen);
    }

    public void setRotationAngles(T beeEntity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        float lvt_7_1_ = p_225597_4_ - (float)beeEntity.ticksExisted;
        float lvt_8_1_ = (0.5F + beeEntity.getClientPeekAmount(lvt_7_1_)) * 3.1415927F;
        float lvt_9_1_ = -1.0F + MathHelper.sin(lvt_8_1_);
        float lvt_10_1_ = 0.0F;
        if (lvt_8_1_ > 3.1415927F) {
            lvt_10_1_ = MathHelper.sin(p_225597_4_ * 0.1F) * 0.7F;
        }

        this.lid.setRotationPoint(0.0F, 16.0F + MathHelper.sin(lvt_8_1_) * 8.0F + lvt_10_1_, 0.0F);
        if (beeEntity.getClientPeekAmount(lvt_7_1_) > 0.3F) {
            this.lid.rotateAngleY = lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * 3.1415927F * 0.125F;
        } else {
            this.lid.rotateAngleY = 0.0F;
        }

        this.head.rotateAngleX = p_225597_6_ * 0.017453292F;
        this.head.rotateAngleY = p_225597_5_ * 0.017453292F;
    }
}
