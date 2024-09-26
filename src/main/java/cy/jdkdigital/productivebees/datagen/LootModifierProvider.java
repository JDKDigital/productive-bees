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
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
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
        add("armadillo_scute_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/armadillo"), new ItemStack(Items.ARMADILLO_SCUTE), 0.15f));
        add("turtle_scute_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/turtle"), new ItemStack(Items.TURTLE_SCUTE), 0.15f));
        add("wither_nether_star_wannabee", new ItemLootModifier(lootTableConditions(true, "entities/wither"), new ItemStack(Items.NETHER_STAR), 0.15f));

        add("sniffer_sussy_egg", new IngredientModifier(lootTableConditions(false, "gameplay/sniffer_digging"), ComponentIngredient.of(BeeCreator.getSpawnEgg(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "sussy"))), 0.15f, true));
        add("sussy_amber_egg", new IngredientModifier(lootTableConditions(false, "archaeology/ocean_ruin_cold"), ComponentIngredient.of(BeeCreator.getSpawnEgg(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "amber"))), 0.08f, true));
        add("village_chest_sturdy_cage", new ItemLootModifier(anyOfConditions("chests/village/village_armorer", "chests/village/village_butcher", "chests/village/village_desert_house", "chests/village/village_fisher", "chests/village/village_plains_house", "chests/village/village_savanna_house", "chests/village/village_shepherd", "chests/village/village_snowy_house", "chests/village/village_taiga_house", "chests/village/village_temple", "chests/nether_bridge", "chests/desert_pyramid", "chests/abandoned_mineshaft"), new ItemStack(ModItems.STURDY_BEE_CAGE), 0.2f));

        add("frog_eat_bee", new IngredientModifier(frogConditions("entities/bee"), ComponentIngredient.of(BeeCreator.getSpawnEgg(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "ribbeet"))), 1.0f, true));

        add("undergarden_forgotten_egg", new IngredientModifier(lootTableConditions(false, "undergarden:chests/catacombs"), ComponentIngredient.of(BeeCreator.getSpawnEgg(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "forgotten"), true)), 0.25f, false));
        add("aquaculture_neptunium_egg", new ContainerContentsModifier(lootTableConditions(false, BuiltInLootTables.FISHING.location().toString()), new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("aquaculture", "neptunes_bounty"))), ComponentIngredient.of(BeeCreator.getSpawnEgg(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "neptunium"), true)), 0.25f, false));
    }

    private LootItemCondition[] lootTableConditions(boolean addUUIDCondition, String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(ResourceLocation.parse(s)).build());
        }
        if (addUUIDCondition) {
            list.add(new LootItemKilledByUUIDCondition(ModEntities.WANNA_BEE_UUID));
        }
        return list.toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] anyOfConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition.Builder>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(ResourceLocation.parse(s)));
        }
        return List.of(AnyOfCondition.anyOf(list.toArray(new LootItemCondition.Builder[0])).build()).toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] frogConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        list.add(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG))).build());
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(ResourceLocation.parse(s)).build());
        }
        return list.toArray(new LootItemCondition[0]);
    }
}
