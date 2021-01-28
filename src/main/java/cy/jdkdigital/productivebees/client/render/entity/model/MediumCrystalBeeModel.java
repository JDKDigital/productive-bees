package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MediumCrystalBeeModel extends MediumBeeModel
{
    public MediumCrystalBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
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
        crystals.setTextureOffset(50, 54).addBox(1.0F, 1.0F, 1.0F, 3.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        crystals.setTextureOffset(48, 47).addBox(-1.0F, 0.0F, 0.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        crystals.setTextureOffset(52, 60).addBox(-1.0F, 2.0F, 4.0F, 3.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        crystals.setTextureOffset(42, 58).addBox(0.0F, 2.0F, -2.0F, 3.0F, 4.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(this.crystals);
    }

    @Override
    protected void addSantaHat() {}
}
