package cy.jdkdigital.productivebees.init;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItems
{
    public static final FoodProperties SUGARBAG = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2F).alwaysEdible()
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


    public static final List<DeferredHolder<Item, ? extends Item>> SPAWN_EGGS = Lists.newArrayList();

    public static final DeferredHolder<Item, ? extends Item> HONEY_BUCKET = createItem("honey_bucket", () -> new BucketItem(ModFluids.HONEY.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredHolder<Item, ? extends Item> BEE_CAGE = createItem("bee_cage", () -> new BeeCage(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> STURDY_BEE_CAGE = createItem("sturdy_bee_cage", () -> new SturdyBeeCage(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, ? extends Item> HONEY_TREAT = createItem("honey_treat", () -> new HoneyTreat(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> GENE_BOTTLE = createItem("gene_bottle", () -> new GeneBottle(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> GENE = createItem("gene", () -> new Gene(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> MILK_BOTTLE = createItem("milk_bottle", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final DeferredHolder<Item, ? extends Item> BEE_BOMB = createItem("bee_bomb", () -> new BeeBomb(new Item.Properties(), false));
    public static final DeferredHolder<Item, ? extends Item> BEE_BOMB_ANGRY = createItem("bee_bomb_angry", () -> new BeeBomb(new Item.Properties(), true));
    public static final DeferredHolder<Item, ? extends Item> NEST_LOCATOR = createItem("nest_locator", () -> new NestLocator(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> SUGARBAG_HONEYCOMB = createItem("sugarbag_honeycomb", () -> new Item(new Item.Properties().food(SUGARBAG).rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, ? extends Item> TREAT_ON_A_STICK = createItem("treat_on_a_stick", () -> new TreatOnAStick(new Item.Properties().durability(25),7));
    public static final DeferredHolder<Item, ? extends Item> WAX = createItem("wax", () -> new HoneycombItem(new Item.Properties()));

    public static final DeferredHolder<Item, ? extends Item> BEE_NEST_DIAMOND_HELMET = createItem("bee_nest_diamond_helmet", () -> new BeeNestHelmet(ArmorMaterials.DIAMOND, new Item.Properties()));

    public static final DeferredHolder<Item, ? extends Item> CONFIGURABLE_HONEYCOMB = createItem("configurable_honeycomb", () -> new Honeycomb(new Item.Properties(), "#d2ab00"));

    public static final DeferredHolder<Item, ? extends Item> HONEYCOMB_GHOSTLY = createItem("honeycomb_ghostly", () -> new HoneycombItem(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> HONEYCOMB_MILKY = createItem("honeycomb_milky", () -> new HoneycombItem(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> HONEYCOMB_POWDERY = createItem("honeycomb_powdery", () -> new HoneycombItem(new Item.Properties()));

    public static final DeferredHolder<Item, ? extends Item> DRACONIC_DUST = createItem("draconic_dust", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> DRACONIC_CHUNK = createItem("draconic_chunk", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> WITHER_SKULL_CHIP = createItem("wither_skull_chip", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> OBSIDIAN_SHARD = createItem("obsidian_shard", () -> new Item(new Item.Properties()));

    // Hive upgrades
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_BASE = createItem("upgrade_base", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY = createItem("upgrade_productivity", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY_2 = createItem("upgrade_productivity_2", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY_3 = createItem("upgrade_productivity_3", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY_4 = createItem("upgrade_productivity_4", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_TIME = createItem("upgrade_time", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_BREEDING = createItem("upgrade_breeding", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_NOT_BABEE = createItem("upgrade_not_babee", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_COMB_BLOCK = createItem("upgrade_comb_block", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_ANTI_TELEPORT = createItem("upgrade_anti_teleport", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_RANGE = createItem("upgrade_range", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_FILTER = createItem("upgrade_filter", () -> new FilterUpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_BEE_SAMPLER = createItem("upgrade_bee_sampler", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_SIMULATOR = createItem("upgrade_simulator", () -> new UpgradeItem((new Item.Properties())));

    public static final DeferredHolder<Item, ? extends Item> ADV_BREED_BEE = createItem("adv_breed_bee", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> ADV_BREED_ALL_BEES = createItem("adv_breed_all_bees", () -> new Item(new Item.Properties()));

    public static DeferredHolder<Item, ? extends Item> CONFIGURABLE_SPAWN_EGG;
    public static DeferredHolder<Item, ? extends Item> CONFIGURABLE_COMB_BLOCK;

    public static <I extends Item> DeferredHolder<Item, I> createItem(String name, Supplier<? extends I> supplier) {
        return ProductiveBees.ITEMS.register(name, supplier);
    }
}
