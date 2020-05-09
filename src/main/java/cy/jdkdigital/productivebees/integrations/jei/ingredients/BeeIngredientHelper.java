package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeeIngredientHelper implements IIngredientHelper<ProduciveBeesJeiPlugin.BeeIngredient> {

    @Nullable
    @Override
    public ProduciveBeesJeiPlugin.BeeIngredient getMatch(Iterable<ProduciveBeesJeiPlugin.BeeIngredient> iterable, ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        for (ProduciveBeesJeiPlugin.BeeIngredient ingredient : iterable) {
            if (ingredient.getBeeType().getRegistryName() == beeIngredient.getBeeType().getRegistryName()) {
                return ingredient;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getDisplayName(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getName().getFormattedText();
    }

    @Nonnull
    @Override
    public String getUniqueId(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return "beeingredient:" + beeIngredient.getBeeType().getRegistryName();
    }

    @Nonnull
    @Override
    public String getWildcardId(@Nonnull ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return getUniqueId(beeIngredient);
    }

    @Nonnull
    @Override
    public String getModId(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getRegistryName().getNamespace();
    }

    @Nonnull
    @Override
    public String getResourceId(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getRegistryName().getPath();
    }

    @Nonnull
    @Override
    public ProduciveBeesJeiPlugin.BeeIngredient copyIngredient(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return new ProduciveBeesJeiPlugin.BeeIngredient(beeIngredient.getBeeType(), beeIngredient.getRenderType());
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        if(beeIngredient == null) {
            return "beeingredient:null";
        }
        if(beeIngredient.getBeeType() == null) {
            return "beeingredient:bee:null";
        }
        return "beeingredient:" + beeIngredient.getBeeType().getRegistryName();
    }
}
