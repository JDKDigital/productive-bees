package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SlimyBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T> {

    public SlimyBeeModel(boolean outerLayer) {
        super(false);

        ModelRenderer slime = new ModelRenderer(this, 0, 0);
        ModelRenderer innards = new ModelRenderer(this, 34, 0);

        if (!outerLayer) {
            addBodyParts();
        } else {
            slime.setRotationPoint(0.0F, 0.0F, 0.0F);
            slime.addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
            this.beeModel.addChild(slime);

            innards.setRotationPoint(0.0F, 0.0F, 0.0F);
            innards.addBox(-2.5F, -3.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F);
            this.beeModel.addChild(innards);
        }
    }

        protected void addBodyParts() {
            this.beeModel.setRotationPoint(0.0F, 19.0F, 0.0F);
            this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.beeModel.addChild(this.body);
            this.stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
            this.body.addChild(this.stinger);
            this.leftAntenae.setRotationPoint(0.0F, -2.0F, -5.0F);
            this.leftAntenae.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
            this.rightAntenae.setRotationPoint(0.0F, -2.0F, -5.0F);
            this.rightAntenae.addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
            this.body.addChild(this.leftAntenae);
            this.body.addChild(this.rightAntenae);
            this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
            this.rightWing.rotateAngleX = 0.0F;
            this.rightWing.rotateAngleY = -0.2618F;
            this.rightWing.rotateAngleZ = 0.0F;
            this.beeModel.addChild(this.rightWing);
            this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
            this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
            this.leftWing.rotateAngleX = 0.0F;
            this.leftWing.rotateAngleY = 0.2618F;
            this.leftWing.rotateAngleZ = 0.0F;
            this.leftWing.mirror = true;
            this.beeModel.addChild(this.leftWing);
            this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
            this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
            this.beeModel.addChild(this.frontLegs);
            this.frontLegs.addBox("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
            this.midLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
            this.beeModel.addChild(this.midLegs);
            this.midLegs.addBox("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
            this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
            this.beeModel.addChild(this.backLegs);
            this.backLegs.addBox("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
        }
}
