package cy.jdkdigital.productivebees.init;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItems
{
    public static final FoodProperties SUGARBAG = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.2F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 1), 0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1), 0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1200, 1), 0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1), 0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1), 0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1), 0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1), 0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.LUCK, 1200, 1), 0.1f)
            .build();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProductiveBees.MODID);

    public static final List<RegistryObject<Item>> SPAWN_EGGS = Lists.newArrayList();

//    public static final RegistryObject<Item> GUIDE_BOOK = createItem("guide_book", () -> new Item(new Item.Properties().stacksTo(1).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEY_BUCKET = createItem("honey_bucket", () -> new BucketItem(ModFluids.HONEY, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_CAGE = createItem("bee_cage", () -> new BeeCage(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> STURDY_BEE_CAGE = createItem("sturdy_bee_cage", () -> new SturdyBeeCage(new Item.Properties().stacksTo(16).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEY_TREAT = createItem("honey_treat", () -> new HoneyTreat(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> GENE_BOTTLE = createItem("gene_bottle", () -> new GeneBottle(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> GENE = createItem("gene", () -> new Gene(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> MILK_BOTTLE = createItem("milk_bottle", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_BOMB = createItem("bee_bomb", () -> new BeeBomb(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_BOMB_ANGRY = createItem("bee_bomb_angry", () -> new BeeBombAngry(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> NEST_LOCATOR = createItem("nest_locator", () -> new NestLocator(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> SUGARBAG_HONEYCOMB = createItem("sugarbag_honeycomb", () -> new Item(new Item.Properties().food(SUGARBAG).rarity(Rarity.EPIC).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> TREAT_ON_A_STICK = createItem("treat_on_a_stick", () -> new TreatOnAStick(new Item.Properties().durability(25).tab(ModItemGroups.PRODUCTIVE_BEES),7));
    public static final RegistryObject<Item> WAX = createItem("wax", () -> new HoneycombItem(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> BEE_NEST_DIAMOND_HELMET = createItem("bee_nest_diamond_helmet", () -> new BeeNestHelmet(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, (new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> CONFIGURABLE_HONEYCOMB = createItem("configurable_honeycomb", () -> new Honeycomb(new Item.Properties().tab(null), "#d2ab00"));

    public static final RegistryObject<Item> HONEYCOMB_GHOSTLY = createItem("honeycomb_ghostly", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_MILKY = createItem("honeycomb_milky", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_POWDERY = createItem("honeycomb_powdery", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> DRACONIC_DUST = createItem("draconic_dust", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> DRACONIC_CHUNK = createItem("draconic_chunk", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WITHER_SKULL_CHIP = createItem("wither_skull_chip", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WOOD_CHIP = createItem("wood_chip", () -> new WoodChip(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> STONE_CHIP = createItem("stone_chip", () -> new StoneChip(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));

    // Hive upgrades
    public static final RegistryObject<Item> UPGRADE_BASE = createItem("upgrade_base", () -> new Item(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> UPGRADE_PRODUCTIVITY = createItem("upgrade_productivity", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_HIGH_END_PRODUCTIVITY = createItem("upgrade_high_end_productivity", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_NUCLEAR_PRODUCTIVITY = createItem("upgrade_nuclear_productivity", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_COSMIC_PRODUCTIVITY = createItem("upgrade_cosmic_productivity", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> UPGRADE_TIME = createItem("upgrade_time", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_BREEDING = createItem("upgrade_breeding", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_NOT_BABEE = createItem("upgrade_not_babee", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_COMB_BLOCK = createItem("upgrade_comb_block", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_ANTI_TELEPORT = createItem("upgrade_anti_teleport", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_RANGE = createItem("upgrade_range", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_FILTER = createItem("upgrade_filter", () -> new FilterUpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_BEE_SAMPLER = createItem("upgrade_bee_sampler", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_SIMULATOR = createItem("upgrade_simulator", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> ADV_BREED_BEE = createItem("adv_breed_bee", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADV_BREED_ALL_BEES = createItem("adv_breed_all_bees", () -> new Item(new Item.Properties()));

    public static RegistryObject<Item> CONFIGURABLE_SPAWN_EGG;
    public static RegistryObject<Item> CONFIGURABLE_COMB_BLOCK;

    public static CreativeModeTab itemGroupCompat(String mods) {
        String[] modNames = mods.split(",");
        return ModList.get().isLoaded(modNames[0]) || (modNames.length > 1 && ModList.get().isLoaded(modNames[1])) ? ModItemGroups.PRODUCTIVE_BEES : null;
    }

    public static <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
        return ITEMS.register(name, supplier);
    }
}
