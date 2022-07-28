package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.*;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.*;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> HIVE_BEES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> SOLITARY_BEES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ProductiveBees.MODID);

    public static RegistryObject<EntityType<ThrowableItemProjectile>> BEE_BOMB = createEntity("bee_bomb", EntityType.Builder.<ThrowableItemProjectile>of(BeeBombEntity::new, MobCategory.MISC).sized(0.25F, 0.25F));

    public static RegistryObject<EntityType<Bee>> DYE_BEE = createHiveBee("dye_bee", ProductiveBee::new, 16768648, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> LUMBER_BEE = createHiveBee("lumber_bee", LumberBee::new, 8306542, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> QUARRY_BEE = createHiveBee("quarry_bee", QuarryBee::new, 7566195, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> RANCHER_BEE = createHiveBee("rancher_bee", RancherBee::new, 9615358, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> COLLECTOR_BEE = createHiveBee("collector_bee", HoarderBee::new, 8306149, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> HOARDER_BEE = createHiveBee("hoarder_bee", HoarderBee::new, 8306149, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> FARMER_BEE = createHiveBee("farmer_bee", FarmerBee::new, 9615358, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> CREEPER_BEE = createHiveBee("creeper_bee", CreeperBee::new, 894731, 6238757, ModItemGroups.PRODUCTIVE_BEES);
    public static RegistryObject<EntityType<Bee>> CUPID_BEE = createHiveBee("cupid_bee", CupidBee::new, 894731, 6238757, ModItemGroups.PRODUCTIVE_BEES);

    public static RegistryObject<EntityType<Bee>> ASHY_MINING_BEE = createSolitaryBee("ashy_mining_bee", MiningBee::new, 11709345, 6238757);
    public static RegistryObject<EntityType<Bee>> BLUE_BANDED_BEE = createSolitaryBee("blue_banded_bee", BlueBandedBee::new, 9615358, 6238757);
    public static RegistryObject<EntityType<Bee>> GREEN_CARPENTER_BEE = createSolitaryBee("green_carpenter_bee", CarpenterBee::new, 9615358, 6238757);
    public static RegistryObject<EntityType<Bee>> YELLOW_BLACK_CARPENTER_BEE = createSolitaryBee("yellow_black_carpenter_bee", CarpenterBee::new, 15582019, 6238757);
    public static RegistryObject<EntityType<Bee>> CHOCOLATE_MINING_BEE = createSolitaryBee("chocolate_mining_bee", MiningBee::new, 11709345, 6238757);
    public static RegistryObject<EntityType<Bee>> DIGGER_BEE = createSolitaryBee("digger_bee", DiggerBee::new, 8875079, 6238757);
    public static RegistryObject<EntityType<Bee>> LEAFCUTTER_BEE = createSolitaryBee("leafcutter_bee", SolitaryBee::new, 2057258, 6238757);
    public static RegistryObject<EntityType<Bee>> MASON_BEE = createSolitaryBee("mason_bee", MasonBee::new, 2226382, 6238757);
    public static RegistryObject<EntityType<Bee>> NEON_CUCKOO_BEE = createSolitaryBee("neon_cuckoo_bee", NeonCuckooBee::new, 9615358, 6238757);
    public static RegistryObject<EntityType<Bee>> NOMAD_BEE = createSolitaryBee("nomad_bee", NomadBee::new, 14529911, 6238757);
    public static RegistryObject<EntityType<Bee>> REED_BEE = createSolitaryBee("reed_bee", ReedBee::new, 13806336, 6238757);
    public static RegistryObject<EntityType<Bee>> RESIN_BEE = createSolitaryBee("resin_bee", ResinBee::new, 13939231, 6238757);
    public static RegistryObject<EntityType<Bee>> SWEAT_BEE = createSolitaryBee("sweat_bee", SweatBee::new, 9748939, 6238757);
    public static RegistryObject<EntityType<BumbleBee>> BUMBLE = createSolitaryBee("bumble_bee", BumbleBee::new, 9748939, 6238757);

    public static RegistryObject<EntityType<ConfigurableBee>> CONFIGURABLE_BEE = createHiveBee("configurable_bee", ConfigurableBee::new, 16768648, 6238757, ModItemGroups.PRODUCTIVE_BEES);

    public static <E extends Bee> RegistryObject<EntityType<E>> createHiveBee(String name, EntityType.EntityFactory<E> supplier, int primaryColor, int secondaryColor, CreativeModeTab itemGroup) {
        return createBee(HIVE_BEES, name, supplier, primaryColor, secondaryColor, itemGroup);
    }

    public static <E extends Bee> RegistryObject<EntityType<E>> createSolitaryBee(String name, EntityType.EntityFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(SOLITARY_BEES, name, supplier, primaryColor, secondaryColor, ModItemGroups.PRODUCTIVE_BEES);
    }

    public static <E extends Bee> RegistryObject<EntityType<E>> createBee(DeferredRegister<EntityType<?>> registry, String name, EntityType.EntityFactory<E> supplier, int primaryColor, int secondaryColor, CreativeModeTab itemGroup) {
        EntityType.Builder<E> builder = EntityType.Builder.of(supplier, MobCategory.CREATURE).sized(0.7F, 0.6F).setTrackingRange(8);

        RegistryObject<EntityType<E>> entity = registry.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

        if (itemGroup != null) {
            RegistryObject<Item> spawnEgg = ModItems.ITEMS.register("spawn_egg_" + name, () -> new SpawnEgg(entity::get, secondaryColor, primaryColor, new Item.Properties().tab(itemGroup)));
            if (name.equals("configurable_bee")) {
                ModItems.CONFIGURABLE_SPAWN_EGG = spawnEgg;
            }
            ModItems.SPAWN_EGGS.add(spawnEgg);
        }


        return entity;
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> createEntity(String name, EntityType.Builder<E> builder) {
        RegistryObject<EntityType<E>> entity = ENTITIES.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

        return entity;
    }
}
