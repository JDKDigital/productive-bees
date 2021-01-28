package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MediumShellBeeModel extends MediumBeeModel
{
    public MediumShellBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
    }

    @Override
    public void addBodyParts(boolean withTorso) {
        super.addBodyParts(withTorso);
        addCrystals();
    }

    @Override
    protected void addCrystals() {
        crystals.setRotationPoint(-1.5F, -7.0F, -4.0F);
        crystals.setRotationPoint(-1.5F, -4.0F, -4.0F);
        crystals.setTextureOffset(36, 46).addBox(-2.5F, -1.0F, 0.0F, 8.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        crystals.setTextureOffset(48, 58).addBox(-2.5F, -2.0F, 0.0F, 4.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(crystals);
    }

    @Override
    protected void addSantaHat() {}
}
