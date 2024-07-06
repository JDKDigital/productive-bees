package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.biome.Biome;

public class BeeSpawningEmiRecipe extends BasicEmiRecipe
{
    private final HolderSet<Biome> biomes;
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_spawning_recipe.png");

    public BeeSpawningEmiRecipe(RecipeHolder<BeeSpawningRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BEE_SPAWNING_CATEGORY, recipe.id(), 126, 70);

        this.inputs.add(EmiIngredient.of(recipe.value().ingredient));
        this.inputs.add(EmiIngredient.of(recipe.value().spawnItem));
        recipe.value().output.forEach(beeIngredientSupplier -> this.outputs.add(BeeEmiStack.of(beeIngredientSupplier.get())));

        this.biomes = recipe.value().biomes;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 126, 70, 0, 0);

        widgets.addSlot(this.inputs.get(0), 35, 26);
        widgets.addSlot(this.inputs.get(1), 10, 26);

        widgets.addSlot(this.outputs.get(0), 95, 27).drawBack(false).recipeContext(this);

//        int xPos = 0;
//        AtomicInteger yPos = new AtomicInteger(65);
//        Minecraft minecraft = Minecraft.getInstance();
//        if (minecraft.level != null) {
//            var biomeRegistry = minecraft.level.registryAccess().registryOrThrow(Registries.BIOME);
//            for (Holder<Biome> biome : biomes) {
//                var key = biomeRegistry.getKey(biome.value());
//                if (key != null) {
//                    widgets.addText(Component.translatable("biome.minecraft." + key.getPath()), xPos, yPos.get(), 0xFF000000, false);
//                    yPos.addAndGet(minecraft.font.lineHeight + 2);
//                }
//            }
//        }
    }
}
