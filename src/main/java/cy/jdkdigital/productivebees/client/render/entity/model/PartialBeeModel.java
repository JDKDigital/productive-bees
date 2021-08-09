package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;

abstract class PartialBeeModel
{
    protected final ModelPart model;

    public PartialBeeModel(ModelPart model) {
        this.model = model;
    }
}
