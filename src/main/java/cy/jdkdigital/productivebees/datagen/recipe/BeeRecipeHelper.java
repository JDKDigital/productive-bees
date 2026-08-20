package cy.jdkdigital.productivebees.datagen.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

/** Shared pieces for datagen recipes that turn one configurable bee into another. */
public final class BeeRecipeHelper
{
    private BeeRecipeHelper() {}

    public static Identifier beeId(String beePath) {
        return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, beePath);
    }

    /** A configurable bee's spawn egg, matched by its bee type. */
    public static Ingredient beeIngredient(String beePath) {
        return BeeCreator.getSpawnEggIngredient(beeId(beePath), true);
    }

    /** A configurable bee's spawn egg as a recipe result. */
    public static ItemStackTemplate beeResult(String beePath) {
        return new ItemStackTemplate(ModItems.CONFIGURABLE_SPAWN_EGG.get(), BeeCreator.getSpawnEggPatch(beeId(beePath)));
    }

    public static BeeExistsCondition beeExists(String beePath) {
        return new BeeExistsCondition(beeId(beePath));
    }
}
