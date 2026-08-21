package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.advancements.criterion.CalmBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.CatchBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.FishBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.SaddleBeeTrigger;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/** Datagen for productivebees advancements. Breed/catch criteria use full BeeProvider paths so they survive subfolder renames. */
public class PBAdvancementProvider extends AdvancementProvider
{
    public PBAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new Generator()));
    }

    private static class Generator implements AdvancementSubProvider
    {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
            HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);
            HolderGetter<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
            HolderGetter<EntityType<?>> entities = registries.lookupOrThrow(Registries.ENTITY_TYPE);
            TagKey<Item> solitaryOverworldNestsItem = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "solitary_overworld_nests"));
            TagKey<Block> solitaryNestsBlock = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "solitary_nests"));

            // --- husbandry/advanced_beehive tree ---
            AdvancementHolder advancedBeehive = adv()
                    .parent(Identifier.parse("minecraft:husbandry/safely_harvest_honey"))
                    .display(ModBlocks.HIVES.get("advanced_oak_beehive").get().asItem(),
                            t("advanced_beehive"), d("advanced_beehive"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_hive", hasTagItems(items, ModTags.HIVES))
                    .save(consumer, "productivebees:husbandry/advanced_beehive");

            AdvancementHolder expansionBox = adv()
                    .parent(advancedBeehive)
                    .display(ModBlocks.EXPANSIONS.get("expansion_box_oak").get().asItem(),
                            t("expansion_box"), d("expansion_box"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_box", hasTagItems(items, ModTags.BOXES))
                    .save(consumer, "productivebees:husbandry/advanced_beehive/expansion_box");

            adv()
                    .parent(expansionBox)
                    .display(productivelibItem("upgrade_base"),
                            t("hive_upgrades"), d("hive_upgrades"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(productivelibItem("upgrade_base")))
                    .save(consumer, "productivebees:husbandry/advanced_beehive/expansion_box/hive_upgrades");

            AdvancementHolder dragonEggHive = adv()
                    .parent(advancedBeehive)
                    .display(ModBlocks.DRAGON_EGG_HIVE.get().asItem(),
                            t("dragon_egg_hive"), d("dragon_egg_hive"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(ModBlocks.DRAGON_EGG_HIVE.get().asItem()))
                    .save(consumer, "productivebees:husbandry/advanced_beehive/dragon_egg_hive");

            adv()
                    .parent(dragonEggHive)
                    .display(ModBlocks.INACTIVE_DRAGON_EGG.get().asItem(),
                            t("convert_egg"), d("convert_egg"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("dragon_breath", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                            LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, ModBlocks.INACTIVE_DRAGON_EGG.get())),
                            ItemPredicate.Builder.item().of(items, Items.DRAGON_BREATH)))
                    .save(consumer, "productivebees:husbandry/advanced_beehive/dragon_egg_hive/convert_egg");

            // --- husbandry/bee_cage tree ---
            AdvancementHolder beeCage = adv()
                    .parent(Identifier.parse("minecraft:husbandry/safely_harvest_honey"))
                    .display(ModItems.BEE_CAGE.get(),
                            t("bee_cage"), d("bee_cage"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(ModItems.BEE_CAGE.get()))
                    .save(consumer, "productivebees:husbandry/bee_cage");

            adv()
                    .parent(beeCage)
                    .display(ModItems.HONEY_TREAT.get(),
                            t("calm_bee"), d("calm_bee"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("calm_bee", crit(ModAdvancements.CALM_BEE.get(), CalmBeeTrigger.TriggerInstance.any()))
                    .save(consumer, "productivebees:husbandry/bee_cage/calm_bee");

            adv()
                    .parent(beeCage)
                    .display(ModItems.BEE_CAGE.get(),
                            t("catch_any_bee"), d("catch_any_bee"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("catch", crit(ModAdvancements.CATCH_BEE.get(), CatchBeeTrigger.TriggerInstance.any()))
                    .save(consumer, "productivebees:husbandry/bee_cage/catch_any_bee");

            adv()
                    .parent(beeCage)
                    .display(ModItems.NEST_LOCATOR.get(),
                            t("nest_locator"), d("nest_locator"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(ModItems.NEST_LOCATOR.get()))
                    .save(consumer, "productivebees:husbandry/bee_cage/nest_locator");

            AdvancementHolder overworldNest = adv()
                    .parent(beeCage)
                    .display(ModBlocks.OAK_WOOD_NEST.get().asItem(),
                            t("overworld_nest"), d("overworld_nest"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasTagItems(items, solitaryOverworldNestsItem))
                    .save(consumer, "productivebees:husbandry/bee_cage/overworld_nest");

            adv()
                    .parent(overworldNest)
                    .display(ModItems.SUGARBAG_HONEYCOMB.get(),
                            t("consume_sugarbag_comb"), d("consume_sugarbag_comb"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("consume", ConsumeItemTrigger.TriggerInstance.usedItem(items, ModItems.SUGARBAG_HONEYCOMB.get()))
                    .save(consumer, "productivebees:husbandry/bee_cage/overworld_nest/consume_sugarbag_comb");

            AdvancementHolder treatOnNest = adv()
                    .parent(overworldNest)
                    .display(ModItems.HONEY_TREAT.get(),
                            t("treat_on_nest"), d("treat_on_nest"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("use_treat", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                            LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, solitaryNestsBlock)),
                            ItemPredicate.Builder.item().of(items, ModItems.HONEY_TREAT.get())))
                    .save(consumer, "productivebees:husbandry/bee_cage/overworld_nest/treat_on_nest");

            AdvancementHolder bumblebeeRider = adv()
                    .parent(treatOnNest)
                    .display(Items.SADDLE,
                            t("bumblebee_rider"), d("bumblebee_rider"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("saddle_bee", crit(ModAdvancements.SADDLE_BEE.get(), SaddleBeeTrigger.TriggerInstance.any()))
                    .save(consumer, "productivebees:husbandry/bee_cage/overworld_nest/treat_on_nest/bumblebee_rider");

            adv()
                    .parent(bumblebeeRider)
                    .display(ModItems.TREAT_ON_A_STICK.get(),
                            t("treat_on_stick"), d("treat_on_stick"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(ModItems.TREAT_ON_A_STICK.get()))
                    .save(consumer, "productivebees:husbandry/bee_cage/overworld_nest/treat_on_nest/bumblebee_rider/treat_on_stick");

            AdvancementHolder professionalBee = adv()
                    .parent(beeCage)
                    .display(ModItems.HONEYCOMB_MILKY.get(),
                            t("professional_bee"), d("professional_bee"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("lumber_bee", bredEntityBeeWith(entities, ModEntities.LUMBER_BEE.get()))
                    .addCriterion("rancher_bee", bredEntityBeeWith(entities, ModEntities.RANCHER_BEE.get()))
                    .addCriterion("quarry_bee", bredEntityBeeWith(entities, ModEntities.QUARRY_BEE.get()))
                    .addCriterion("farmer_bee", bredEntityBeeWith(entities, ModEntities.FARMER_BEE.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, "productivebees:husbandry/bee_cage/professional_bee");

            AdvancementHolder feedingSlab = adv()
                    .parent(professionalBee)
                    .display(ModBlocks.FEEDER.get().asItem(),
                            t("feeding_slab"), d("feeding_slab"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(ModBlocks.FEEDER.get().asItem()))
                    .save(consumer, "productivebees:husbandry/bee_cage/professional_bee/feeding_slab");

            adv()
                    .parent(feedingSlab)
                    .display(ModItems.HONEY_BUCKET.get(),
                            t("honeylogged"), d("honeylogged"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("honeylog", ItemUsedOnLocationTrigger.TriggerInstance.placedBlockWithProperties(
                            ModBlocks.FEEDER.get(), Feeder.HONEYLOGGED, true))
                    .save(consumer, "productivebees:husbandry/bee_cage/professional_bee/feeding_slab/honeylogged");

            AdvancementHolder quartzNest = adv()
                    .parent(beeCage)
                    .display(ModBlocks.NETHER_QUARTZ_NEST.get().asItem(),
                            t("quartz_nest"), d("quartz_nest"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", hasItems(ModBlocks.NETHER_QUARTZ_NEST.get().asItem()))
                    .save(consumer, "productivebees:husbandry/bee_cage/quartz_nest");

            AdvancementHolder catchCrystalline = adv()
                    .parent(quartzNest)
                    .display(ModItems.CONFIGURABLE_HONEYCOMB.get(),
                            t("catch_crystalline_bee"), d("catch_crystalline_bee"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("catch", crit(ModAdvancements.CATCH_BEE.get(), CatchBeeTrigger.TriggerInstance.create("productivebees:gems/crystalline")))
                    .save(consumer, "productivebees:husbandry/bee_cage/quartz_nest/catch_crystalline_bee");

            AdvancementHolder breedIron = adv()
                    .parent(catchCrystalline)
                    .display(ModItems.ADV_BREED_BEE.get(),
                            t("breed_iron_bee"), d("breed_iron_bee"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("iron_bee", bredConfigurableBeeWith(entities, "productivebees:raw_materials/iron"))
                    .save(consumer, "productivebees:husbandry/bee_cage/quartz_nest/catch_crystalline_bee/breed_iron_bee");

            String[] variants = {"raw_materials/iron", "raw_materials/gold", "coal", "dusts/redstone", "gems/lapis",
                    "gems/diamond", "gems/emerald", "dusts/blazing", "obsidian", "frosty", "experience"};
            EntityType<?>[] entityBees = {ModEntities.DYE_BEE.get(), ModEntities.LUMBER_BEE.get(),
                    ModEntities.RANCHER_BEE.get(), ModEntities.QUARRY_BEE.get(), ModEntities.FARMER_BEE.get()};
            Advancement.Builder breedAll = adv()
                    .parent(breedIron)
                    .display(ModItems.ADV_BREED_ALL_BEES.get(),
                            t("breed_all_productive_bees"), d("breed_all_productive_bees"), null,
                            AdvancementType.CHALLENGE, true, true, false);
            for (String variant : variants) {
                String simple = variant.contains("/") ? variant.substring(variant.lastIndexOf('/') + 1) : variant;
                breedAll.addCriterion(simple + "_bee", bredConfigurableBeeWith(entities, "productivebees:" + variant));
            }
            for (EntityType<?> et : entityBees) {
                breedAll.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(et).getPath(), bredEntityBeeWith(entities, et));
            }
            breedAll.save(consumer, "productivebees:husbandry/bee_cage/quartz_nest/catch_crystalline_bee/breed_iron_bee/breed_all_productive_bees");

            // --- fishy_bees ---
            adv()
                    .parent(Identifier.parse("minecraft:husbandry/fishy_business"))
                    .display(ModItems.ADV_BREED_BEE.get(),
                            t("fishy_bees"), d("fishy_bees"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("catch", crit(ModAdvancements.FISH_BEE.get(), FishBeeTrigger.TriggerInstance.any()))
                    .save(consumer, "productivebees:husbandry/fishy_bees");
        }
    }

    // ---------- helpers ----------

    private static Advancement.Builder adv() { return Advancement.Builder.advancement(); }
    private static Component t(String key) { return Component.translatable("advancements.husbandry." + key + ".title"); }
    private static Component d(String key) { return Component.translatable("advancements.husbandry." + key + ".description"); }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(Item item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }
    private static Criterion<InventoryChangeTrigger.TriggerInstance> hasTagItems(HolderGetter<Item> items, TagKey<Item> tag) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, tag));
    }

    private static Criterion<BredAnimalsTrigger.TriggerInstance> bredConfigurableBeeWith(HolderGetter<EntityType<?>> entities, String fullBeeId) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", fullBeeId);
        return BredAnimalsTrigger.TriggerInstance.bredAnimals(
                EntityPredicate.Builder.entity()
                        .of(entities, ModEntities.CONFIGURABLE_BEE.get())
                        .nbt(new NbtPredicate(tag)));
    }
    private static Criterion<BredAnimalsTrigger.TriggerInstance> bredEntityBeeWith(HolderGetter<EntityType<?>> entities, EntityType<?> type) {
        return BredAnimalsTrigger.TriggerInstance.bredAnimals(
                EntityPredicate.Builder.entity().of(entities, type));
    }

    private static Item productivelibItem(String path) {
        return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("productivelib", path));
    }

    private static <T extends CriterionTriggerInstance> Criterion<T> crit(CriterionTrigger<T> trigger, T instance) {
        return trigger.createCriterion(instance);
    }

}
