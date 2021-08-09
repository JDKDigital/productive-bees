package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MediumFoliageBeeModel extends MediumBeeModel
{
    public MediumFoliageBeeModel(ModelPart model) {
        super(model);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = MediumBeeModel.createMeshDefinition(true);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.getChild(ProductiveBeeModel.BONE);
        PartDefinition body = bone.getChild(ProductiveBeeModel.BODY);

        // Foliage structure
        body.addOrReplaceChild(
                ProductiveBeeModel.EXTERNALS,
                CubeListBuilder
                        .create()
                        .texOffs(52, 28)
                        .addBox(-3.0F, -6.0F, -5.0F, 0.0F, 6.0F, 6.0F)
                        .texOffs(52, 34)
                        .addBox(-6.0F, -6.0F, -2.0F, 6.0F, 6.0F, 0.0F)
                        .texOffs(52, 34)
                        .addBox(1.0F, -6.0F, -4.0F, 0.0F, 6.0F, 6.0F)
                        .texOffs(52, 40)
                        .addBox(-2.0F, -6.0F, -1.0F, 6.0F, 6.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );

        // TODO remove santa hat

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
