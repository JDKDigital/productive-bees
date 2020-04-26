package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.DyeBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.SlimyBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.SolitaryBeeRenderer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.*;
import cy.jdkdigital.productivebees.entity.bee.nesting.*;
import cy.jdkdigital.productivebees.entity.bee.solitary.*;
import cy.jdkdigital.productivebees.item.SpawnEgg;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> HIVE_BEES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> SOLITARY_BEES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ProductiveBees.MODID);

    public static RegistryObject<EntityType<BeeEntity>> IRON_BEE = createHiveBee("iron_bee", ProductiveBeeEntity::new, 6238757, 13487565);
    public static RegistryObject<EntityType<BeeEntity>> GOLD_BEE = createHiveBee("gold_bee", ProductiveBeeEntity::new, 6238757, 15582019);

    public static RegistryObject<EntityType<BeeEntity>> REDSTONE_BEE = createHiveBee("redstone_bee", ProductiveBeeEntity::new, 6238757, 13645345);
    public static RegistryObject<EntityType<BeeEntity>> LAPIS_BEE = createHiveBee("lapis_bee", ProductiveBeeEntity::new, 6238757, 4276966);
    public static RegistryObject<EntityType<BeeEntity>> EMERALD_BEE = createHiveBee("emerald_bee", ProductiveBeeEntity::new, 6238757, 3000655);
    public static RegistryObject<EntityType<BeeEntity>> DIAMOND_BEE = createHiveBee("diamond_bee", ProductiveBeeEntity::new, 6238757, 4055009);
    public static RegistryObject<EntityType<BeeEntity>> DYE_BEE = createHiveBee("dye_bee", ProductiveBeeEntity::new, 6238757, 15582019);

    public static RegistryObject<EntityType<BeeEntity>> CREEPER_BEE = createHiveBee("creeper_bee", CreeperBeeEntity::new, 6238757, 894731);
    public static RegistryObject<EntityType<BeeEntity>> ZOMBIE_BEE = createHiveBee("zombie_bee", ZombieBeeEntity::new, 6238757, 7969893);
    public static RegistryObject<EntityType<BeeEntity>> ENDER_BEE = createHiveBee("ender_bee", EnderBeeEntity::new, 6238757, 1447446);
    public static RegistryObject<EntityType<BeeEntity>> SKELETAL_BEE = createHiveBee("skeletal_bee", SkeletalBeeEntity::new, 6238757, 12698049);
    public static RegistryObject<EntityType<BeeEntity>> WITHER_BEE = createHiveBee("wither_bee", WitherBeeEntity::new, 6238757, 1315860);

    public static RegistryObject<EntityType<BeeEntity>> GLOWING_BEE = createHiveBee("glowing_bee", GlowingBeeEntity::new, 6238757, 16579584);
    public static RegistryObject<EntityType<BeeEntity>> QUARTZ_BEE = createHiveBee("quartz_bee", ProductiveBeeEntity::new, 6238757, 15657702);
    public static RegistryObject<EntityType<BeeEntity>> MAGMATIC_BEE = createHiveBee("magmatic_bee", MagmaticBeeEntity::new, 6238757, 3407872);
    public static RegistryObject<EntityType<BeeEntity>> BLAZING_BEE = createHiveBee("blazing_bee", BlazingBeeEntity::new, 6238757, 16775294); // 16167425
    public static RegistryObject<EntityType<BeeEntity>> DRACONIC_BEE = createHiveBee("draconic_bee", DraconicBeeEntity::new, 6238757, 1842204);
    public static RegistryObject<EntityType<BeeEntity>> SLIMY_BEE = createHiveBee("slimy_bee", SlimyBeeEntity::new, 6238757, 8306542);

    public static RegistryObject<EntityType<BeeEntity>> ASHY_MINING_BEE = createSolitaryBee("ashy_mining_bee", AshyMiningBeeEntity::new, 6238757, 11709345);
    public static RegistryObject<EntityType<BeeEntity>> BLUE_BANDED_BEE = createSolitaryBee("blue_banded_bee", BlueBandedBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> GREEN_CARPENTER_BEE = createSolitaryBee("green_carpenter_bee", CarpenterBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> YELLOW_CARPENTER_BEE = createSolitaryBee("yellow_carpenter_bee", CarpenterBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> CHOCOLATE_MINING_BEE = createSolitaryBee("chocolate_mining_bee", ChocolateMiningBeeEntity::new, 6238757, 11709345);
    public static RegistryObject<EntityType<BeeEntity>> DIGGER_BEE = createSolitaryBee("digger_bee", DiggerBeeEntity::new, 6238757, 8875079);
    public static RegistryObject<EntityType<BeeEntity>> LEAFCUTTER_BEE = createSolitaryBee("leafcutter_bee", LeafcutterBeeEntity::new, 6238757, 2057258);
    public static RegistryObject<EntityType<BeeEntity>> MASON_BEE = createSolitaryBee("mason_bee", MasonBeeEntity::new, 6238757, 12226382);
    public static RegistryObject<EntityType<BeeEntity>> NEON_CUCKOO_BEE = createSolitaryBee("neon_cuckoo_bee", NeonCuckooBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> NOMAD_BEE = createSolitaryBee("nomad_bee", NomadBeeEntity::new, 6238757, 14529911);
    public static RegistryObject<EntityType<BeeEntity>> REED_BEE = createSolitaryBee("reed_bee", ReedBeeEntity::new, 6238757, 11709345);
    public static RegistryObject<EntityType<BeeEntity>> RESIN_BEE = createSolitaryBee("resin_bee", ResinBeeEntity::new, 6238757, 11709345);
    public static RegistryObject<EntityType<BeeEntity>> SWEATY_BEE = createSolitaryBee("sweaty_bee", SweatBeeEntity::new, 6238757, 11709345);

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createHiveBee(String name, EntityType.IFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(HIVE_BEES, name, supplier, primaryColor, secondaryColor);
    }
    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createSolitaryBee(String name, EntityType.IFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(SOLITARY_BEES, name, supplier, primaryColor, secondaryColor);
    }

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createBee(DeferredRegister<EntityType<?>> registry, String name, EntityType.IFactory<E> supplier, int primaryColor, int secondaryColor) {
        EntityType.Builder<E> builder = EntityType.Builder.<E>create(supplier, EntityClassification.CREATURE).size(0.7F, 0.6F);
        if (name.equals("magmatic_bee")) {
            builder.immuneToFire();
        }

        RegistryObject<EntityType<E>> entity = registry.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

        RegistryObject<Item> spawnEgg = ModItems.ITEMS.register("spawn_egg_" + name, () -> new SpawnEgg(entity::get, primaryColor, secondaryColor, new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
        ModItems.SPAWN_EGGS.add(spawnEgg);

        return entity;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRendering()
    {
        for(RegistryObject<EntityType<?>> registryObject : HIVE_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            if (bee.getTranslationKey().contains("slimy_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, SlimyBeeRenderer::new);
            } else if (bee.getTranslationKey().contains("dye_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, DyeBeeRenderer::new);
            } else {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, ProductiveBeeRenderer::new);
            }
        }
        for(RegistryObject<EntityType<?>> registryObject : SOLITARY_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends SolitaryBeeEntity>) bee, SolitaryBeeRenderer::new);
        }
    }
}
