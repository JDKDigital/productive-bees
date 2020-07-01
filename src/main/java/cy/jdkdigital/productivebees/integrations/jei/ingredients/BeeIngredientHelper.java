package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeeIngredientHelper implements IIngredientHelper<BeeIngredient>
{
    @Nullable
    @Override
    public BeeIngredient getMatch(Iterable<BeeIngredient> iterable, BeeIngredient beeIngredient) {
        for (BeeIngredient ingredient : iterable) {
            if (ingredient.getBeeType().getRegistryName() == beeIngredient.getBeeType().getRegistryName()) {
                return ingredient;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getDisplayName(BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getName().getString();
    }

    @Nonnull
    @Override
    public String getUniqueId(BeeIngredient beeIngredient) {
        return "beeingredient:" + beeIngredient.getBeeType().getRegistryName();
    }

    @Nonnull
    @Override
    public String getWildcardId(@Nonnull BeeIngredient beeIngredient) {
        return getUniqueId(beeIngredient);
    }

    @Nonnull
    @Override
    public String getModId(BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getRegistryName().getNamespace();
    }

    @Nonnull
    @Override
    public String getResourceId(BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getRegistryName().getPath();
    }

    @Nonnull
    @Override
    public BeeIngredient copyIngredient(BeeIngredient beeIngredient) {
        return new BeeIngredient(beeIngredient.getBeeType(), beeIngredient.getRenderType());
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return "beeingredient:null";
        }
        if (beeIngredient.getBeeType() == null) {
            return "beeingredient:bee:null";
        }
        return "beeingredient:" + beeIngredient.getBeeType().getRegistryName();
    }
}
