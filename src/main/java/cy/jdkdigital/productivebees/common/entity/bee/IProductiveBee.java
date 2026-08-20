package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;

public interface IProductiveBee
{
    default float getSizeModifier() {
        return 1.0f;
    }

    default boolean canSelfBreed() {
        return true;
    }

    default Ingredient getBreedingIngredient() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(ModTags.DEFAULT_BREEDING).orElseThrow());
    }

    default Integer getBreedingItemCount() {
        return 1;
    }

    default String getRenderer() {
        return "default";
    }

    boolean canOperateDuringNight();

    boolean canOperateDuringRain();

    boolean canOperateDuringThunder();
}
