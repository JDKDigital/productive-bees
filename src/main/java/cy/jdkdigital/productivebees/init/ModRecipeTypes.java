package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModRecipeTypes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ProductiveBees.MODID);

    public static final RegistryObject<RecipeSerializer<?>> ADVANCED_BEEHIVE = createRecipeType("advanced_beehive", () -> new AdvancedBeehiveRecipe.Serializer<>(AdvancedBeehiveRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGE = createRecipeType("centrifuge", () -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BOTTLER = createRecipeType("bottler", () -> new BottlerRecipe.Serializer<>(BottlerRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> INCUBATION = createRecipeType("incubation", () -> new IncubationRecipe.Serializer<>(IncubationRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_BREEDING = createRecipeType("bee_breeding", () -> new BeeBreedingRecipe.Serializer<>(BeeBreedingRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_CONVERSION = createRecipeType("bee_conversion", () -> new BeeConversionRecipe.Serializer<>(BeeConversionRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_FISHING = createRecipeType("bee_fishing", () -> new BeeFishingRecipe.Serializer<>(BeeFishingRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_SPAWNING = createRecipeType("bee_spawning", () -> new BeeSpawningRecipe.Serializer<>(BeeSpawningRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_SPAWNING_BIG = createRecipeType("bee_spawning_big", () -> new BeeSpawningBigRecipe.Serializer<>(BeeSpawningBigRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_NBT_CHANGER = createRecipeType("bee_nbt_changer", () -> new BeeNBTChangerRecipe.Serializer<>(BeeNBTChangerRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> WOOD_CHIP = createRecipeType("wood_chip", () -> new WoodChipRecipe.Serializer<>(WoodChipRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> STONE_CHIP = createRecipeType("stone_chip", () -> new StoneChipRecipe.Serializer<>(StoneChipRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> GENE_TREAT = createRecipeType("gene_treat", () -> new HoneyTreatGeneRecipe.Serializer<>(HoneyTreatGeneRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> GENE_GENE = createRecipeType("gene_gene", () -> new CombineGeneRecipe.Serializer<>(CombineGeneRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_CAGE_BOMB = createRecipeType("bee_cage_bomb", () -> new BeeBombBeeCageRecipe.Serializer<>(BeeBombBeeCageRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> CONFIGURABLE_HONEYCOMB = createRecipeType("configurable_honeycomb", () -> new ConfigurableHoneycombRecipe.Serializer<>(ConfigurableHoneycombRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> CONFIGURABLE_COMB_BLOCK = createRecipeType("configurable_comb_block", () -> new ConfigurableCombBlockRecipe.Serializer<>(ConfigurableCombBlockRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BLOCK_CONVERSION = createRecipeType("block_conversion", () -> new BlockConversionRecipe.Serializer<>(BlockConversionRecipe::new));

    public static <B extends RecipeSerializer<?>> RegistryObject<B> createRecipeType(String name, Supplier<? extends B> supplier) {
        return RECIPE_SERIALIZERS.register(name, supplier);
    }

    public static RecipeType<AdvancedBeehiveRecipe> ADVANCED_BEEHIVE_TYPE;
    public static RecipeType<BeeBreedingRecipe> BEE_BREEDING_TYPE;
    public static RecipeType<BlockConversionRecipe> BLOCK_CONVERSION_TYPE;
    public static RecipeType<BeeConversionRecipe> BEE_CONVERSION_TYPE;
    public static RecipeType<IncubationRecipe> INCUBATION_TYPE;
    public static RecipeType<BeeFishingRecipe> BEE_FISHING_TYPE;
    public static RecipeType<BeeSpawningRecipe> BEE_SPAWNING_TYPE;
    public static RecipeType<BeeSpawningBigRecipe> BEE_SPAWNING_BIG_TYPE;
    public static RecipeType<BeeNBTChangerRecipe> BEE_NBT_CHANGER_TYPE;
    public static RecipeType<BottlerRecipe> BOTTLER_TYPE;
    public static RecipeType<CentrifugeRecipe> CENTRIFUGE_TYPE;

    public static void registerTypes() {
        ADVANCED_BEEHIVE_TYPE = RecipeType.register(ProductiveBees.MODID + ":advanced_beehive");
        BEE_BREEDING_TYPE = RecipeType.register(ProductiveBees.MODID + ":bee_breeding");
        BLOCK_CONVERSION_TYPE = RecipeType.register(ProductiveBees.MODID + ":block_conversion");
        BEE_CONVERSION_TYPE = RecipeType.register(ProductiveBees.MODID + ":bee_conversion");
        INCUBATION_TYPE = RecipeType.register(ProductiveBees.MODID + ":incubation");
        BEE_FISHING_TYPE = RecipeType.register(ProductiveBees.MODID + ":bee_fishing");
        BEE_SPAWNING_TYPE = RecipeType.register(ProductiveBees.MODID + ":bee_spawning");
        BEE_SPAWNING_BIG_TYPE = RecipeType.register(ProductiveBees.MODID + ":bee_spawning_big");
        BEE_NBT_CHANGER_TYPE = RecipeType.register(ProductiveBees.MODID + ":bee_nbt_changer");
        BOTTLER_TYPE = RecipeType.register(ProductiveBees.MODID + ":bottler");
        CENTRIFUGE_TYPE = RecipeType.register(ProductiveBees.MODID + ":centrifuge");
    }
}
