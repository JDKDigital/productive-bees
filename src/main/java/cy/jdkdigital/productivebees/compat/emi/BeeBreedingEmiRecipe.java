package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BeeBreedingEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_breeding_recipe.png");

    public BeeBreedingEmiRecipe(RecipeHolder<BeeBreedingRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BEE_BREEDING_CATEGORY, recipe.id(), 126, 70);

        this.inputs.add(BeeEmiStack.of(recipe.value().parent1.get()));
        this.inputs.add(BeeEmiStack.of(recipe.value().parent2.get()));

        Entity bee1 = recipe.value().parent1.get().getCachedEntity(Minecraft.getInstance().level);
        if (bee1 instanceof ProductiveBee pBee) {
            this.inputs.add(EmiIngredient.of(pBee.getBreedingItems().stream().map(EmiStack::of).toList()));
        } else {
            this.inputs.add(EmiIngredient.of(ItemTags.FLOWERS));
        }
        Entity bee2 = recipe.value().parent2.get().getCachedEntity(Minecraft.getInstance().level);
        if (bee2 instanceof ProductiveBee pBee) {
            this.inputs.add(EmiIngredient.of(pBee.getBreedingItems().stream().map(EmiStack::of).toList()));
        } else {
            this.inputs.add(EmiIngredient.of(ItemTags.FLOWERS));
        }

        this.outputs.add(BeeEmiStack.of(recipe.value().offspring.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 126, 70, 0, 0);

        widgets.addSlot(this.inputs.get(0), 11, 16).drawBack(false);
        widgets.addSlot(this.inputs.get(1), 45, 16).drawBack(false);
        widgets.addSlot(this.outputs.get(0), 104, 17).drawBack(false);

        widgets.addSlot(this.inputs.get(2), 9, 37).drawBack(false);
        widgets.addSlot(this.inputs.get(3), 43, 37).drawBack(false);
    }
}
