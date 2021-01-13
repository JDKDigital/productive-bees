package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SlimBeeModel extends PartialBeeModel
{
    public SlimBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
    }

    @Override
    protected void addTorso(boolean withTorso) {
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        if (withTorso) {
            torso.addBox(-2.5F, -2.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        }
        stinger.setTextureOffset(22, 6).addBox(0.0F, 0.0F, 4.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(stinger);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setRotationPoint(0.0F, -1.0F, -4.0F);
        leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        rightAntenna.setRotationPoint(0.0F, -1.0F, -4.0F);
        rightAntenna.setTextureOffset(0, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setRotationPoint(-0.5F, -2.0F, -2.0F);
        rightWing.setTextureOffset(-2, 16).addBox(-7.0F, 0.0F, 0.0F, 7.0F, 0.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(rightWing, 0.0F, -0.2617993877991494F, 0.0F);

        leftWing.mirror = true;
        leftWing.setRotationPoint(0.5F, -2.0F, -2.0F);
        leftWing.setTextureOffset(-2, 16).addBox(0.0F, 0.0F, 0.0F, 7.0F, 0.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(leftWing, 0.0F, 0.2617993877991494F, 0.0F);

        body.addChild(rightWing);
        body.addChild(leftWing);
    }

    @Override
    protected void addLegs() {
        frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        frontLegs.setTextureOffset(22, 1).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        middleLegs.setTextureOffset(22, 3).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        backLegs.setTextureOffset(22, 5).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        body.addChild(frontLegs);
        body.addChild(backLegs);
        body.addChild(middleLegs);
    }
}
