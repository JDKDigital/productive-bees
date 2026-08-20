package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.ComponentIngredient;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivelib.loot.ContainerContentsModifier;
import cy.jdkdigital.productivelib.loot.IngredientModifier;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import cy.jdkdigital.productivelib.loot.LootItemKilledByUUIDCondition;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootModifierProvider extends GlobalLootModifierProvider
{
    public LootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ProductiveBees.MODID);
    }

    @Override
    protected void start() {
        add("armadillo_scute_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/armadillo"), 0, new ItemStackTemplate(Items.ARMADILLO_SCUTE), 0.15f));
        add("turtle_scute_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/turtle"), 0, new ItemStackTemplate(Items.TURTLE_SCUTE), 0.15f));
        add("wither_nether_star_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/wither"), 0, new ItemStackTemplate(Items.NETHER_STAR), 0.15f));
        add("ender_dragon_breath_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/ender_dragon"), 0, new ItemStackTemplate(Items.DRAGON_BREATH), 0.10f));

        add("sniffer_sussy_egg", new IngredientModifier(lootTableConditions(false, "gameplay/sniffer_digging"), 0, BeeCreator.getSpawnEggIngredient(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "sussy")), 0.15f, true));
        add("sussy_amber_egg", new IngredientModifier(lootTableConditions(false, "archaeology/ocean_ruin_cold"), 0, BeeCreator.getSpawnEggIngredient(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "amber")), 0.08f, true));
        add("village_chest_sturdy_cage", new ItemLootModifier(anyOfConditions("chests/village/village_armorer", "chests/village/village_butcher", "chests/village/village_desert_house", "chests/village/village_fisher", "chests/village/village_plains_house", "chests/village/village_savanna_house", "chests/village/village_shepherd", "chests/village/village_snowy_house", "chests/village/village_taiga_house", "chests/village/village_temple", "chests/nether_bridge", "chests/desert_pyramid", "chests/abandoned_mineshaft"), 0, new ItemStackTemplate(ModItems.STURDY_BEE_CAGE.get()), 0.2f));

        add("frog_eat_bee", new IngredientModifier(frogConditions("entities/bee"), 0, BeeCreator.getSpawnEggIngredient(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "ribbeet")), 1.0f, true));

        add("undergarden_forgotten_egg", new IngredientModifier(lootTableConditions(false, "undergarden:chests/catacombs"), 0, BeeCreator.getSpawnEggIngredient(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "forgotten"), true), 0.25f, false), new ModLoadedCondition("undergarden"));
        add("aquaculture_neptunium_egg", new ContainerContentsModifier(lootTableConditions(false, BuiltInLootTables.FISHING.identifier().toString()), 0, new ItemStackTemplate(BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("aquaculture", "neptunes_bounty")).map(Holder::value).orElse(Items.AIR)), BeeCreator.getSpawnEggIngredient(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "neptunium"), true), 0.25f, false), new ModLoadedCondition("aquaculture"));
    }

    private LootItemCondition[] lootTableConditions(boolean addUUIDCondition, String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)).build());
        }
        if (addUUIDCondition) {
            list.add(new LootItemKilledByUUIDCondition(ModEntities.WANNA_BEE_UUID));
        }
        return list.toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] anyOfConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition.Builder>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)));
        }
        return List.of(AnyOfCondition.anyOf(list.toArray(new LootItemCondition.Builder[0])).build()).toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] frogConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        list.add(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(registries.lookupOrThrow(Registries.ENTITY_TYPE), EntityType.FROG))).build());
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)).build());
        }
        return list.toArray(new LootItemCondition[0]);
    }
}
