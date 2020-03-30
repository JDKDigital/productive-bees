package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.SlimyBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.SolitaryBeeRenderer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.CreeperBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.EnderBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.solitary.*;
import cy.jdkdigital.productivebees.entity.bee.hive.WitherBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.ZombieBeeEntity;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
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

//    public static RegistryObject<EntityType<BeeEntity>> IRON_BEE = createBee("iron_bee", (EntityType<BeeEntity> entityType, World world) -> {
//        ProductiveBeeEntity bee = new ProductiveBeeEntity(entityType, world);
//        bee.setProductionList(ProductiveBeesConfig.BEES.itemProductionRules.get().get("productivebees:iron_bee"));
//        return bee;
//    });
    public static RegistryObject<EntityType<BeeEntity>> IRON_BEE = createHiveBee("iron_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> GOLD_BEE = createHiveBee("gold_bee", ProductiveBeeEntity::new);

    public static RegistryObject<EntityType<BeeEntity>> REDSTONE_BEE = createHiveBee("redstone_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> LAPIS_BEE = createHiveBee("lapis_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> EMERALD_BEE = createHiveBee("emerald_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> DIAMOND_BEE = createHiveBee("diamond_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> DYE_BEE = createHiveBee("dye_bee", ProductiveBeeEntity::new);

    public static RegistryObject<EntityType<BeeEntity>> CREEPER_BEE = createHiveBee("creeper_bee", CreeperBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> ZOMBIE_BEE = createHiveBee("zombie_bee", ZombieBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> ENDER_BEE = createHiveBee("ender_bee", EnderBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> SKELETAL_BEE = createHiveBee("skeletal_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> WITHER_BEE = createHiveBee("wither_bee", WitherBeeEntity::new);

    public static RegistryObject<EntityType<BeeEntity>> GLOWING_BEE = createHiveBee("glowing_bee", GlowingBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> QUARTZ_BEE = createHiveBee("quartz_bee", ProductiveBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> MAGMATIC_BEE = createHiveBee("magmatic_bee", MagmaticBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> DRACONIC_BEE = createHiveBee("draconic_bee", DraconicBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> MINING_BEE = createSolitaryBee("mining_bee", MiningBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> NOMAD_BEE = createSolitaryBee("nomad_bee", NomadBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> DIGGER_BEE = createSolitaryBee("digger_bee", DiggerBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> MASON_BEE = createSolitaryBee("mason_bee", MasonBeeEntity::new);
    public static RegistryObject<EntityType<BeeEntity>> SLIMY_BEE = createHiveBee("slimy_bee", SlimyBeeEntity::new);

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createHiveBee(String name, EntityType.IFactory<E> supplier) {
        EntityType.Builder<E> builder = EntityType.Builder.<E>create(supplier, EntityClassification.CREATURE).size(0.7F, 0.6F);
        if (name.equals("magmatic_bee")) {
            builder.immuneToFire();
        }
        return HIVE_BEES.register(name, () -> builder.build("productivebees:" + name));
    }
    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createSolitaryBee(String name, EntityType.IFactory<E> supplier) {
        EntityType.Builder<E> builder = EntityType.Builder.<E>create(supplier, EntityClassification.CREATURE).size(0.7F, 0.6F);
        if (name.equals("magmatic_bee")) {
            builder.immuneToFire();
        }
        return SOLITARY_BEES.register(name, () -> builder.build("productivebees:" + name));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRendering()
    {
        for(RegistryObject<EntityType<?>> registryObject : HIVE_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            ProductiveBees.LOGGER.info("REGNAME: " + bee.getTranslationKey());
            if (bee.getTranslationKey().contains("slimy_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, SlimyBeeRenderer::new);
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
