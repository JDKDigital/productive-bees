package cy.jdkdigital.productivebees.recipe;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public abstract class TagOutputRecipe
{
    public final Map<Ingredient, IntArrayNBT> itemOutput;
    public final Map<ItemStack, IntArrayNBT> calculatedItemOutput = new LinkedHashMap<>();
    public static Map<String, Integer> modPreference = new HashMap<>();

    public TagOutputRecipe(Ingredient itemOutput) {
        this.itemOutput = new LinkedHashMap<>();
        this.itemOutput.put(itemOutput, new IntArrayNBT(new int[]{1, 1, 100}));
    }

    public TagOutputRecipe(Map<Ingredient, IntArrayNBT> itemOutput) {
        this.itemOutput = itemOutput;
    }

    public Map<ItemStack, IntArrayNBT> getRecipeOutputs() {
        if (calculatedItemOutput.isEmpty() && !itemOutput.isEmpty()) {
            itemOutput.forEach((ingredient, intNBT) -> {
                ItemStack preferredItem = getPreferredItemByMod(ingredient);
                if (preferredItem != null && !preferredItem.getItem().equals(Items.BARRIER)) {
                    calculatedItemOutput.put(preferredItem, intNBT);
                }
            });
        }

        return new LinkedHashMap<>(calculatedItemOutput);
    }

    private static ItemStack getPreferredItemByMod(Ingredient ingredient) {
        List<ItemStack> stacks = Arrays.asList(ingredient.getItems());
        return getPreferredItemByMod(stacks);
    }

    private static ItemStack getPreferredItemByMod(List<ItemStack> list) {
        ItemStack preferredItem = null;
        int currBest = getModPreference().size();
        for (ItemStack item : list) {
            ResourceLocation rl = item.getItem().getRegistryName();
            if (rl != null) {
                String modId = rl.getNamespace();
                int priority = 100;
                if (getModPreference().containsKey(modId)) {
                    priority = getModPreference().get(modId);
                }
                if (preferredItem == null || (priority >= 0 && priority <= currBest)) {
                    preferredItem = item.copy();
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
                ITag<Fluid> fluidTag = TagCollectionManager.getInstance().getFluids().getTag(new ResourceLocation(fluidName));
                if (fluidTag != null && fluidTag.getValues().size() > 0) {
                    int currBest = 100;
                    for (Fluid fluid: fluidTag.getValues()) {
                        if (!fluid.isSource(fluid.defaultFluidState())) {
                            fluid = ((FlowingFluid) fluid).getSource();
                        }

                        if (!fluid.isSource(fluid.defaultFluidState())) {
                            continue;
                        }

                        ResourceLocation rl = fluid.getRegistryName();
                        if (rl != null) {
                            String modId = rl.getNamespace();
                            int priority = currBest;
                            if (getModPreference().containsKey(modId)) {
                                priority = getModPreference().get(modId);
                            }

                            if (preferredFluid == null || (priority >= 0 && priority <= currBest)) {
                                preferredFluid = fluid;
                                currBest = priority;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Who cares
            }
        }

        return preferredFluid;
    }

    public static List<Fluid> getAllFluidsFromName(String fluidName) {
        // Try loading from fluid registry
        List<Fluid> fluids = Collections.singletonList(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName)));

        // Try loading fluid from fluid tag
        if (fluids.get(0).equals(Fluids.EMPTY)) {
            try {
                ITag<Fluid> fluidTag = TagCollectionManager.getInstance().getFluids().getTag(new ResourceLocation(fluidName));
                if (fluidTag != null && fluidTag.getValues().size() > 0) {
                    return fluidTag.getValues();
                }
            } catch (Exception e) {
                // Who cares
            }
        }

        return fluids;
    }

    private static Map<String, Integer> getModPreference() {
        if (modPreference.size() > 0) {
            return modPreference;
        }

        int priority = 0;
        for (String modId : ProductiveBeesConfig.GENERAL.preferredTagSource.get()) {
            if (ModList.get().isLoaded(modId)) {
                modPreference.put(modId, ++priority);
            }
        }

        return modPreference;
    }
}
