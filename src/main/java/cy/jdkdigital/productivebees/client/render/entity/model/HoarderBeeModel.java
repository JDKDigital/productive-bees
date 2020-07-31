package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.HoarderBeeEntity;
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

        body.setTextureOffset(3, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.0F);

        abdomen.setTextureOffset(38, 7).addBox(-3.5F, -4.0F, 1.0F, 7.0F, 7.0F, 4.0F, 0.0F);

        beeModel.addChild(abdomen);
    }

    public void setRotationAngles(T entity, float var2, float var3, float var4, float var5, float var6) {
        super.setRotationAngles(entity, var2, var3, var4, var5, var6);
        assert entity instanceof HoarderBeeEntity;

        HoarderBeeEntity beeEntity = (HoarderBeeEntity) entity;

        float time = var4 - (float)beeEntity.ticksExisted;
        float lvt_8_1_ = (0.5F + beeEntity.getClientPeekAmount(time)) * 3.1415927F;
        float lvt_9_1_ = -1.0F + MathHelper.sin(lvt_8_1_);

        abdomen.setRotationPoint(0.0F, 0.0F, 3.0F + MathHelper.sin(lvt_8_1_) * 3.0F);
        stinger.setRotationPoint(0.0F, 0.0F, 3.0F + MathHelper.sin(lvt_8_1_) * 3.0F);
        if (beeEntity.getClientPeekAmount(time) > 0.3F) {
            abdomen.rotateAngleZ = lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * 3.1415927F * 0.125F;
        } else {
            abdomen.rotateAngleZ = 0.0F;
        }
        stinger.rotateAngleZ = abdomen.rotateAngleZ;
    }
}
