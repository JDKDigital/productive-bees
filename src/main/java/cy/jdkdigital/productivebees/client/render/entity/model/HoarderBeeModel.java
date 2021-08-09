package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.HoarderBee;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class HoarderBeeModel<T extends ProductiveBee> extends ProductiveBeeModel<T>
{
    public HoarderBeeModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = MediumBeeModel.createMeshDefinition(false);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.getChild(ProductiveBeeModel.BONE);

        PartDefinition body = bone.addOrReplaceChild(
                ProductiveBeeModel.BODY,
                CubeListBuilder
                        .create()
                        .texOffs(3, 3)
                        .addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F),
                PartPose.ZERO);

        body.addOrReplaceChild(
                ProductiveBeeModel.EXTERNALS,
                CubeListBuilder
                        .create()
                        .texOffs(38, 7)
                        .addBox(-3.5F, -4.0F, 1.0F, 7.0F, 7.0F, 4.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        assert entity instanceof HoarderBee;

        HoarderBee beeEntity = (HoarderBee) entity;

        float time = ageInTicks - (float) beeEntity.tickCount;
        float peekAmount = (0.5F + beeEntity.getClientPeekAmount(time)) * 3.1415927F;
        float rotation = -1.0F + Mth.sin(peekAmount);

        externals.setPos(0.0F, 0.0F, 3.0F + Mth.sin(peekAmount) * 3.0F);
        stinger.setPos(0.0F, 0.0F, 3.0F + Mth.sin(peekAmount) * 3.0F);
        if (beeEntity.getClientPeekAmount(time) > 0.3F) {
            externals.zRot = rotation * rotation * rotation * rotation * 3.1415927F * 0.125F;
        } else {
            externals.zRot = 0.0F;
        }
        stinger.zRot = externals.zRot;
    }
}
