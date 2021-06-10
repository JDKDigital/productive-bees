package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

public class BeeSpawningBigRecipe extends BeeSpawningRecipe
{
    public static final IRecipeType<BeeSpawningBigRecipe> BEE_SPAWNING = IRecipeType.register(ProductiveBees.MODID + ":bee_spawning_big");

    public BeeSpawningBigRecipe(ResourceLocation id, Ingredient ingredient, List<Lazy<BeeIngredient>> output, List<String> biomes, String temperature) {
        super(id, ingredient, output, biomes, temperature);
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEE_SPAWNING_BIG.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_SPAWNING;
    }
}
