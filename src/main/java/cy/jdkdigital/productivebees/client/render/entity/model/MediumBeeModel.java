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
        body.setPos(0.0F, 19.0F, 0.0F);
        torso.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        if (withTorso) {
            torso.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        }
        stinger.texOffs(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        torso.addChild(stinger);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setPos(0.0F, -2.0F, -5.0F);
        leftAntenna.texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        rightAntenna.setPos(0.0F, -2.0F, -5.0F);
        rightAntenna.texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setPos(-1.5F, -4.0F, -3.0F);
        setRotationAngle(rightWing, 0.0F, -0.2617999870103947F, 0.0F);
        rightWing.texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        leftWing.setPos(1.5F, -4.0F, -3.0F);
        setRotationAngle(leftWing, 0.0F, 0.2617999870103947F, 0.0F);
        leftWing.mirror = true;
        leftWing.texOffs(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        body.addChild(rightWing);
        body.addChild(leftWing);
    }

    @Override
    protected void addLegs() {
        frontLegs.setPos(1.5F, 3.0F, -2.0F);
        frontLegs.texOffs(26, 1).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);
        middleLegs.setPos(1.5F, 3.0F, 0.0F);
        middleLegs.texOffs(26, 3).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);
        backLegs.setPos(1.5F, 3.0F, 2.0F);
        backLegs.texOffs(26, 5).addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        body.addChild(backLegs);
    }

    @Override
    protected void addInnards() {
        innards.setPos(0.0F, 0.0F, 0.0F);
        innards.texOffs(34, 0).addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
        body.addChild(innards);
    }

    @Override
    protected void addSantaHat() {
        ModelRenderer hatDroop = new ModelRenderer(this.model);
        hatDroop.setPos(-0.5F, -10.0F, -5.0F);
        hatDroop.texOffs(27, 55).addBox(-1.5F, -6.0F, 5.0F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        hatDroop.texOffs(0, 40).addBox(-3.5F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(hatDroop, -0.5061454830783556F, 0.0F, 0.0F);
        santaHat.setPos(0.5F, 5.0F, 0.0F);
        santaHat.texOffs(0, 52).addBox(-5.0F, -10.0F, -6.0F, 9.0F, 3.0F, 9.0F, 0.0F, 0.0F, 0.0F);

        santaHat.addChild(hatDroop);
        torso.addChild(santaHat);
    }
}
