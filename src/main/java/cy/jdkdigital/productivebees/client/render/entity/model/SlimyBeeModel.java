package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SlimyBeeModel extends MediumBeeModel
{
    public SlimyBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = MediumBeeModel.createMeshDefinition(false);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.getChild(ProductiveBeeModel.BONE);
        PartDefinition body = bone.getChild(ProductiveBeeModel.BODY);

        body.addOrReplaceChild(
                ProductiveBeeModel.EXTERNALS,
                CubeListBuilder
                        .create()
                        .addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F),
                PartPose.ZERO
        );
        body.addOrReplaceChild(
                ProductiveBeeModel.INNARDS,
                CubeListBuilder
                        .create().texOffs(34, 0)
                        .addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
