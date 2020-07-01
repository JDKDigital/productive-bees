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
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> HIVE_BEES = DeferredRegister.create(ForgeRegistries.ENTITIES, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> SOLITARY_BEES = DeferredRegister.create(ForgeRegistries.ENTITIES, ProductiveBees.MODID);

    public static RegistryObject<EntityType<BeeEntity>> IRON_BEE = createHiveBee("iron_bee", ProductiveBeeEntity::new, 6238757, 13487565);
    public static RegistryObject<EntityType<BeeEntity>> GOLD_BEE = createHiveBee("gold_bee", ProductiveBeeEntity::new, 6238757, 15582019);

    public static RegistryObject<EntityType<BeeEntity>> COAL_BEE = createHiveBee("coal_bee", ProductiveBeeEntity::new, 2237733, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> REDSTONE_BEE = createHiveBee("redstone_bee", ProductiveBeeEntity::new, 6238757, 13645345);
    public static RegistryObject<EntityType<BeeEntity>> LAPIS_BEE = createHiveBee("lapis_bee", ProductiveBeeEntity::new, 6238757, 4276966);
    public static RegistryObject<EntityType<BeeEntity>> EMERALD_BEE = createHiveBee("emerald_bee", ProductiveBeeEntity::new, 6238757, 3000655);
    public static RegistryObject<EntityType<BeeEntity>> DIAMOND_BEE = createHiveBee("diamond_bee", ProductiveBeeEntity::new, 6238757, 4055009);
    public static RegistryObject<EntityType<BeeEntity>> DYE_BEE = createHiveBee("dye_bee", ProductiveBeeEntity::new, 6238757, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> LUMBER_BEE = createHiveBee("lumber_bee", LumberBeeEntity::new, 9615358, 8306542);

    public static RegistryObject<EntityType<BeeEntity>> CREEPER_BEE = createHiveBee("creeper_bee", CreeperBeeEntity::new, 6238757, 894731);
    public static RegistryObject<EntityType<BeeEntity>> ZOMBIE_BEE = createHiveBee("zombie_bee", ZombieBeeEntity::new, 6238757, 7969893);
    public static RegistryObject<EntityType<BeeEntity>> ENDER_BEE = createHiveBee("ender_bee", EnderBeeEntity::new, 6238757, 1447446);
    public static RegistryObject<EntityType<BeeEntity>> SKELETAL_BEE = createHiveBee("skeletal_bee", SkeletalBeeEntity::new, 6238757, 12698049);
    public static RegistryObject<EntityType<BeeEntity>> WITHER_BEE = createHiveBee("wither_bee", WitherBeeEntity::new, 6238757, 1315860);

    public static RegistryObject<EntityType<BeeEntity>> GLOWING_BEE = createHiveBee("glowing_bee", GlowingBeeEntity::new, 6238757, 16579584);
    public static RegistryObject<EntityType<BeeEntity>> QUARTZ_BEE = createHiveBee("quartz_bee", QuartzBeeEntity::new, 6238757, 15657702);
    public static RegistryObject<EntityType<BeeEntity>> MAGMATIC_BEE = createHiveBee("magmatic_bee", MagmaticBeeEntity::new, 6238757, 3407872);
    public static RegistryObject<EntityType<BeeEntity>> BLAZING_BEE = createHiveBee("blazing_bee", BlazingBeeEntity::new, 6238757, 16763648);
    public static RegistryObject<EntityType<BeeEntity>> DRACONIC_BEE = createHiveBee("draconic_bee", DraconicBeeEntity::new, 6238757, 1842204);
    public static RegistryObject<EntityType<BeeEntity>> SLIMY_BEE = createHiveBee("slimy_bee", SlimyBeeEntity::new, 6238757, 8306542);

    public static RegistryObject<EntityType<BeeEntity>> ASHY_MINING_BEE = createSolitaryBee("ashy_mining_bee", SolitaryBeeEntity::new, 6238757, 11709345);
    public static RegistryObject<EntityType<BeeEntity>> BLUE_BANDED_BEE = createSolitaryBee("blue_banded_bee", BlueBandedBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> GREEN_CARPENTER_BEE = createSolitaryBee("green_carpenter_bee", GreenCarpenterBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> YELLOW_BLACK_CARPENTER_BEE = createSolitaryBee("yellow_black_carpenter_bee", YellowBlackCarpenterBeeEntity::new, 6238757, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> CHOCOLATE_MINING_BEE = createSolitaryBee("chocolate_mining_bee", SolitaryBeeEntity::new, 6238757, 11709345);
    public static RegistryObject<EntityType<BeeEntity>> DIGGER_BEE = createSolitaryBee("digger_bee", SolitaryBeeEntity::new, 6238757, 8875079);
    public static RegistryObject<EntityType<BeeEntity>> LEAFCUTTER_BEE = createSolitaryBee("leafcutter_bee", SolitaryBeeEntity::new, 6238757, 2057258);
    public static RegistryObject<EntityType<BeeEntity>> MASON_BEE = createSolitaryBee("mason_bee", SolitaryBeeEntity::new, 6238757, 12226382);
    public static RegistryObject<EntityType<BeeEntity>> NEON_CUCKOO_BEE = createSolitaryBee("neon_cuckoo_bee", SolitaryBeeEntity::new, 6238757, 9615358);
    public static RegistryObject<EntityType<BeeEntity>> NOMAD_BEE = createSolitaryBee("nomad_bee", NomadBeeEntity::new, 6238757, 14529911);
    public static RegistryObject<EntityType<BeeEntity>> REED_BEE = createSolitaryBee("reed_bee", ReedBeeEntity::new, 6238757, 13806336);
    public static RegistryObject<EntityType<BeeEntity>> RESIN_BEE = createSolitaryBee("resin_bee", ResinBeeEntity::new, 6238757, 13939231);
    public static RegistryObject<EntityType<BeeEntity>> SWEATY_BEE = createSolitaryBee("sweaty_bee", SolitaryBeeEntity::new, 6238757, 9748939);

    public static RegistryObject<EntityType<BeeEntity>> ALUMINIUM_BEE = createHiveBee("aluminium_bee", ProductiveBeeEntity::new, 9748939, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> AMBER_BEE = createHiveBee("amber_bee", ProductiveBeeEntity::new, 13806336, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> BRASS_BEE = createHiveBee("brass_bee", ProductiveBeeEntity::new, 11309338, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> BRONZE_BEE = createHiveBee("bronze_bee", ProductiveBeeEntity::new, 13939231, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> COPPER_BEE = createHiveBee("copper_bee", ProductiveBeeEntity::new, 14851873, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> ELECTRUM_BEE = createHiveBee("electrum_bee", ProductiveBeeEntity::new, 16762718, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> INVAR_BEE = createHiveBee("invar_bee", ProductiveBeeEntity::new, 10661549, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> LEAD_BEE = createHiveBee("lead_bee", ProductiveBeeEntity::new, 6241124, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> NICKEL_BEE = createHiveBee("nickel_bee", ProductiveBeeEntity::new, 9429476, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> OSMIUM_BEE = createHiveBee("osmium_bee", ProductiveBeeEntity::new, 5021110, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> PLATINUM_BEE = createHiveBee("platinum_bee", ProductiveBeeEntity::new, 12118748, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> RADIOACTIVE_BEE = createHiveBee("radioactive_bee", ProductiveBeeEntity::new, 62574, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> SILVER_BEE = createHiveBee("silver_bee", ProductiveBeeEntity::new, 12836322, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> STEEL_BEE = createHiveBee("steel_bee", ProductiveBeeEntity::new, 6647662, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> TIN_BEE = createHiveBee("tin_bee", ProductiveBeeEntity::new, 15389140, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> TITANIUM_BEE = createHiveBee("titanium_bee", ProductiveBeeEntity::new, 11068133, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> TUNGSTEN_BEE = createHiveBee("tungsten_bee", ProductiveBeeEntity::new, 1926751, 15582019);
    public static RegistryObject<EntityType<BeeEntity>> ZINC_BEE = createHiveBee("zinc_bee", ProductiveBeeEntity::new, 15389140, 15582019);

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createHiveBee(String name, EntityType.IFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(HIVE_BEES, name, supplier, primaryColor, secondaryColor, ModItemGroups.PRODUCTIVE_BEES);
    }

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createSolitaryBee(String name, EntityType.IFactory<E> supplier, int primaryColor, int secondaryColor) {
        return createBee(SOLITARY_BEES, name, supplier, primaryColor, secondaryColor, ModItemGroups.PRODUCTIVE_BEES);
    }

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createBee(DeferredRegister<EntityType<?>> registry, String name, EntityType.IFactory<E> supplier, int primaryColor, int secondaryColor, ItemGroup itemGroup) {
        EntityType.Builder<E> builder = EntityType.Builder.<E>create(supplier, EntityClassification.CREATURE).size(0.7F, 0.6F);
        if (name.equals("magmatic_bee")) {
            builder.immuneToFire();
        }

        RegistryObject<EntityType<E>> entity = registry.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

        if (itemGroup != null) {
            RegistryObject<Item> spawnEgg = ModItems.ITEMS.register("spawn_egg_" + name, () -> new SpawnEgg(entity::get, primaryColor, secondaryColor, new Item.Properties().group(itemGroup)));
            ModItems.SPAWN_EGGS.add(spawnEgg);
        }

        return entity;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRendering() {
        for (RegistryObject<EntityType<?>> registryObject : HIVE_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            if (bee.getTranslationKey().contains("slimy_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, SlimyBeeRenderer::new);
            }
            else if (bee.getTranslationKey().contains("dye_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, DyeBeeRenderer::new);
            }
            else {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, ProductiveBeeRenderer::new);
            }
        }
        for (RegistryObject<EntityType<?>> registryObject : SOLITARY_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends SolitaryBeeEntity>) bee, SolitaryBeeRenderer::new);
        }
    }
}
