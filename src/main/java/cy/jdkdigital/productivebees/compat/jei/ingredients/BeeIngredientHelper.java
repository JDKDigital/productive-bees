package cy.jdkdigital.productivebees.compat.jei.ingredients;

import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.compat.jei.ProductiveBeesJeiPlugin;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeeIngredientHelper implements IIngredientHelper<BeeIngredient>
{
    @Nonnull
    @Override
    public IIngredientType<BeeIngredient> getIngredientType() {
        return ProductiveBeesJeiPlugin.BEE_INGREDIENT;
    }

    @Nonnull
    @Override
    public String getDisplayName(BeeIngredient beeIngredient) {
        String name = beeIngredient.getBeeEntity().getDescription().getString();
        BeeData beeData = BeeRegistries.lookup(beeIngredient.getBeeType());
        if (beeData != null) {
            name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeIngredient.getBeeType()) + "_bee").toString();
            name = name + " (" + BeeData.groupFor(BeeRegistries.resolveId(beeIngredient.getBeeType())) + ")";
        }
        return name;
    }

    @Nonnull
    @Override
    public Object getUid(BeeIngredient beeIngredient, UidContext uidContext) {
        return beeIngredient.getBeeType();
    }

    @Override
    public String getDisplayModId(BeeIngredient ingredient) {
        return ingredient.getBeeType().getNamespace();
    }

    @Override
    public Identifier getIdentifier(BeeIngredient ingredient) {
        return ingredient.getBeeType();
    }

    @Nonnull
    @Override
    public BeeIngredient copyIngredient(BeeIngredient beeIngredient) {
        return new BeeIngredient(beeIngredient.getBeeEntity(), beeIngredient.getBeeType());
    }

    @Override
    public ItemStack getCheatItemStack(BeeIngredient ingredient) {
        ItemStack cage = BeeIngredientRenderer.getCageStack(ingredient);
        return cage.isEmpty() ? ItemStack.EMPTY : cage.copy();
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return "beeingredient:null";
        }
        if (beeIngredient.getBeeEntity() == null) {
            return "beeingredient:bee_null";
        }
        return beeIngredient.getBeeType().toString();
    }
}
