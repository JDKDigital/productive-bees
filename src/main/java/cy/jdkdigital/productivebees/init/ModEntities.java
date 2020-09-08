package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.*;
import cy.jdkdigital.productivebees.entity.BeeBombEntity;
import cy.jdkdigital.productivebees.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.*;
import cy.jdkdigital.productivebees.entity.bee.nesting.*;
import cy.jdkdigital.productivebees.entity.bee.solitary.*;
import cy.jdkdigital.productivebees.item.SpawnEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> HIVE_BEES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ProductiveBees.MODID);
    public static final DeferredRegister<EntityType<?>> SOLITARY_BEES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ProductiveBees.MODID);

    public static RegistryObject<EntityType<ProjectileItemEntity>> BEE_BOMB = createEntity("bee_bomb", BeeBombEntity::new);

    public static RegistryObject<EntityType<BeeEntity>> IRON_BEE = createColoredHiveBee("iron_bee", IronBeeEntity::new, "#cdcdcd", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> GOLD_BEE = createColoredHiveBee("gold_bee", GoldBeeEntity::new, "#c8df24", "#804f40");

    public static RegistryObject<EntityType<BeeEntity>> COAL_BEE = createColoredHiveBee("coal_bee", ProductiveBeeEntity::new, "#222525", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> REDSTONE_BEE = createColoredHiveBee("redstone_bee", ProductiveBeeEntity::new, "#d03621", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> LAPIS_BEE = createColoredHiveBee("lapis_bee", ProductiveBeeEntity::new, "#3537bc", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> EMERALD_BEE = createColoredHiveBee("emerald_bee", ProductiveBeeEntity::new, "#26ac43", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> DIAMOND_BEE = createColoredHiveBee("diamond_bee", ProductiveBeeEntity::new, "#3ddfe1", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> DYE_BEE = createHiveBee("dye_bee", ProductiveBeeEntity::new, 16768648, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> LUMBER_BEE = createHiveBee("lumber_bee", LumberBeeEntity::new, 8306542, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> RANCHER_BEE = createHiveBee("rancher_bee", RancherBeeEntity::new, 9615358, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> HOARDER_BEE = createHiveBee("hoarder_bee", HoarderBeeEntity::new, 8306149, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> FARMER_BEE = createHiveBee("farmer_bee", FarmerBeeEntity::new, 9615358, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> CARTOGRAPHER_BEE = createHiveBee("cartographer_bee", ProductiveBeeEntity::new, 9615358, 6238757);

    public static RegistryObject<EntityType<BeeEntity>> CREEPER_BEE = createHiveBee("creeper_bee", CreeperBeeEntity::new, 894731, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> ENDER_BEE = createHiveBee("ender_bee", EnderBeeEntity::new, 1447446, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> WITHER_BEE = createHiveBee("wither_bee", WitherBeeEntity::new, 1315860, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> BLAZING_BEE = createHiveBee("blazing_bee", BlazingBeeEntity::new, 16763648, 6238757);

    public static RegistryObject<EntityType<BeeEntity>> ZOMBIE_BEE = createHiveBee("zombie_bee", ZombieBeeEntity::new, 7969893, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> SKELETAL_BEE = createHiveBee("skeletal_bee", SkeletalBeeEntity::new, 12698049, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> GLOWING_BEE = createHiveBee("glowing_bee", GlowingBeeEntity::new, 16579584, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> QUARTZ_BEE = createColoredHiveBee("quartz_bee", QuartzBeeEntity::new, "#ede5dd", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> MAGMATIC_BEE = createHiveBee("magmatic_bee", MagmaticBeeEntity::new, 3407872, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> DRACONIC_BEE = createHiveBee("draconic_bee", DraconicBeeEntity::new, 1842204, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> SLIMY_BEE = createHiveBee("slimy_bee", SlimyBeeEntity::new, 8306542, 6238757);

    public static RegistryObject<EntityType<BeeEntity>> ASHY_MINING_BEE = createSolitaryBee("ashy_mining_bee", SolitaryBeeEntity::new, 11709345, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> BLUE_BANDED_BEE = createSolitaryBee("blue_banded_bee", BlueBandedBeeEntity::new, 9615358, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> GREEN_CARPENTER_BEE = createSolitaryBee("green_carpenter_bee", GreenCarpenterBeeEntity::new, 9615358, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> YELLOW_BLACK_CARPENTER_BEE = createSolitaryBee("yellow_black_carpenter_bee", YellowBlackCarpenterBeeEntity::new, 15582019, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> CHOCOLATE_MINING_BEE = createSolitaryBee("chocolate_mining_bee", SolitaryBeeEntity::new, 11709345, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> DIGGER_BEE = createSolitaryBee("digger_bee", SolitaryBeeEntity::new, 8875079, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> LEAFCUTTER_BEE = createSolitaryBee("leafcutter_bee", SolitaryBeeEntity::new, 2057258, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> MASON_BEE = createSolitaryBee("mason_bee", SolitaryBeeEntity::new, 2226382, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> NEON_CUCKOO_BEE = createSolitaryBee("neon_cuckoo_bee", SolitaryBeeEntity::new, 9615358, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> NOMAD_BEE = createSolitaryBee("nomad_bee", NomadBeeEntity::new, 14529911, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> REED_BEE = createSolitaryBee("reed_bee", ReedBeeEntity::new, 13806336, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> RESIN_BEE = createSolitaryBee("resin_bee", ResinBeeEntity::new, 13939231, 6238757);
    public static RegistryObject<EntityType<BeeEntity>> SWEATY_BEE = createSolitaryBee("sweaty_bee", SweatyBeeEntity::new, 9748939, 6238757);

    // @deprecated
    public static RegistryObject<EntityType<BeeEntity>> ALUMINIUM_BEE = createColoredHiveBee("aluminium_bee", ProductiveBeeEntity::new, "#A4A6B1", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> AMBER_BEE = createColoredHiveBee("amber_bee", ProductiveBeeEntity::new, "#d2ab00", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> BRASS_BEE = createColoredHiveBee("brass_bee", ProductiveBeeEntity::new, "#DAAA4C", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> BRONZE_BEE = createColoredHiveBee("bronze_bee", ProductiveBeeEntity::new, "#C98C52", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> COPPER_BEE = createColoredHiveBee("copper_bee", ProductiveBeeEntity::new, "#F48702", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> ELECTRUM_BEE = createColoredHiveBee("electrum_bee", ProductiveBeeEntity::new, "#D5BB4F", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> INVAR_BEE = createColoredHiveBee("invar_bee", ProductiveBeeEntity::new, "#ADB7B2", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> LEAD_BEE = createColoredHiveBee("lead_bee", ProductiveBeeEntity::new, "#677193", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> NICKEL_BEE = createColoredHiveBee("nickel_bee", ProductiveBeeEntity::new, "#D8CC93", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> OSMIUM_BEE = createColoredHiveBee("osmium_bee", ProductiveBeeEntity::new, "#4c9db6", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> PLATINUM_BEE = createColoredHiveBee("platinum_bee", ProductiveBeeEntity::new, "#6FEAEF", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> RADIOACTIVE_BEE = createColoredHiveBee("radioactive_bee", ProductiveBeeEntity::new, "#60AE11", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> SILVER_BEE = createColoredHiveBee("silver_bee", ProductiveBeeEntity::new, "#A9DBE5", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> STEEL_BEE = createColoredHiveBee("steel_bee", ProductiveBeeEntity::new, "#737373", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> TIN_BEE = createColoredHiveBee("tin_bee", ProductiveBeeEntity::new, "#9ABDD6", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> TITANIUM_BEE = createColoredHiveBee("titanium_bee", ProductiveBeeEntity::new, "#D0D1DA", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> TUNGSTEN_BEE = createColoredHiveBee("tungsten_bee", ProductiveBeeEntity::new, "#616669", "#804f40");
    public static RegistryObject<EntityType<BeeEntity>> ZINC_BEE = createColoredHiveBee("zinc_bee", ProductiveBeeEntity::new, "#E9EBE7", "#804f40");

    public static RegistryObject<EntityType<ConfigurableBeeEntity>> CONFIGURABLE_BEE = createColoredHiveBee("configurable_bee", ConfigurableBeeEntity::new, "#73ffb9", "#0f5c7a");

    public static <E extends BeeEntity> RegistryObject<EntityType<E>> createColoredHiveBee(String name, EntityType.IFactory<E> supplier, String primaryColor, String secondaryColor) {
        Color primary = Color.decode(primaryColor);
        Color secondary = Color.decode(secondaryColor);
        return createHiveBee(name, (entityType, world) -> {
            ProductiveBeeEntity bee = (ProductiveBeeEntity) supplier.create(entityType, world);
            bee.setColor(primary, secondary);
            return (E) bee;
        }, primary.getRGB(), secondary.getRGB());
    }

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
            RegistryObject<Item> spawnEgg = ModItems.ITEMS.register("spawn_egg_" + name, () -> new SpawnEgg(entity::get, secondaryColor, primaryColor, new Item.Properties().group(itemGroup)));
            if (name.equals("configurable_bee")) {
                ModItems.CONFIGURABLE_SPAWN_EGG = spawnEgg;
            }
            ModItems.SPAWN_EGGS.add(spawnEgg);
        }


        return entity;
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> createEntity(String name, EntityType.IFactory<E> supplier) {
        EntityType.Builder<E> builder = EntityType.Builder.<E>create(supplier, EntityClassification.MISC).size(0.25F, 0.25F);

        RegistryObject<EntityType<E>> entity = ENTITIES.register(name, () -> builder.build(ProductiveBees.MODID + ":" + name));

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
            else if (bee.getTranslationKey().contains("rancher_bee") || bee.getTranslationKey().contains("farmer_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, RancherBeeRenderer::new);
            }
            else if (bee.getTranslationKey().contains("hoarder_bee")) {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, HoarderBeeRenderer::new);
            }
            else {
                RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends ProductiveBeeEntity>) bee, ProductiveBeeRenderer::new);
            }
        }
        for (RegistryObject<EntityType<?>> registryObject : SOLITARY_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends SolitaryBeeEntity>) bee, SolitaryBeeRenderer::new);
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderingRegistry.registerEntityRenderingHandler(BEE_BOMB.get(), entity -> new SpriteRenderer<>(entity, itemRenderer));
    }
}
