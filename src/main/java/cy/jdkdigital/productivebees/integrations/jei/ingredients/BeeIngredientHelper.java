package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeeIngredientHelper implements IIngredientHelper<BeeIngredient>
{
    @Nullable
    @Override
    public BeeIngredient getMatch(Iterable<BeeIngredient> iterable, BeeIngredient beeIngredient) {
        for (BeeIngredient ingredient : iterable) {
            if (ingredient.getBeeType() == beeIngredient.getBeeType()) {
                return ingredient;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getDisplayName(BeeIngredient beeIngredient) {
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
        if (nbt != null) {
            return new TranslationTextComponent("entity.productivebees." + ProductiveBeeEntity.getBeeName(beeIngredient.getBeeType().toString()) + "_bee").toString();
        }
        return beeIngredient.getBeeEntity().getDescription().getString();
    }

    @Nonnull
    @Override
    public String getUniqueId(BeeIngredient beeIngredient) {
        return "beeingredient:" + beeIngredient.getBeeType();
    }

    @Nonnull
    @Override
    public String getWildcardId(@Nonnull BeeIngredient beeIngredient) {
        return getUniqueId(beeIngredient);
    }

    @Nonnull
    @Override
    public String getModId(BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getNamespace();
    }

    @Nonnull
    @Override
    public String getResourceId(BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getPath();
    }

    @Nonnull
    @Override
    public BeeIngredient copyIngredient(BeeIngredient beeIngredient) {
        return new BeeIngredient(beeIngredient.getBeeEntity(), beeIngredient.getBeeType());
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return "beeingredient:null";
        }
        if (beeIngredient.getBeeEntity() == null) {
            return "beeingredient:bee:null";
        }
        return "beeingredient:" + beeIngredient.getBeeType();
    }
}
