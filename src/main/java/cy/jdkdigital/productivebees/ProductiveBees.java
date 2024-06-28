package cy.jdkdigital.productivebees;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.capabilities.attributes.BeeAttributesHandler;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.common.crafting.ingredient.HoneycombIngredient;
import cy.jdkdigital.productivebees.init.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(ProductiveBees.MODID)
public final class ProductiveBees
{
    public static final String MODID = "productivebees";
    public static final RandomSource random = RandomSource.create();

    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRIES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, MODID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_POOL_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_POOL_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MODID);
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(MODID, MODID));
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(MODID, () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(Items.BEE_NEST))
                .title(Component.literal("Productive Bees"))
                .build();
    });

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<BeeExistsCondition>> BEE_EXISTS_CONDITION = CONDITION_CODECS.register("bee_exists", () -> BeeExistsCondition.CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<HoneycombIngredient>> HONEYCOMB_INGREDIENT_TYPE = INGREDIENT_TYPES.register("component", () -> new IngredientType<>(HoneycombIngredient.CODEC));

    public static final Supplier<AttachmentType<BeeAttributesHandler>> ATTRIBUTE_HANDLER = ATTACHMENT_TYPES.register("attributes_handler", () -> AttachmentType.serializable(BeeAttributesHandler::new).build());

    public ProductiveBees(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.registerHives();

        ModPointOfInterestTypes.POI_TYPES.register(modEventBus);
        ModProfessions.PROFESSIONS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ModEntities.HIVE_BEES.register(modEventBus);
        ModEntities.SOLITARY_BEES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModFeatures.TREE_DECORATORS.register(modEventBus);
        ModFeatures.BIOME_MODIFIERS.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        TRIGGER_TYPES.register(modEventBus);
        LOOT_SERIALIZERS.register(modEventBus);
        LOOT_POOL_ENTRIES.register(modEventBus);
        LOOT_POOL_FUNCTIONS.register(modEventBus);
        LOOT_POOL_CONDITIONS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        CONDITION_CODECS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        INGREDIENT_TYPES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        // Config loading
        modContainer.registerConfig(ModConfig.Type.SERVER, ProductiveBeesConfig.SERVER_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ProductiveBeesConfig.CLIENT_CONFIG);

        NeoForgeMod.enableMilkFluid();
        ModDataComponents.register();
        ModAdvancements.register();
        ModProfessions.register();
    }

    public static boolean includeMod(String modId) {
        return ModList.get().isLoaded(modId); // !FMLLoader.isProduction() ||
    }
}
