package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SlimyBeeModel extends MediumBeeModel
{
    public SlimyBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer externals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, externals, innards, santaHat);
    }

    @Override
    public void addBodyParts(boolean withTorso) {
        super.addBodyParts(false);
        addCrystals();
    }

    @Override
    protected void addCrystals() {
        externals.setRotationPoint(0.0F, 0.0F, 0.0F);
        externals.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        body.addChild(externals);

        innards.setRotationPoint(0.0F, 0.0F, 0.0F);
        innards.addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
        body.addChild(innards);
    }

    @Override
    protected void addSantaHat() {}
}
