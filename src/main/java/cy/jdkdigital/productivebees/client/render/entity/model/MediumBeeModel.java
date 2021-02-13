package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MediumBeeModel extends PartialBeeModel
{
    public MediumBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
    }

    @Override
    protected void addTorso(boolean withTorso) {
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        if (withTorso) {
            torso.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        }
        stinger.setTextureOffset(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        torso.addChild(stinger);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        leftAntenna.setTextureOffset(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        rightAntenna.setTextureOffset(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        setRotationAngle(rightWing, 0.0F, -0.2617999870103947F, 0.0F);
        rightWing.setTextureOffset(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        setRotationAngle(leftWing, 0.0F, 0.2617999870103947F, 0.0F);
        leftWing.mirror = true;
        leftWing.setTextureOffset(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        body.addChild(rightWing);
        body.addChild(leftWing);
    }

    @Override
    protected void addLegs() {
        frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        frontLegs.setTextureOffset(26, 1).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);
        middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        middleLegs.setTextureOffset(26, 3).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);
        backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        backLegs.setTextureOffset(26, 5).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        body.addChild(backLegs);
    }

    @Override
    protected void addInnards() {
        innards.setRotationPoint(0.0F, 0.0F, 0.0F);
        innards.setTextureOffset(34, 0).addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
        body.addChild(innards);
    }

    @Override
    protected void addSantaHat() {
        ModelRenderer hatDroop = new ModelRenderer(this.model);
        hatDroop.setRotationPoint(-0.5F, -10.0F, -5.0F);
        hatDroop.setTextureOffset(27, 55).addBox(-1.5F, -6.0F, 5.0F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        hatDroop.setTextureOffset(0, 40).addBox(-3.5F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(hatDroop, -0.5061454830783556F, 0.0F, 0.0F);
        santaHat.setRotationPoint(0.5F, 5.0F, 0.0F);
        santaHat.setTextureOffset(0, 52).addBox(-5.0F, -10.0F, -6.0F, 9.0F, 3.0F, 9.0F, 0.0F, 0.0F, 0.0F);

        santaHat.addChild(hatDroop);
        torso.addChild(santaHat);
    }
}
