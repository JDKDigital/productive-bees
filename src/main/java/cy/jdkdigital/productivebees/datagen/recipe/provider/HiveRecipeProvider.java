package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class HiveRecipeProvider extends RecipeProvider implements IConditionBuilder
{
    public HiveRecipeProvider(PackOutput gen) {
        super(gen);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                ConditionalRecipe.builder()
                    .addCondition(
                            modLoaded(modid)
                    )
                    .addRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive).group("hives").pattern("WWW").pattern("CHC").pattern("FWS")
                            .define('W', type.planks())
                            .define('H', Ingredient.of(ModTags.Forge.HIVES))
                            .define('C', Ingredient.of(ModTags.Forge.HONEYCOMBS))
                            .define('F', Ingredient.of(ModTags.Forge.CAMPFIRES))
                            .define('S', Ingredient.of(Tags.Items.SHEARS))
                            .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                            ::save
                    )
                    .build(consumer, new ResourceLocation(ProductiveBees.MODID, "hives/advanced_" + name + "_beehive"));

                ConditionalRecipe.builder()
                    .addCondition(
                        modLoaded(modid)
                    )
                    .addRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box).group("expansion_boxes").pattern("WWW").pattern("WCW").pattern("WWW")
                            .define('W', type.planks())
                            .define('C', Ingredient.of(ModTags.Forge.HONEYCOMBS))
                            .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                            ::save
                    )
                    .build(consumer, new ResourceLocation(ProductiveBees.MODID, "expansion_boxes/expansion_box_" + name));
            });
        });
    }
}
