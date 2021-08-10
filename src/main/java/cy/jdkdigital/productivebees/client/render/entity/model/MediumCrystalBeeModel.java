package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MediumCrystalBeeModel extends MediumBeeModel
{
    public MediumCrystalBeeModel(ModelPart model) {
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
                        .create().texOffs(50, 54)
                        .addBox(1.0F, 1.0F, 1.0F, 3.0F, 2.0F, 4.0F)
                        .texOffs(48, 47)
                        .addBox(-1.0F, 0.0F, 0.0F, 4.0F, 3.0F, 4.0F)
                        .texOffs(52, 60)
                        .addBox(-1.0F, 2.0F, 4.0F, 3.0F, 1.0F, 2.0F)
                        .texOffs(42, 58)
                        .addBox(0.0F, 2.0F, -2.0F, 3.0F, 4.0F, 2.0F),
                PartPose.offset(-1.5F, -7.0F, -4.0F)
        );

        // TODO remove santa hat

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
