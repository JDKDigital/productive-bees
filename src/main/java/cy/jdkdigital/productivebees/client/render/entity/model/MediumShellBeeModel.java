package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MediumShellBeeModel extends MediumBeeModel
{
    public MediumShellBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = MediumBeeModel.createMeshDefinition(true);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.getChild(ProductiveBeeModel.BONE);
        PartDefinition body = bone.getChild(ProductiveBeeModel.BODY);

        // Crystal structure
        body.addOrReplaceChild(
                ProductiveBeeModel.EXTERNALS,
                CubeListBuilder
                        .create().texOffs(36, 46)
                        .addBox(-2.5F, -1.0F, 0.0F, 8.0F, 6.0F, 6.0F)
                        .texOffs(48, 58)
                        .addBox(-2.5F, -2.0F, 0.0F, 4.0F, 1.0F, 4.0F),
                PartPose.offset(-1.5F, -4.0F, -4.0F)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
