package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MediumElvisBeeModel extends MediumBeeModel
{
    public MediumElvisBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = MediumBeeModel.createMeshDefinition(true);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.getChild(ProductiveBeeModel.BONE);
        PartDefinition body = bone.getChild(ProductiveBeeModel.BODY);

        body.addOrReplaceChild(
                ProductiveBeeModel.EXTERNALS,
                CubeListBuilder
                        .create().texOffs(34, 8)
                        .addBox(-2.5F, -2.0F, -2.0F, 7.0F, 4.0F, 2.0F),
                PartPose.offset(-1.0F, -2.0F, -5.0F)
        );

        body.addOrReplaceChild(ProductiveBeeModel.LEFT_ANTENNA, CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild(ProductiveBeeModel.RIGHT_ANTENNA, CubeListBuilder.create(), PartPose.ZERO);

        // TODO remove santa hat

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
