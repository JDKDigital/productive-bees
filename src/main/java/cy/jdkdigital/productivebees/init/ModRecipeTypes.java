package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.*;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModRecipeTypes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ProductiveBees.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ProductiveBees.MODID);

    public static final RegistryObject<RecipeSerializer<?>> ADVANCED_BEEHIVE = RECIPE_SERIALIZERS.register("advanced_beehive", () -> new AdvancedBeehiveRecipe.Serializer<>(AdvancedBeehiveRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGE = RECIPE_SERIALIZERS.register("centrifuge", () -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BOTTLER = RECIPE_SERIALIZERS.register("bottler", () -> new BottlerRecipe.Serializer<>(BottlerRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> INCUBATION = RECIPE_SERIALIZERS.register("incubation", () -> new IncubationRecipe.Serializer<>(IncubationRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_BREEDING = RECIPE_SERIALIZERS.register("bee_breeding", () -> new BeeBreedingRecipe.Serializer<>(BeeBreedingRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_CONVERSION = RECIPE_SERIALIZERS.register("bee_conversion", () -> new BeeConversionRecipe.Serializer<>(BeeConversionRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_SPAWNING = RECIPE_SERIALIZERS.register("bee_spawning", () -> new BeeSpawningRecipe.Serializer<>(BeeSpawningRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_SPAWNING_BIG = RECIPE_SERIALIZERS.register("bee_spawning_big", () -> new BeeSpawningBigRecipe.Serializer<>(BeeSpawningBigRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_NBT_CHANGER = RECIPE_SERIALIZERS.register("bee_nbt_changer", () -> new BeeNBTChangerRecipe.Serializer<>(BeeNBTChangerRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> WOOD_CHIP = RECIPE_SERIALIZERS.register("wood_chip", () -> new WoodChipRecipe.Serializer<>(WoodChipRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> STONE_CHIP = RECIPE_SERIALIZERS.register("stone_chip", () -> new StoneChipRecipe.Serializer<>(StoneChipRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> GENE_TREAT = RECIPE_SERIALIZERS.register("gene_treat", () -> new HoneyTreatGeneRecipe.Serializer<>(HoneyTreatGeneRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> GENE_GENE = RECIPE_SERIALIZERS.register("gene_gene", () -> new CombineGeneRecipe.Serializer<>(CombineGeneRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BEE_CAGE_BOMB = RECIPE_SERIALIZERS.register("bee_cage_bomb", () -> new BeeBombBeeCageRecipe.Serializer<>(BeeBombBeeCageRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> CONFIGURABLE_HONEYCOMB = RECIPE_SERIALIZERS.register("configurable_honeycomb", () -> new ConfigurableHoneycombRecipe.Serializer<>(ConfigurableHoneycombRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> CONFIGURABLE_COMB_BLOCK = RECIPE_SERIALIZERS.register("configurable_comb_block", () -> new ConfigurableCombBlockRecipe.Serializer<>(ConfigurableCombBlockRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BLOCK_CONVERSION = RECIPE_SERIALIZERS.register("block_conversion", () -> new BlockConversionRecipe.Serializer<>(BlockConversionRecipe::new));

    public static RegistryObject<RecipeType<AdvancedBeehiveRecipe>> ADVANCED_BEEHIVE_TYPE = registerRecipeType("advanced_beehive");
    public static RegistryObject<RecipeType<BeeBreedingRecipe>> BEE_BREEDING_TYPE = registerRecipeType("bee_breeding");
    public static RegistryObject<RecipeType<BlockConversionRecipe>> BLOCK_CONVERSION_TYPE = registerRecipeType("block_conversion");
    public static RegistryObject<RecipeType<BeeConversionRecipe>> BEE_CONVERSION_TYPE = registerRecipeType("bee_conversion");
    public static RegistryObject<RecipeType<IncubationRecipe>> INCUBATION_TYPE = registerRecipeType("incubation");
    public static RegistryObject<RecipeType<BeeSpawningBigRecipe>> BEE_SPAWNING_BIG_TYPE = registerRecipeType("bee_spawning_big");
    public static RegistryObject<RecipeType<BeeSpawningRecipe>> BEE_SPAWNING_TYPE = registerRecipeType("bee_spawning");
    public static RegistryObject<RecipeType<BeeNBTChangerRecipe>> BEE_NBT_CHANGER_TYPE = registerRecipeType("bee_nbt_changer");
    public static RegistryObject<RecipeType<BottlerRecipe>> BOTTLER_TYPE = registerRecipeType("bottler");
    public static RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE_TYPE = registerRecipeType("centrifuge");

    static <T extends Recipe<Container>> RegistryObject<RecipeType<T>> registerRecipeType(final String name) {
        return RECIPE_TYPES.register(name, () -> new RecipeType<T>() {
            public String toString() {
                return ProductiveBees.MODID + ":" + name;
            }
        });
    }
}
