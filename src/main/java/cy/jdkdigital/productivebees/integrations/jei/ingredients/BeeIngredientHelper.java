package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeeIngredientHelper implements IIngredientHelper<BeeIngredient>
{
    @Nullable
    @Override
    public BeeIngredient getMatch(Iterable<BeeIngredient> iterable, BeeIngredient beeIngredient, UidContext uidContext) {
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
        CompoundTag nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
        if (nbt != null) {
            return new TranslatableComponent("entity.productivebees.bee_configurable", nbt.getString("name")).getString();
        }
        return beeIngredient.getBeeEntity().getDescription().getString();
    }

    @Nonnull
    @Override
    public String getUniqueId(BeeIngredient beeIngredient, UidContext uidContext) {
        return "beeingredient:" + beeIngredient.getBeeType();
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
