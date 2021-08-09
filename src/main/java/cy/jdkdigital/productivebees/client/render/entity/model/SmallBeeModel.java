package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SmallBeeModel extends PartialBeeModel
{
    public SmallBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = ProductiveBeeModel.createMeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition bone = root.addOrReplaceChild(
                ProductiveBeeModel.BONE,
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 19.0F, 0.0F)
        );

        PartDefinition body = bone.addOrReplaceChild(
                ProductiveBeeModel.BODY,
                CubeListBuilder.create()
                        .addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 8.0F),
                PartPose.ZERO
        );

        body.addOrReplaceChild(
                ProductiveBeeModel.STINGER,
                CubeListBuilder.create()
                        .texOffs(22, 6)
                        .addBox(0.0F, -0.5F, 4.0F, 0.0F, 1.0F, 1.0F),
                PartPose.ZERO
        );

        body.addOrReplaceChild(
                ProductiveBeeModel.LEFT_ANTENNA,
                CubeListBuilder.create()
                        .addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, -1.0F, -4.0F)
        );
        body.addOrReplaceChild(
                ProductiveBeeModel.RIGHT_ANTENNA,
                CubeListBuilder.create()
                        .texOffs(0, 3)
                        .addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, -1.0F, -4.0F)
        );

        CubeDeformation var5 = new CubeDeformation(0.001F);
        bone.addOrReplaceChild(
                ProductiveBeeModel.RIGHT_WING,
                CubeListBuilder.create()
                        .texOffs(-2, 16)
                        .addBox(-7.0F, 0.0F, 0.0F, 7.0F, 0.0F, 5.0F, var5),
                PartPose.offsetAndRotation(-0.5F, -3.0F, -2.0F, 0.0F, -0.2618F, 0.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.LEFT_WING,
                CubeListBuilder.create()
                        .texOffs(-2, 16).mirror()
                        .addBox(0.0F, 0.0F, 0.0F, 7.0F, 0.0F, 5.0F, var5),
                PartPose.offsetAndRotation(00.5F, -3.0F, -2.0F, 0.0F, 0.2618F, 0.0F)
        );

        bone.addOrReplaceChild(
                ProductiveBeeModel.FRONT_LEGS,
                CubeListBuilder
                        .create().texOffs(22, 1)
                        .addBox(-4.5F, 0.0F, 0.0F, 6.0F, 1.0F, 0.0F),
                PartPose.offset(1.5F, 3.0F, -2.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.MIDDLE_LEGS,
                CubeListBuilder
                        .create().texOffs(22, 3)
                        .addBox(-4.5F, 0.0F, 0.0F, 6.0F, 1.0F, 0.0F),
                PartPose.offset(1.5F, 3.0F, 0.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.BACK_LEGS,
                CubeListBuilder
                        .create().texOffs(22, 5)
                        .addBox(-4.5F, 0.0F, 0.0F, 6.0F, 1.0F, 0.0F),
                PartPose.offset(1.5F, 3.0F, 2.0F)
        );

        // TODO Remove santa hat

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
