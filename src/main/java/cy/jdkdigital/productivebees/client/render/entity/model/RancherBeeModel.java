package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RancherBeeModel<T extends ProductiveBee> extends ProductiveBeeModel<T>
{
    public RancherBeeModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = MediumBeeModel.createMeshDefinition(true);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.getChild(ProductiveBeeModel.BONE);
        PartDefinition body = bone.getChild(ProductiveBeeModel.BODY);

        body.addOrReplaceChild(
                "hat",
                CubeListBuilder.create()
                        .texOffs(25, 8)
                        .addBox(-7.0F, 0.0F, -1.0F, 9.0F, 1.0F, 9.0F)
                        .texOffs(29, 12)
                        .addBox(-5.0F, -2.0F, 1.0F, 5.0F, 2.0F, 5.0F),
                PartPose.offset(2.5F, -4.5F, -5.0F)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
