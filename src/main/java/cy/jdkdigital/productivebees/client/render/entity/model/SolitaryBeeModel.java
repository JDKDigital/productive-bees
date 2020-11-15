package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;

public class SolitaryBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T>
{
    public SolitaryBeeModel() {
        this(true);
    }

    public SolitaryBeeModel(boolean addBodyParts) {
        super(false);

        if (addBodyParts) {
            addBodyParts();
        }
    }

    protected void addBodyParts() {
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.body.addChild(this.torso);
        this.torso.addBox(-3.5F, -4.0F, -5.0F, 6.0F, 6.0F, 10.0F, 0.0F);

        this.stinger.addBox(-0.5F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);

        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addBox(1.0F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);

        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addBox(-3.0F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);

        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);

        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.rightWing.addBox(-10.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.body.addChild(this.rightWing);

        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.body.addChild(this.leftWing);

        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.frontLegs.addBox("frontLegBox", -5.0F, -1.0F, 0.0F, 5, 2, 0, 0.0F, 26, 1);

        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.middleLegs.addBox("midLegBox", -5.0F, -1.0F, 0.0F, 5, 2, 0, 0.0F, 26, 3);

        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.backLegs.addBox("backLegBox", -5.0F, -1.0F, 0.0F, 5, 2, 0, 0.0F, 26, 5);

        this.body.addChild(this.frontLegs);
        this.body.addChild(this.middleLegs);
        this.body.addChild(this.backLegs);
    }
}
