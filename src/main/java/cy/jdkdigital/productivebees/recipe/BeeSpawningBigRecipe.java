package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class BeeSpawningBigRecipe extends BeeSpawningRecipe
{
    public static final IRecipeType<BeeSpawningBigRecipe> BEE_SPAWNING = IRecipeType.register(ProductiveBees.MODID + ":bee_spawning_big");

    public BeeSpawningBigRecipe(ResourceLocation id, Ingredient ingredient, List<BeeIngredient> output, int repopulationCooldown) {
        super(id, ingredient, output, repopulationCooldown);
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ForgeRegistries.RECIPE_SERIALIZERS.getValue(ProduciveBeesJeiPlugin.CATEGORY_BEE_SPAWNING_BIG_UID);
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_SPAWNING;
    }
}
