package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.hive.HoarderBeeEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class HoarderBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T>
{
    protected final ModelRenderer abdomen;

    public HoarderBeeModel() {
        super();

        abdomen = new ModelRenderer(this);

        addBodyParts(false);

        torso.setTextureOffset(3, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.0F);

        abdomen.setTextureOffset(38, 7).addBox(-3.5F, -4.0F, 1.0F, 7.0F, 7.0F, 4.0F, 0.0F);

        body.addChild(abdomen);
    }

    @Override
    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        assert entity instanceof HoarderBeeEntity;

        HoarderBeeEntity beeEntity = (HoarderBeeEntity) entity;

        float time = ageInTicks - (float) beeEntity.ticksExisted;
        float peekAmount = (0.5F + beeEntity.getClientPeekAmount(time)) * 3.1415927F;
        float lvt_9_1_ = -1.0F + MathHelper.sin(peekAmount);

        abdomen.setRotationPoint(0.0F, 0.0F, 3.0F + MathHelper.sin(peekAmount) * 3.0F);
        stinger.setRotationPoint(0.0F, 0.0F, 3.0F + MathHelper.sin(peekAmount) * 3.0F);
        if (beeEntity.getClientPeekAmount(time) > 0.3F) {
            abdomen.rotateAngleZ = lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * 3.1415927F * 0.125F;
        }
        else {
            abdomen.rotateAngleZ = 0.0F;
        }
        stinger.rotateAngleZ = abdomen.rotateAngleZ;
    }
}
