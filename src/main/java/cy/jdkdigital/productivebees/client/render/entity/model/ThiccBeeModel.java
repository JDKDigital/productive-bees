package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThiccBeeModel extends PartialBeeModel
{
    public ThiccBeeModel(Model model, ModelRenderer body, ModelRenderer torso, ModelRenderer stinger, ModelRenderer leftAntenna, ModelRenderer rightAntenna, ModelRenderer leftWing, ModelRenderer rightWing, ModelRenderer middleLegs, ModelRenderer frontLegs, ModelRenderer backLegs, ModelRenderer innards, ModelRenderer santaHat) {
        super(model, body, torso, stinger, leftAntenna, rightAntenna, leftWing, rightWing, middleLegs, frontLegs, backLegs, innards, santaHat);
    }

    @Override
    protected void addTorso(boolean withTorso) {
        body.setRotationPoint(0.0F, 18.0F, 0.0F);
        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        if (withTorso) {
            torso.setTextureOffset(3, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        }
        ModelRenderer butt = new ModelRenderer(this.model);
        butt.setRotationPoint(0.0F, -4.0F, 0.0F);
        butt.setTextureOffset(30, 0).addBox(-4.5F, -1.0F, 0.0F, 9.0F, 9.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        butt.setTextureOffset(9, 0).addBox(-0.5F, 3.0F, 8.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(butt, -0.2617993877991494F, 0.0F, 0.0F);
        torso.addChild(butt);
    }

    @Override
    protected void addAntenna() {
        leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        rightAntenna.setTextureOffset(0, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        torso.addChild(leftAntenna);
        torso.addChild(rightAntenna);
    }

    @Override
    protected void addWings() {
        rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        rightWing.setTextureOffset(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(rightWing, 0.3490658503988659F, -0.2617993877991494F, 0.0F);

        leftWing.mirror = true;
        leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        leftWing.setTextureOffset(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        setRotationAngle(leftWing, 0.3490658503988659F, 0.2617993877991494F, 0.0F);

        body.addChild(leftWing);
        body.addChild(rightWing);
    }

    @Override
    protected void addLegs() {
        frontLegs.setRotationPoint(1.5F, 3.0F, -3.0F);
        frontLegs.setTextureOffset(24, 1).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        middleLegs.setRotationPoint(1.5F, 3.5F, -1.0F);
        middleLegs.setTextureOffset(24, 3).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        backLegs.setRotationPoint(1.5F, 4.0F, 1.0F);
        backLegs.setTextureOffset(24, 5).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        body.addChild(frontLegs);
        body.addChild(middleLegs);
        body.addChild(backLegs);
    }
}
