package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.GeckoBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.*;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.*;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ModEntities
{
    public static final UUID WANNA_BEE_UUID = UUID.nameUUIDFromBytes("pb_wanna_bee".getBytes(StandardCharsets.UTF_8));

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> HIVE_BEES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> SOLITARY_BEES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ProductiveBees.MODID);

    public static DeferredHolder<EntityType<?>, EntityType<ThrowableItemProjectile>> BEE_BOMB = createEntity("bee_bomb", EntityType.Builder.<ThrowableItemProjectile>of(BeeBombEntity::new, MobCategory.MISC).sized(0.25F, 0.25F));

    public static DeferredHolder<EntityType<?>, EntityType<Bee>> DYE_BEE = createHiveBee("dye_bee", ProductiveBee::new, 16768648, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> LUMBER_BEE = createHiveBee("lumber_bee", LumberBee::new, 8306542, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> QUARRY_BEE = createHiveBee("quarry_bee", QuarryBee::new, 7566195, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> RANCHER_BEE = createHiveBee("rancher_bee", RancherBee::new, 9615358, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> COLLECTOR_BEE = createHiveBee("collector_bee", HoarderBee::new, 8306149, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> HOARDER_BEE = createHiveBee("hoarder_bee", HoarderBee::new, 8306149, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> FARMER_BEE = createHiveBee("farmer_bee", FarmerBee::new, 9615358, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> CREEPER_BEE = createHiveBee("creeper_bee", CreeperBee::new, 894731, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> CUPID_BEE = createHiveBee("cupid_bee", CupidBee::new, 894731, 6238757);

    public static DeferredHolder<EntityType<?>, EntityType<Bee>> ASHY_MINING_BEE = createSolitaryBee("ashy_mining_bee", MiningBee::new, 11709345, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> BLUE_BANDED_BEE = createSolitaryBee("blue_banded_bee", BlueBandedBee::new, 9615358, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> GREEN_CARPENTER_BEE = createSolitaryBee("green_carpenter_bee", CarpenterBee::new, 9615358, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> YELLOW_BLACK_CARPENTER_BEE = createSolitaryBee("yellow_black_carpenter_bee", CarpenterBee::new, 15582019, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> CHOCOLATE_MINING_BEE = createSolitaryBee("chocolate_mining_bee", MiningBee::new, 11709345, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> DIGGER_BEE = createSolitaryBee("digger_bee", DiggerBee::new, 8875079, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> LEAFCUTTER_BEE = createSolitaryBee("leafcutter_bee", SolitaryBee::new, 2057258, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> MASON_BEE = createSolitaryBee("mason_bee", MasonBee::new, 2226382, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> NEON_CUCKOO_BEE = createSolitaryBee("neon_cuckoo_bee", NeonCuckooBee::new, 9615358, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> NOMAD_BEE = createSolitaryBee("nomad_bee", NomadBee::new, 14529911, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> REED_BEE = createSolitaryBee("reed_bee", ReedBee::new, 13806336, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> RESIN_BEE = createSolitaryBee("resin_bee", ResinBee::new, 13939231, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> SWEAT_BEE = createSolitaryBee("sweat_bee", SweatBee::new, 9748939, 6238757);
    public static DeferredHolder<EntityType<?>, EntityType<Bee>> BUMBLE = createSolitaryBee("bumble_bee", BumbleBee::new, 9748939, 6238757);

    public static DeferredHolder<EntityType<?>, EntityType<ConfigurableBee>> CONFIGURABLE_BEE = createHiveBee("configurable_bee", ModList.get().isLoaded("geckolib") ? GeckoBee::new : ConfigurableBee::new, 16768648, 6238757);

    public static <E extends Bee> DeferredHolder<EntityType<?>, EntityType<E>> createHiveBee(String name, EntityType.EntityFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(HIVE_BEES, name, supplier, primaryColor, secondaryColor);
    }

    public static <E extends Bee> DeferredHolder<EntityType<?>, EntityType<E>> createSolitaryBee(String name, EntityType.EntityFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(SOLITARY_BEES, name, supplier, primaryColor, secondaryColor);
    }

    public static <E extends Bee> DeferredHolder<EntityType<?>, EntityType<E>> createBee(DeferredRegister<EntityType<?>> registry, String name, EntityType.EntityFactory<E> supplier, int primaryColor, int secondaryColor) {
        EntityType.Builder<E> builder = EntityType.Builder.of(supplier, MobCategory.CREATURE).sized(0.7F, 0.6F).setTrackingRange(8);

        DeferredHolder<EntityType<?>, EntityType<E>> entity = registry.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

        DeferredHolder<Item, ? extends Item> spawnEgg = ProductiveBees.ITEMS.register("spawn_egg_" + name, () -> new SpawnEgg(entity::get, FastColor.ARGB32.color(255, primaryColor), FastColor.ARGB32.color(255, secondaryColor), new Item.Properties()));
        if (name.equals("configurable_bee")) {
            ModItems.CONFIGURABLE_SPAWN_EGG = spawnEgg;
        }
        ModItems.SPAWN_EGGS.add(spawnEgg);

        return entity;
    }

    public static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> createEntity(String name, EntityType.Builder<E> builder) {
        DeferredHolder<EntityType<?>, EntityType<E>> entity = ENTITIES.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

        return entity;
    }
}
