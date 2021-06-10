package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class TinyBeeModel extends PartialBeeModel
{
    public TinyBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
    }

    @Override
    protected void addTorso(boolean withTorso) {
        body.setPos(0.0F, 19.0F, 0.0F);
        torso.setPos(0.0F, 0.0F, 0.0F);
        if (withTorso) {
            torso.addBox(-1.5F, -2.0F, -2.0F, 3.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        }
        stinger.setPos(0.0F, 0.0F, 0.0F);
        stinger.texOffs(22, 6).addBox(0.0F, 0.0F, 4.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        torso.addChild(stinger);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setPos(0.0F, -1.0F, -2.0F);
        leftAntenna.addBox(1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        rightAntenna.setPos(0.0F, -1.0F, -2.0F);
        rightAntenna.texOffs(0, 2).addBox(-2.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setPos(-0.5F, -2.0F, -1.0F);
        rightWing.texOffs(-3, 9).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(rightWing, 0.0F, -0.2617993877991494F, 0.0F);
        leftWing.mirror = true;
        leftWing.setPos(0.5F, -2.0F, -1.0F);
        leftWing.texOffs(-3, 9).addBox(0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(leftWing, 0.0F, 0.2617993877991494F, 0.0F);

        body.addChild(leftWing);
        body.addChild(rightWing);
    }

    @Override
    protected void addLegs() {
        backLegs.setPos(1.5F, 1.0F, 1.0F);
        backLegs.texOffs(15, 5).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        middleLegs.setPos(1.5F, 1.0F, 0.0F);
        middleLegs.texOffs(15, 3).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        frontLegs.setPos(1.5F, 1.0F, -1.0F);
        frontLegs.texOffs(15, 1).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        body.addChild(backLegs);
    }
}
