package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public abstract class TagOutputRecipe
{
    public final Map<Ingredient, IntArrayNBT> itemOutput;
    public static Map<String, Integer> modPreference = new HashMap<>();

    public TagOutputRecipe(Map<Ingredient, IntArrayNBT> itemOutput) {
        this.itemOutput = itemOutput;
    }

    public Map<ItemStack, IntArrayNBT> getRecipeOutputs() {
        Map<ItemStack, IntArrayNBT> output = new IdentityHashMap<>();

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

    public static Fluid getPreferredFluidByMod(String fluidName) {
        // Try loading from fluid registry
        Fluid preferredFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

        // Try loading fluid from fluid tag
        if (preferredFluid == null || preferredFluid.equals(Fluids.EMPTY)) {
            try {
                Tag<Fluid> fluidTag = FluidTags.getCollection().get(new ResourceLocation(fluidName));
                if (fluidTag.getAllElements().size() > 0) {
                    int currBest = getModPreference().size();
                    for (Fluid fluid: fluidTag.getAllElements()) {
                        ResourceLocation rl = fluid.getRegistryName();
                        if(rl != null) {
                            String modId = rl.getNamespace();
                            int priority = 100;
                            if (getModPreference().containsKey(modId)) {
                                priority = getModPreference().get(modId);
                            };
                            if (preferredFluid == null || (priority >= 0 && priority < currBest)) {
                                preferredFluid = fluid;
                                currBest = priority;
                            }
                        }
                    }
                    preferredFluid = fluidTag.getAllElements().iterator().next();
                }
            } catch (Exception e) {
                // Who cares
            }
        }

        return preferredFluid;
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
