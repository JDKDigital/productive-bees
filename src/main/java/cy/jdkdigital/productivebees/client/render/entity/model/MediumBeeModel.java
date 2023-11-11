package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MediumBeeModel extends PartialBeeModel
{
    public MediumBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = createMeshDefinition(true);
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    protected static MeshDefinition createMeshDefinition(boolean withTorso) {
        MeshDefinition meshDefinition = ProductiveBeeModel.createMeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition bone = root.addOrReplaceChild(ProductiveBeeModel.BONE, CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

        CubeListBuilder bodyBuilder = CubeListBuilder.create();
        if (withTorso) {
            bodyBuilder.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F);
        }
        PartDefinition body = bone.addOrReplaceChild(ProductiveBeeModel.BODY, bodyBuilder, PartPose.ZERO);

        body.addOrReplaceChild(ProductiveBeeModel.STINGER, CubeListBuilder.create().texOffs(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F), PartPose.ZERO);

        body.addOrReplaceChild(
                ProductiveBeeModel.LEFT_ANTENNA,
                CubeListBuilder
                        .create().texOffs(2, 0)
                        .addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, -2.0F, -5.0F)
        );
        body.addOrReplaceChild(
                ProductiveBeeModel.RIGHT_ANTENNA,
                CubeListBuilder
                        .create().texOffs(2, 3)
                        .addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, -2.0F, -5.0F)
        );

        CubeDeformation var5 = new CubeDeformation(0.001F);
        bone.addOrReplaceChild(
                ProductiveBeeModel.RIGHT_WING,
                CubeListBuilder
                        .create().texOffs(0, 18)
                        .addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var5),
                PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.LEFT_WING,
                CubeListBuilder
                        .create().texOffs(0, 18).mirror()
                        .addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var5),
                PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F)
        );

        bone.addOrReplaceChild(
                ProductiveBeeModel.FRONT_LEGS,
                CubeListBuilder
                        .create().texOffs(26, 1)
                        .addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0),
                PartPose.offset(1.5F, 3.0F, -2.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.MIDDLE_LEGS,
                CubeListBuilder
                        .create().texOffs(26, 3)
                        .addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0),
                PartPose.offset(1.5F, 3.0F, 0.0F)
        );
        bone.addOrReplaceChild(
                ProductiveBeeModel.BACK_LEGS,
                CubeListBuilder
                        .create().texOffs(26, 5)
                        .addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0),
                PartPose.offset(1.5F, 3.0F, 2.0F)
        );

        PartDefinition hat = body.addOrReplaceChild(
                ProductiveBeeModel.SANTA_HAT,
                CubeListBuilder
                        .create().texOffs(0, 52)
                        .addBox(-5.0F, -10.1F, -6.1F, 9.0F, 3.0F, 9.0F),
                PartPose.offset(0.5F, 5.0F, 0.0F)
        );
        hat.addOrReplaceChild(
                ProductiveBeeModel.SANTA_HAT,
                CubeListBuilder
                        .create().texOffs(27, 55)
                        .addBox(-1.5F, -6.0F, 5.0F, 3.0F, 3.0F, 3.0F)
                        .texOffs(0, 40)
                        .addBox(-3.5F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F),
                PartPose.offset(-0.5F, -10.0F, -5.0F)
        );
        return meshDefinition;
    }
}
