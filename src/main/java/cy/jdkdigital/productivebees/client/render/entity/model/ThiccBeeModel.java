package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ThiccBeeModel extends PartialBeeModel
{
    public ThiccBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer crystals, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, crystals, innards, santaHat);
    }

    @Override
    protected void addTorso(boolean withTorso) {
        body.setPos(0.0F, 18.0F, 0.0F);
        torso.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        if (withTorso) {
            torso.texOffs(3, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        }
        externals.setPos(0.0F, -4.0F, 0.0F);
        externals.texOffs(30, 0).addBox(-4.5F, -1.0F, 0.0F, 9.0F, 9.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        externals.texOffs(9, 0).addBox(-0.5F, 3.0F, 8.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(externals, -0.2617993877991494F, 0.0F, 0.0F);
        torso.addChild(externals);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setPos(0.0F, -2.0F, -5.0F);
        leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        rightAntenna.setPos(0.0F, -2.0F, -5.0F);
        rightAntenna.texOffs(0, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setPos(-1.5F, -4.0F, -3.0F);
        rightWing.texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(rightWing, 0.3490658503988659F, -0.2617993877991494F, 0.0F);

        leftWing.mirror = true;
        leftWing.setPos(1.5F, -4.0F, -3.0F);
        leftWing.texOffs(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(leftWing, 0.3490658503988659F, 0.2617993877991494F, 0.0F);

        body.addChild(leftWing);
        body.addChild(rightWing);
    }

    @Override
    protected void addLegs() {
        frontLegs.setPos(1.5F, 3.0F, -3.0F);
        frontLegs.texOffs(24, 1).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        middleLegs.setPos(1.5F, 4.0F, 1.0F);
        middleLegs.texOffs(24, 3).addBox(-5.0F, -1.5F, -1.0F, 7.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        backLegs.setPos(1.5F, 3.5F, 0.0F);
        backLegs.texOffs(24, 5).addBox(-5.0F, 7.0F, 1.0F, 7.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        externals.addChild(backLegs);
    }
}
