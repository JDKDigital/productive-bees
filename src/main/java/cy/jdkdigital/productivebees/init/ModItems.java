package cy.jdkdigital.productivebees.init;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModItems
{
    public static final FoodProperties SUGARBAG = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2F).alwaysEdible().build();

    public static final Consumable SUGARBAG_CONSUMABLE = Consumable.builder()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1), 0.2f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1), 0.1f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1200, 1), 0.3f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HASTE, 1200, 1), 0.1f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1), 0.1f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.STRENGTH, 1200, 1), 0.1f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 1200, 1), 0.1f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.RESISTANCE, 1200, 1), 0.2f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.LUCK, 1200, 1), 0.1f))
            .build();


    public static final List<DeferredHolder<Item, ? extends Item>> SPAWN_EGGS = Lists.newArrayList();

    public static final DeferredHolder<Item, ? extends Item> HONEY_BUCKET = createItem("honey_bucket", p -> new BucketItem(ModFluids.HONEY.get(), p), () -> new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    public static final DeferredHolder<Item, ? extends Item> BEE_CAGE = createItem("bee_cage", BeeCage::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> STURDY_BEE_CAGE = createItem("sturdy_bee_cage", SturdyBeeCage::new, () -> new Item.Properties().stacksTo(16));
    public static final DeferredHolder<Item, ? extends Item> HONEY_TREAT = createItem("honey_treat", HoneyTreat::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> GENE_BOTTLE = createItem("gene_bottle", GeneBottle::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> GENE = createItem("gene", Gene::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> MILK_BOTTLE = createItem("milk_bottle", Item::new, () -> new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16));
    public static final DeferredHolder<Item, ? extends Item> BEE_BOMB = createItem("bee_bomb", p -> new BeeBomb(p, false), () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> BEE_BOMB_ANGRY = createItem("bee_bomb_angry", p -> new BeeBomb(p, true), () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> NEST_LOCATOR = createItem("nest_locator", NestLocator::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> SUGARBAG_HONEYCOMB = createItem("sugarbag_honeycomb", Item::new, () -> new Item.Properties().food(SUGARBAG, SUGARBAG_CONSUMABLE).rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, ? extends Item> TREAT_ON_A_STICK = createItem("treat_on_a_stick", p -> new TreatOnAStick(p, 7), () -> new Item.Properties().durability(25));
    public static final DeferredHolder<Item, ? extends Item> WAX = createItem("wax", HoneycombItem::new, () -> new Item.Properties());

    public static final ResourceKey<EquipmentAsset> BEE_NEST_DIAMOND_EQUIPMENT = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_nest_diamond"));
    public static final DeferredHolder<Item, ? extends Item> BEE_NEST_DIAMOND_HELMET = createItem("bee_nest_diamond_helmet", BeeNestHelmet::new, () -> new Item.Properties()
            .humanoidArmor(ArmorMaterials.DIAMOND, ArmorType.HELMET)
            .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                    .setEquipSound(ArmorMaterials.DIAMOND.equipSound())
                    .setAsset(BEE_NEST_DIAMOND_EQUIPMENT)
                    .build()));

    public static final DeferredHolder<Item, ? extends Item> CONFIGURABLE_HONEYCOMB = createItem("configurable_honeycomb", p -> new Honeycomb(p, "#d2ab00"), () -> new Item.Properties());

    public static final DeferredHolder<Item, ? extends Item> HONEYCOMB_GHOSTLY = createItem("honeycomb_ghostly", HoneycombItem::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> HONEYCOMB_MILKY = createItem("honeycomb_milky", HoneycombItem::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> HONEYCOMB_POWDERY = createItem("honeycomb_powdery", HoneycombItem::new, () -> new Item.Properties());

    public static final DeferredHolder<Item, ? extends Item> DRACONIC_DUST = createItem("draconic_dust", Item::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> DRACONIC_CHUNK = createItem("draconic_chunk", Item::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> WITHER_SKULL_CHIP = createItem("wither_skull_chip", Item::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> OBSIDIAN_SHARD = createItem("obsidian_shard", Item::new, () -> new Item.Properties());

    public static final DeferredHolder<Item, ? extends Item> ADV_BREED_BEE = createItem("adv_breed_bee", Item::new, () -> new Item.Properties());
    public static final DeferredHolder<Item, ? extends Item> ADV_BREED_ALL_BEES = createItem("adv_breed_all_bees", Item::new, () -> new Item.Properties());

    public static DeferredHolder<Item, ? extends Item> CONFIGURABLE_SPAWN_EGG;
    public static DeferredHolder<Item, ? extends Item> CONFIGURABLE_COMB_BLOCK;

    @SuppressWarnings("unchecked")
    public static <I extends Item> DeferredHolder<Item, I> createItem(String name, Function<Item.Properties, ? extends I> factory, Supplier<Item.Properties> properties) {
        return (DeferredHolder<Item, I>) ProductiveBees.ITEMS.registerItem(name, factory::apply, properties);
    }
}
