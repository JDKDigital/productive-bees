package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MediumFoliageBeeModel extends MediumBeeModel
{
    public MediumFoliageBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
    }

    @Override
    public void addBodyParts(boolean withTorso) {
        super.addBodyParts(withTorso);
        addCrystals();
    }

    @Override
    protected void addCrystals() {
        externals.setRotationPoint(0.0F, -4.0F, 0.0F);
        externals.setTextureOffset(52, 28).addBox(-3.0F, -6.0F, -5.0F, 0.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        externals.setTextureOffset(52, 34).addBox(-6.0F, -6.0F, -2.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        externals.setTextureOffset(52, 34).addBox(1.0F, -6.0F, -4.0F, 0.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        externals.setTextureOffset(52, 40).addBox(-2.0F, -6.0F, -1.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(externals, 0.0F, -0.7853981633974483F, 0.0F);
        torso.addChild(externals);
    }

    @Override
    protected void addSantaHat() {}
}
