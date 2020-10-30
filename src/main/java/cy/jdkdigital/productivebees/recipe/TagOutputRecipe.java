package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TagOutputRecipe
{
    public final Map<Ingredient, IntArrayNBT> itemOutput;

    public TagOutputRecipe(Map<Ingredient, IntArrayNBT> itemOutput) {
        this.itemOutput = itemOutput;
    }

    public Map<ItemStack, IntArrayNBT> getRecipeOutputs() {
        Map<ItemStack, IntArrayNBT> output = new HashMap<>();

        if (!itemOutput.isEmpty()) {
            itemOutput.forEach((ingredient, intNBTS) -> {
                ItemStack preferredItem = getPreferredItemByMod(ingredient);
                if (preferredItem != null && !preferredItem.getItem().equals(Items.BARRIER)) {
                    output.put(preferredItem.copy(), intNBTS.copy());
                }
            });
        }

        return output;
    }

    public static ItemStack getPreferredItemByMod(Ingredient ingredient) {
        List<ItemStack> stacks = Arrays.asList(ingredient.getMatchingStacks());
        return getPreferredItemByMod(stacks);
    }

    public static ItemStack getPreferredItemByMod(List<ItemStack> list) {
        ItemStack preferredItem = null;
        int currBest = ProductiveBees.modPreference.size();
        for(ItemStack item : list) {
            ResourceLocation rl = item.getItem().getRegistryName();
            if(rl != null) {
                String modId = rl.getNamespace();
                int priority = 100;
                if (ProductiveBees.modPreference.containsKey(modId)) {
                    priority = ProductiveBees.modPreference.get(modId);
                };
                if (preferredItem == null || (priority >= 0 && priority < currBest)) {
                    preferredItem = item;
                    currBest = priority;
                }
            }
        }
        return preferredItem;
    }
}
