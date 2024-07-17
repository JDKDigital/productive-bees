package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProductiveBees.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ProductiveBees.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> ADVANCED_BEEHIVE = RECIPE_SERIALIZERS.register("advanced_beehive", AdvancedBeehiveRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CENTRIFUGE = RECIPE_SERIALIZERS.register("centrifuge", CentrifugeRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BOTTLER = RECIPE_SERIALIZERS.register("bottler", BottlerRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> INCUBATION = RECIPE_SERIALIZERS.register("incubation", IncubationRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BEE_BREEDING = RECIPE_SERIALIZERS.register("bee_breeding", BeeBreedingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BEE_CONVERSION = RECIPE_SERIALIZERS.register("bee_conversion", BeeConversionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BEE_FISHING = RECIPE_SERIALIZERS.register("bee_fishing", BeeFishingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BEE_SPAWNING = RECIPE_SERIALIZERS.register("bee_spawning", BeeSpawningRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BEE_NBT_CHANGER = RECIPE_SERIALIZERS.register("bee_nbt_changer", BeeNBTChangerRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> GENE_TREAT = RECIPE_SERIALIZERS.register("gene_treat", HoneyTreatGeneRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> GENE_GENE = RECIPE_SERIALIZERS.register("gene_gene", CombineGeneRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BEE_CAGE_BOMB = RECIPE_SERIALIZERS.register("bee_cage_bomb", BeeBombBeeCageRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CONFIGURABLE_HONEYCOMB = RECIPE_SERIALIZERS.register("configurable_honeycomb", ConfigurableHoneycombRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CONFIGURABLE_COMB_BLOCK = RECIPE_SERIALIZERS.register("configurable_comb_block", ConfigurableCombBlockRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BLOCK_CONVERSION = RECIPE_SERIALIZERS.register("block_conversion", BlockConversionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> ITEM_CONVERSION = RECIPE_SERIALIZERS.register("item_conversion", ItemConversionRecipe.Serializer::new);

    public static DeferredHolder<RecipeType<?>, RecipeType<AdvancedBeehiveRecipe>> ADVANCED_BEEHIVE_TYPE = registerRecipeType("advanced_beehive");
    public static DeferredHolder<RecipeType<?>, RecipeType<BeeBreedingRecipe>> BEE_BREEDING_TYPE = registerRecipeType("bee_breeding");
    public static DeferredHolder<RecipeType<?>, RecipeType<BlockConversionRecipe>> BLOCK_CONVERSION_TYPE = registerRecipeType("block_conversion");
    public static DeferredHolder<RecipeType<?>, RecipeType<ItemConversionRecipe>> ITEM_CONVERSION_TYPE = registerRecipeType("item_conversion");
    public static DeferredHolder<RecipeType<?>, RecipeType<BeeConversionRecipe>> BEE_CONVERSION_TYPE = registerRecipeType("bee_conversion");
    public static DeferredHolder<RecipeType<?>, RecipeType<IncubationRecipe>> INCUBATION_TYPE = registerRecipeType("incubation");
    public static DeferredHolder<RecipeType<?>, RecipeType<BeeFishingRecipe>> BEE_FISHING_TYPE = registerRecipeType("bee_fishing");
    public static DeferredHolder<RecipeType<?>, RecipeType<BeeSpawningRecipe>> BEE_SPAWNING_TYPE = registerRecipeType("bee_spawning");
    public static DeferredHolder<RecipeType<?>, RecipeType<BeeNBTChangerRecipe>> BEE_NBT_CHANGER_TYPE = registerRecipeType("bee_nbt_changer");
    public static DeferredHolder<RecipeType<?>, RecipeType<BottlerRecipe>> BOTTLER_TYPE = registerRecipeType("bottler");
    public static DeferredHolder<RecipeType<?>, RecipeType<CentrifugeRecipe>> CENTRIFUGE_TYPE = registerRecipeType("centrifuge");

    static <T extends Recipe<RecipeInput>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerRecipeType(final String name) {
        return RECIPE_TYPES.register(name, () -> new RecipeType<T>() {});
    }
}
