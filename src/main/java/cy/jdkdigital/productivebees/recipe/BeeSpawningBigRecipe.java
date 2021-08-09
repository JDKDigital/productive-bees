package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

public class BeeSpawningBigRecipe extends BeeSpawningRecipe
{
    public static final RecipeType<BeeSpawningBigRecipe> BEE_SPAWNING = RecipeType.register(ProductiveBees.MODID + ":bee_spawning_big");

    public BeeSpawningBigRecipe(ResourceLocation id, Ingredient ingredient, List<Lazy<BeeIngredient>> output, List<String> biomes, String temperature) {
        super(id, ingredient, output, biomes, temperature);
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEE_SPAWNING_BIG.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return BEE_SPAWNING;
    }
}
