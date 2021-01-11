package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

abstract public class PartialBeeModel
{
    protected final Model model;
    protected ModelRenderer body;
    protected ModelRenderer torso;
    protected ModelRenderer rightWing;
    protected ModelRenderer leftWing;
    protected ModelRenderer frontLegs;
    protected ModelRenderer middleLegs;
    protected ModelRenderer backLegs;
    protected ModelRenderer stinger;
    protected ModelRenderer leftAntenna;
    protected ModelRenderer rightAntenna;
    protected ModelRenderer innards;
    protected ModelRenderer santaHat;

    public PartialBeeModel(
            Model model,
            ModelRenderer body,
            ModelRenderer torso,
            ModelRenderer stinger,
            ModelRenderer leftAntenna,
            ModelRenderer rightAntenna,
            ModelRenderer leftWing,
            ModelRenderer rightWing,
            ModelRenderer middleLegs,
            ModelRenderer frontLegs,
            ModelRenderer backLegs,
            ModelRenderer innards,
            ModelRenderer santaHat)
    {
        this.model = model;
        this.body = body;
        this.torso = torso;
        this.stinger = stinger;
        this.leftAntenna = leftAntenna;
        this.rightAntenna = rightAntenna;
        this.leftWing = leftWing;
        this.rightWing = rightWing;
        this.middleLegs = middleLegs;
        this.frontLegs = frontLegs;
        this.backLegs = backLegs;
        this.innards = innards;
        this.santaHat = santaHat;
    }

    public void addBodyParts(boolean withTorso) {
        addTorso(withTorso);
        addAntenna();
        addWings();
        addLegs();
        addInnards();
        addSantaHat();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    abstract void addTorso(boolean withTorso);
    abstract void addAntenna();
    abstract void addWings();
    abstract void addLegs();
    protected void addInnards() {}
    protected void addSantaHat() {}
}
