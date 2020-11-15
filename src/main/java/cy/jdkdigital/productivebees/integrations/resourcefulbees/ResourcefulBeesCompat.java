package cy.jdkdigital.productivebees.integrations.resourcefulbees;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.config.Config;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModItemGroups;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.recipe.ConfigurableHoneycombRecipe;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ResourcefulBeesCompat
{
    static final public String MODID = "resourcefulbees";

    public static ItemStack getHoneyComb(BeeEntity beeEntity) {
        if (beeEntity instanceof ICustomBee) {
            return new ItemStack(((ICustomBee) beeEntity).getBeeData().getCombRegistryObject().get());
        }
        return ItemStack.EMPTY;
    }

    public static void createCentrifugeRecipesFromItems(RecipeManager recipeManager) {
        int combCount = getCombCount(recipeManager);
        IItemHandlerModifiable inputHandler = new InventoryHandlerHelper.ItemHandler(2);

        for (RegistryObject<Item> registryItem : ModItems.ITEMS.getEntries()) {
            Item item = registryItem.get();
            ResourceLocation regId = item.getRegistryName();
            if (regId.getPath().contains("honeycomb_")) {
                ItemStack comb = new ItemStack(item);
                inputHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, comb);

                CentrifugeRecipe recipe = BeeHelper.getCentrifugeRecipe(recipeManager, inputHandler);
                if (recipe != null) {
                    IRecipe<?> honeycombCentrifuge = centrifugeRecipe(recipe, comb, new ResourceLocation(regId.getNamespace(), regId.getPath() + "_dep"));
                    recipeManager.recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                }
            }
            else if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                regId = block.getRegistryName();
                if (regId.getPath().contains("comb_")) {
                    ItemStack combBlock = new ItemStack(item);

                    Item combItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(regId.getNamespace(), regId.getPath().replace("comb_", "honeycomb_")));
                    inputHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(combItem));

                    CentrifugeRecipe recipe = BeeHelper.getCentrifugeRecipe(recipeManager, inputHandler);

                    if (recipe != null) {
                        IRecipe<?> honeycombBlockCentrifuge = centrifugeHoneyCombBlockRecipe(recipe, combBlock, regId, combCount);
                        recipeManager.recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
                    }
                }
            }
        }
    }

    public static void createCentrifugeRecipes(RecipeManager recipeManager, ResourceLocation beeType) {
        try {
            IRecipe<?> honeycombCentrifuge = centrifugeRecipe(recipeManager, beeType);
            IRecipe<?> honeycombBlockCentrifuge = centrifugeHoneyCombBlockRecipe(recipeManager, beeType);

            recipeManager.recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
            recipeManager.recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
        } catch (Exception e) {
            ProductiveBees.LOGGER.debug("Failed to register compat recipes for {}", beeType.toString());
        }
    }

    private static IRecipe<?> centrifugeRecipe(RecipeManager recipeManager, ResourceLocation beeType) {
        ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
        ModItemGroups.ModItemGroup.setTag(beeType.toString(), comb);

        IItemHandlerModifiable inputHandler = new InventoryHandlerHelper.ItemHandler(2);
        inputHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, comb);

        CentrifugeRecipe recipe = BeeHelper.getCentrifugeRecipe(recipeManager, inputHandler);

        return centrifugeRecipe(recipe, comb, beeType);
    }

    private static IRecipe<?> centrifugeHoneyCombBlockRecipe(RecipeManager recipeManager, ResourceLocation beeType) {
        ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
        ModItemGroups.ModItemGroup.setTag(beeType.toString(), combBlock);

        ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
        ModItemGroups.ModItemGroup.setTag(beeType.toString(), comb);

        IItemHandlerModifiable inputHandler = new InventoryHandlerHelper.ItemHandler(2);
        inputHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, comb);

        CentrifugeRecipe recipe = BeeHelper.getCentrifugeRecipe(recipeManager, inputHandler);

        int combCount = getCombCount(recipeManager);

        return centrifugeHoneyCombBlockRecipe(recipe, combBlock, beeType, combCount);
    }

    private static IRecipe<?> centrifugeRecipe(CentrifugeRecipe recipe, ItemStack comb, ResourceLocation beeType) {
        List<Pair<ItemStack, Float>> outputList = getRecipeOutput(recipe, 1);

        return new com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe(
                new ResourceLocation(beeType.getNamespace(), beeType.getPath() + "_rbees_comb_centrifuge"),
                Ingredient.fromStacks(comb),
                outputList,
                Config.CENTRIFUGE_RECIPE_TIME.get() * 20,
                false
        );
    }

    private static IRecipe<?> centrifugeHoneyCombBlockRecipe(CentrifugeRecipe recipe, ItemStack combBlock, ResourceLocation beeType, int combCount) {
        List<Pair<ItemStack, Float>> outputList = getRecipeOutput(recipe, combCount);

        return new com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe(
                new ResourceLocation(beeType.getNamespace(), beeType.getPath() + "_rbees_combblock_centrifuge"),
                Ingredient.fromStacks(combBlock),
                outputList,
                Config.CENTRIFUGE_RECIPE_TIME.get() * 20,
                true
        );
    }

    private static List<Pair<ItemStack, Float>> getRecipeOutput(CentrifugeRecipe recipe, int multiplier) {
        Map<ItemStack, IntArrayNBT> outputs = recipe.getRecipeOutputs();

        List<Pair<ItemStack, Float>> outputList = new ArrayList<>();
        for (Map.Entry<ItemStack, IntArrayNBT> entry: outputs.entrySet()) {
            if (outputList.size() < 2) {
                int count = MathHelper.ceil((entry.getValue().get(0).getInt() + entry.getValue().get(1).getInt()) / 2f);
                ItemStack outputItemStack = entry.getKey().copy();
                outputItemStack.setCount(count * multiplier);
                outputList.add(Pair.of(outputItemStack, entry.getValue().get(2).getInt() / 100f));
            }
        }
        if (outputList.size() < 2) {
            Item wax = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ResourcefulBeesCompat.MODID, "wax"));
            outputList.add(Pair.of(new ItemStack(wax,  multiplier), 1.0f));
        }

        outputList.add(Pair.of(new ItemStack(Items.HONEY_BOTTLE, multiplier), 1.0f));

        // Fix ordering so wax is last
        if (outputList.get(0).getLeft().getItem().getRegistryName().toString().contains("wax")) {
            Collections.swap(outputList, 0, 1);
        }

        return outputList;
    }

    private static int getCombCount(RecipeManager recipeManager) {
        Optional<? extends IRecipe<?>> honeycombRecipe = recipeManager.getRecipe(new ResourceLocation(ProductiveBees.MODID, "comb_block/configurable_honeycomb"));
        int count = 4;
        if (honeycombRecipe.isPresent()) {
            count = ((ConfigurableHoneycombRecipe) honeycombRecipe.get()).count;
        }

        return count;
    }
}
