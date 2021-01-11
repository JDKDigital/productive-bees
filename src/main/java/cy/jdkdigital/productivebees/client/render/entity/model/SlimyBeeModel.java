package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SlimyBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T>
{
    public SlimyBeeModel(boolean outerLayer) {
        super(false);

        ModelRenderer slime = new ModelRenderer(this, 0, 0);
        ModelRenderer innards = new ModelRenderer(this, 34, 0);

        if (!outerLayer) {
            addBodyParts(true);
        } else {
            slime.setRotationPoint(0.0F, 0.0F, 0.0F);
            slime.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
            this.body.addChild(slime);

            innards.setRotationPoint(0.0F, 0.0F, 0.0F);
            innards.addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
            this.body.addChild(innards);
        }
    }

    protected void addBodyParts(boolean withTorso) {
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);
        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.body.addChild(this.rightWing);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.addBox("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.addBox("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.addBox("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
    }
}
