package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.helper.RecipeHelper;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

@EmiEntrypoint
public class ProductiveBeesEmiPlugin implements EmiPlugin
{
    public static final EmiRecipeCategory BEE_PRODUCE_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_produce"),
            EmiStack.of(ModBlocks.HIVES.get("advanced_oak_beehive").get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_produce.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BEE_BREEDING_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_breeding"),
            BeeEmiStack.of(BeeIngredientFactory.getIngredient("productivebees:iron").get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_breeding.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BEE_CONVERSION_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_conversion"),
            BeeEmiStack.of(BeeIngredientFactory.getIngredient("productivebees:diamond").get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_conversion.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BEE_FISHING_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_fishing"),
            EmiStack.of(Items.FISHING_ROD),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_fishing.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BEE_SPAWNING_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_spawning"),
            EmiStack.of(ModBlocks.COARSE_DIRT_NEST.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_spawning.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BLOCK_CONVERSION_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block_conversion"),
            EmiStack.of(Items.COBBLESTONE),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/block_conversion.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory ITEM_CONVERSION_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "item_conversion"),
            EmiStack.of(ModBlocks.FEEDER.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/item_conversion.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory CENTRIFUGE_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge"),
            EmiStack.of(ModBlocks.CENTRIFUGE.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/centrifuge.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BLOCK_CENTRIFUGE_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block_centrifuge"),
            EmiStack.of(ModBlocks.HEATED_CENTRIFUGE.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/centrifuge.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory BEE_FLOWERING_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_flowering"),
            EmiStack.of(Items.POPPY),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_flowering.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory INCUBATION_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "incubator"),
            EmiStack.of(ModBlocks.INCUBATOR.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/emi/icon/bee_flowering.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {
        // TODO make bee ingredient favoritable
        BeeIngredientFactory.getOrCreateList(true).values().forEach(beeIngredient -> {
            registry.addEmiStack(BeeEmiStack.of(beeIngredient));
        });

        registry.removeEmiStacks(EmiStack.of(ModItems.ADV_BREED_BEE.get()));
        registry.removeEmiStacks(EmiStack.of(ModItems.ADV_BREED_ALL_BEES.get()));

        registry.addCategory(BEE_PRODUCE_CATEGORY);
        registry.addCategory(BEE_BREEDING_CATEGORY);
        registry.addCategory(BEE_CONVERSION_CATEGORY);
        registry.addCategory(BEE_FISHING_CATEGORY);
        registry.addCategory(BEE_SPAWNING_CATEGORY);
        registry.addCategory(BLOCK_CONVERSION_CATEGORY);
        registry.addCategory(ITEM_CONVERSION_CATEGORY);
        registry.addCategory(CENTRIFUGE_CATEGORY);
        registry.addCategory(BLOCK_CENTRIFUGE_CATEGORY);
        registry.addCategory(BEE_FLOWERING_CATEGORY);
        registry.addCategory(INCUBATION_CATEGORY);
        
        // Workstations
        registry.addWorkstation(BEE_PRODUCE_CATEGORY, EmiStack.of(ModBlocks.HIVES.get("advanced_oak_beehive").get()));
        registry.addWorkstation(BEE_BREEDING_CATEGORY, EmiStack.of(ModBlocks.BREEDING_CHAMBER.get()));
        registry.addWorkstation(CENTRIFUGE_CATEGORY, EmiStack.of(ModBlocks.CENTRIFUGE.get()));
        registry.addWorkstation(CENTRIFUGE_CATEGORY, EmiStack.of(ModBlocks.POWERED_CENTRIFUGE.get()));
        registry.addWorkstation(CENTRIFUGE_CATEGORY, EmiStack.of(ModBlocks.HEATED_CENTRIFUGE.get()));
        registry.addWorkstation(BLOCK_CENTRIFUGE_CATEGORY, EmiStack.of(ModBlocks.HEATED_CENTRIFUGE.get()));
        registry.addWorkstation(BEE_SPAWNING_CATEGORY, EmiStack.of(ModBlocks.COARSE_DIRT_NEST.get()));
//        registry.addWorkstation(EmiStack.of(ModBlocks.BOTTLER.get()), BOTTLER_TYPE);
        registry.addWorkstation(ITEM_CONVERSION_CATEGORY, EmiStack.of(ModBlocks.FEEDER.get()));
        registry.addWorkstation(INCUBATION_CATEGORY, EmiStack.of(ModBlocks.INCUBATOR.get()));

        // Component items
        registry.setDefaultComparison(ModItems.CONFIGURABLE_HONEYCOMB.get(), Comparison.compareData(stack -> stack.get(ModDataComponents.BEE_TYPE.get())));
        registry.setDefaultComparison(ModItems.CONFIGURABLE_COMB_BLOCK.get(), Comparison.compareData(stack -> stack.get(ModDataComponents.BEE_TYPE.get())));
        registry.setDefaultComparison(ModItems.CONFIGURABLE_SPAWN_EGG.get(), Comparison.compareData(stack -> stack.get(DataComponents.ENTITY_DATA)));

        RecipeManager recipeManager = registry.getRecipeManager();

        List<RecipeHolder<AdvancedBeehiveRecipe>> produceRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        produceRecipeList.forEach(recipeHolder -> registry.addRecipe(new BeeProduceEmiRecipe(recipeHolder)));

        List<RecipeHolder<BeeBreedingRecipe>> breedingRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        breedingRecipeList.forEach(recipeHolder -> registry.addRecipe(new BeeBreedingEmiRecipe(recipeHolder)));

        List<RecipeHolder<BeeConversionRecipe>> beeConversionRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
        beeConversionRecipeList.forEach(recipeHolder -> registry.addRecipe(new BeeConversionEmiRecipe(recipeHolder)));

        List<RecipeHolder<BeeFishingRecipe>> beeFishingRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_FISHING_TYPE.get());
        beeFishingRecipeList.forEach(recipeHolder -> registry.addRecipe(new BeeFishingEmiRecipe(recipeHolder)));

        List<RecipeHolder<BeeSpawningRecipe>> beeSpawningRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_SPAWNING_TYPE.get());
        beeSpawningRecipeList.forEach(recipeHolder -> registry.addRecipe(new BeeSpawningEmiRecipe(recipeHolder)));

        List<RecipeHolder<BlockConversionRecipe>> blockConversionRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
        blockConversionRecipeList.forEach(recipeHolder -> registry.addRecipe(new BlockConversionEmiRecipe(recipeHolder)));

        List<RecipeHolder<ItemConversionRecipe>> itemConversionRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.ITEM_CONVERSION_TYPE.get());
        itemConversionRecipeList.forEach(recipeHolder -> registry.addRecipe(new ItemConversionEmiRecipe(recipeHolder)));

        List<RecipeHolder<CentrifugeRecipe>> centrifugeRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        centrifugeRecipeList.forEach(recipeHolder -> registry.addRecipe(new CentrifugeEmiRecipe(recipeHolder)));

        List<RecipeHolder<CentrifugeRecipe>> blockCentrifugeRecipesMap = centrifugeRecipeList.stream().map(recipe -> {
            var item = recipe.value().ingredient.getItems()[0];
            if (item.getItem() instanceof HoneycombItem) {
                List<TagOutputRecipe.ChancedOutput> outputs = new ArrayList<>();
                recipe.value().itemOutput.forEach((chanceOutput) -> {
                    outputs.add(new TagOutputRecipe.ChancedOutput(chanceOutput.ingredient(), chanceOutput.min() * 4, chanceOutput.max() * 4, chanceOutput.chance()));
                });
                return new RecipeHolder<>(recipe.id().withPath(p -> "/" + p + "_block"), new CentrifugeRecipe(Ingredient.of(BeeHelper.getCombBlockFromHoneyComb(item)), outputs, recipe.value().fluidOutput != null ? new FluidStack(recipe.value().fluidOutput.getFluid(), recipe.value().fluidOutput.getAmount() * 4) : null, recipe.value().getProcessingTime()));
            }
            return null;
        }).filter(Objects::nonNull).toList();
        blockCentrifugeRecipesMap.forEach(recipeHolder -> registry.addRecipe(new BlockCentrifugeEmiRecipe(recipeHolder)));

        Map<String, BeeIngredient> beeList = BeeIngredientFactory.getOrCreateList();
        // Bee flowering requirements
        RecipeHelper.getFlowersRecipes(beeList).forEach(recipeHolder -> registry.addRecipe(new BeeFloweringEmiRecipe(recipeHolder)));
        // Incubation recipes
        RecipeHelper.getRecipes(beeList).forEach(recipeHolder -> registry.addRecipe(new IncubationEmiRecipe(recipeHolder)));

        addBlockDupeRecipes(registry);
        addBeeInfo(registry);
        addCombInfo(registry);
        addNestInfo(registry);
    }

    private void addBlockDupeRecipes(EmiRegistry registry) {
        // Quarry and lumber bee recipes
        Collection<AdvancedBeehiveRecipe> chipHiveRecipes = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.QUARRY).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (!b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                List<TagOutputRecipe.ChancedOutput> blockItemOutput = new ArrayList<>();
                blockItemOutput.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(b.asItem()), 1, 1, 1f));
                registry.addRecipe(new BeeProduceEmiRecipe(new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "/quarry_bee/" + blockHolder.getRegisteredName().replace(":", "_")), new AdvancedBeehiveRecipe(BeeIngredientFactory.getIngredient("productivebees:quarry_bee"), blockItemOutput))));
            }
        });
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.LUMBER).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (!b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                List<TagOutputRecipe.ChancedOutput> blockItemOutput = new ArrayList<>();
                blockItemOutput.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(b.asItem()), 1, 1, 1f));
                registry.addRecipe(new BeeProduceEmiRecipe(new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "/lumber_bee/" + blockHolder.getRegisteredName().replace(":", "_")), new AdvancedBeehiveRecipe(BeeIngredientFactory.getIngredient("productivebees:lumber_bee"), blockItemOutput))));
            }
        });
    }

    private void addBeeInfo(EmiRegistry registry) {
        // Bee ingredient descriptions
        Map<String, BeeIngredient> beeList = BeeIngredientFactory.getOrCreateList();
        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            if (entry.getKey().contains(ProductiveBees.MODID)) {
                String beeId = entry.getKey().replace("productivebees:", "");
                List<Component> descriptions = new ArrayList<>();
                if (entry.getValue().isConfigurable()) {
                    CompoundTag nbt = BeeReloadListener.INSTANCE.getData(entry.getKey());
                    if (nbt.contains("description")) {
                        descriptions.add(Component.translatable(nbt.getString("description")));
                    } else {
                        var description = Component.translatable("productivebees.ingredient.description." + beeId + "_bee");
                        if (!description.getString().equals("productivebees.ingredient.description." + beeId + "_bee")) {
                            descriptions.add(description);
                        }
                    }
                    if (!nbt.getBoolean("selfbreed")) {
                        descriptions.add(Component.translatable("productivebees.ingredient.description.selfbreed"));
                    }
                } else {
                    descriptions.add(Component.translatable("productivebees.ingredient.description." + beeId));
                    if (beeId.equals("lumber_bee") || beeId.equals("quarry_bee") || beeId.equals("rancher_bee") || beeId.equals("collector_bee") || beeId.equals("hoarder_bee") || beeId.equals("farmer_bee") || beeId.equals("cupid_bee")) {
                        descriptions.add(Component.translatable("productivebees.ingredient.description.selfbreed"));
                    }
                }

                if (!descriptions.isEmpty()) {
                    registry.addRecipe(new EmiInfoRecipe(List.of(BeeEmiStack.of(entry.getValue())), descriptions, null));
                }
            }
        }
    }

    private void addNestInfo(EmiRegistry registry) {
        List<String> itemInfos = Arrays.asList(
                "inactive_dragon_egg",
                "dragon_egg_hive",
                "bumble_bee_nest",
                "sugar_cane_nest",
                "slimy_nest",
                "stone_nest",
                "sand_nest",
                "snow_nest",
                "gravel_nest",
                "coarse_dirt_nest",
                "oak_wood_nest",
                "spruce_wood_nest",
                "acacia_wood_nest",
                "dark_oak_wood_nest",
                "jungle_wood_nest",
                "birch_wood_nest",
                "end_stone_nest",
                "obsidian_nest",
                "glowstone_nest",
                "soul_sand_nest",
                "nether_brick_nest",
                "nether_quartz_nest"
        );
        for (String itemName : itemInfos) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, itemName));
            registry.addRecipe(new EmiInfoRecipe(List.of(EmiIngredient.of(Ingredient.of(item))), List.of(Component.translatable("productivebees.ingredient.description." + itemName)), null));
        }
    }

    private void addCombInfo(EmiRegistry registry) {
        RecipeManager recipeManager = registry.getRecipeManager();

        Optional<RecipeHolder<?>> honeycombRecipe = recipeManager.byKey(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "comb_block/configurable_honeycomb"));
        int count = 4;
        if (honeycombRecipe.isPresent()) {
            count = ((ConfigurableHoneycombRecipe) honeycombRecipe.get().value()).count;
        }
        for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            ResourceLocation beeType = entry.getKey();
            ResourceLocation idComb = beeType.withPath(p -> p + "_honeycomb");
            ResourceLocation idCombBlock = beeType.withPath(p -> p + "_comb");

            // Add comb item
            ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
            BeeCreator.setType(beeType, comb);
            NonNullList<EmiIngredient> combInput = NonNullList.create();
            for (int i = 0; i < count; i++) {
                combInput.add(EmiIngredient.of(Ingredient.of(comb)));
            }

            // Add comb block
            ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
            BeeCreator.setType(beeType, combBlock);

            registry.addRecipe(new EmiCraftingRecipe(List.of(EmiIngredient.of(combInput)), EmiStack.of(combBlock), idComb));
            ItemStack combOutput = comb.copy();
            combOutput.setCount(count);
            registry.addRecipe(new EmiCraftingRecipe(List.of(EmiIngredient.of(Ingredient.of(combBlock))), EmiStack.of(combOutput), idCombBlock));
        }
    }
}
