package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ThiccBeeModel extends PartialBeeModel
{
    public ThiccBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = ProductiveBeeModel.createMeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition bone = root.addOrReplaceChild(
                ProductiveBeeModel.BONE,
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 18.0F, 0.0F)
        );

        PartDefinition body = bone.addOrReplaceChild(
                ProductiveBeeModel.BODY,
                CubeListBuilder
                        .create().texOffs(3, 3)
                        .addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 5.0F),
                PartPose.ZERO
        );

        body.addOrReplaceChild(
                ProductiveBeeModel.STINGER,
                CubeListBuilder
                        .create().texOffs(26, 7)
                        .addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F),
                PartPose.ZERO
        );

        body.addOrReplaceChild(
                ProductiveBeeModel.LEFT_ANTENNA,
                CubeListBuilder
                        .create().texOffs(0, 0)
                        .addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, -2.0F, -5.0F)
        );
        body.addOrReplaceChild(
                ProductiveBeeModel.RIGHT_ANTENNA,
                CubeListBuilder
                        .create().texOffs(0, 18)
                        .addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(-1.5F, -4.0F, -3.0F)
        );

        CubeDeformation var5 = new CubeDeformation(0.001F);
        bone.addOrReplaceChild(
                ProductiveBeeModel.RIGHT_WING,
                CubeListBuilder
                        .create().texOffs(0, 18)
                        .addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var5),
                PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.3491F, -0.2618F, 0.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.LEFT_WING,
                CubeListBuilder
                        .create().texOffs(0, 18).mirror()
                        .addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var5),
                PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.3491F, 0.2618F, 0.0F)
        );

        bone.addOrReplaceChild(
                ProductiveBeeModel.FRONT_LEGS,
                CubeListBuilder
                        .create().texOffs(24, 1)
                        .addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F),
                PartPose.offset(1.5F, 3.0F, -3.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.MIDDLE_LEGS,
                CubeListBuilder
                        .create().texOffs(24, 3)
                        .addBox(-5.0F, -1.5F, -1.0F, 7.0F, 2.0F, 0.0F),
                PartPose.offset(1.5F, 4.0F, 1.0F)
        );
        bone.addOrReplaceChild(ProductiveBeeModel.BACK_LEGS, CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition externals = body.addOrReplaceChild(
                ProductiveBeeModel.EXTERNALS,
                CubeListBuilder
                        .create().texOffs(30, 0)
                        .addBox(-4.5F, -1.0F, 0.0F, 9.0F, 9.0F, 8.0F)
                        .texOffs(9, 0)
                        .addBox(-0.5F, 3.0F, 8.0F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.2618F, 0.0F, 0.0F)
        );
        externals.addOrReplaceChild(
                ProductiveBeeModel.BACK_LEGS,
                CubeListBuilder
                        .create().texOffs(24, 5)
                        .addBox(-5.0F, 7.0F, 1.0F, 7.0F, 2.0F, 0.0F),
                PartPose.offset(1.5F, 0.0F, 2.5F)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
