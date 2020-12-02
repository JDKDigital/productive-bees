package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TagOutputRecipe
{
    public final Map<Ingredient, IntArrayNBT> itemOutput;
    public static Map<String, Integer> modPreference = new HashMap<>();

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
        int currBest = getModPreference().size();
        for(ItemStack item : list) {
            ResourceLocation rl = item.getItem().getRegistryName();
            if(rl != null) {
                String modId = rl.getNamespace();
                int priority = 100;
                if (getModPreference().containsKey(modId)) {
                    priority = getModPreference().get(modId);
                };
                if (preferredItem == null || (priority >= 0 && priority < currBest)) {
                    preferredItem = item;
                    currBest = priority;
                }
            }
        }
        return preferredItem;
    }

    private static Map<String, Integer> getModPreference() {
        if (modPreference.size() > 0) {
            return modPreference;
        }

        int priority = 0;
        for(String modId: ProductiveBeesConfig.GENERAL.preferredTagSource.get()) {
            if (ModList.get().isLoaded(modId)) {
                modPreference.put(modId, ++priority);
            }
        }

        return modPreference;
    }
}
