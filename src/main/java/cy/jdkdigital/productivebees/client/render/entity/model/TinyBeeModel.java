package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TinyBeeModel extends PartialBeeModel
{
    public TinyBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
    }

    @Override
    protected void addTorso(boolean withTorso) {
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        if (withTorso) {
            torso.addBox(-1.5F, -2.0F, -2.0F, 3.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        }
        stinger.setRotationPoint(0.0F, 0.0F, 0.0F);
        stinger.setTextureOffset(22, 6).addBox(0.0F, 0.0F, 4.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        torso.addChild(stinger);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setRotationPoint(0.0F, -1.0F, -2.0F);
        leftAntenna.addBox(1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        rightAntenna.setRotationPoint(0.0F, -1.0F, -2.0F);
        rightAntenna.setTextureOffset(0, 2).addBox(-2.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setRotationPoint(-0.5F, -2.0F, -1.0F);
        rightWing.setTextureOffset(-3, 9).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(rightWing, 0.0F, -0.2617993877991494F, 0.0F);
        leftWing.mirror = true;
        leftWing.setRotationPoint(0.5F, -2.0F, -1.0F);
        leftWing.setTextureOffset(-3, 9).addBox(0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(leftWing, 0.0F, 0.2617993877991494F, 0.0F);

        body.addChild(leftWing);
        body.addChild(rightWing);
    }

    @Override
    protected void addLegs() {
        backLegs.setRotationPoint(1.5F, 1.0F, 1.0F);
        backLegs.setTextureOffset(15, 5).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        middleLegs.setRotationPoint(1.5F, 1.0F, 0.0F);
        middleLegs.setTextureOffset(15, 3).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        frontLegs.setRotationPoint(1.5F, 1.0F, -1.0F);
        frontLegs.setTextureOffset(15, 1).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        body.addChild(backLegs);
    }
}
