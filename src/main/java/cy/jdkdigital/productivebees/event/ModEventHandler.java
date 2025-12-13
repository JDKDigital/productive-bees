package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BlueBandedBee;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.dispenser.CageDispenseBehavior;
import cy.jdkdigital.productivebees.dispenser.ShearsDispenseItemBehavior;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.network.packets.BeeDataMessage;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.ProductiveLib;
import cy.jdkdigital.productivelib.common.item.AbstractUpgradeItem;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

@EventBusSubscriber(modid = ProductiveBees.MODID)
public class ModEventHandler
{
    @SubscribeEvent
    public static void tabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ProductiveBees.TAB_KEY)) {
            for (DeferredHolder<Item, ? extends Item> item: ProductiveBees.ITEMS.getEntries()) {
                if (
                        !item.equals(ModItems.CONFIGURABLE_HONEYCOMB) &&
                                !item.equals(ModItems.CONFIGURABLE_COMB_BLOCK) &&
                                !item.equals(ModItems.CONFIGURABLE_SPAWN_EGG) &&
                                !item.equals(ModItems.GENE) &&
                                !item.equals(ModItems.GENE_BOTTLE) &&
                                !item.equals(ModItems.ADV_BREED_ALL_BEES) &&
                                !item.equals(ModItems.ADV_BREED_BEE) &&
                                !(item.get() instanceof SpawnEggItem) &&
                                !(item.get() instanceof AbstractUpgradeItem)
                ) {
                    event.accept(new ItemStack(item.get(), 1));
                }
            }

            for (DeferredHolder<Item, ? extends Item> item: ProductiveLib.ITEMS.getEntries()) {
                if (!item.is(LibItems.UPGRADE_POLLEN_SIEVE)) {
                    event.accept(item.get().getDefaultInstance());
                }
            }

            for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                ResourceLocation beeType = entry.getKey();

                // Add comb item
                if (entry.getValue().getBoolean("createComb")) {
                    ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    BeeCreator.setType(beeType, comb);

                    event.accept(comb);

                    // Add comb block
                    ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
                    BeeCreator.setType(beeType, combBlock);

                    event.accept(combBlock);
                }
            }

            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_NORMAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_MEDIUM, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_HIGH, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_VERY_HIGH, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_NONE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_RAIN, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_ANY, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_DIURNAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_NOCTURNAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_METATURNAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_PASSIVE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_NORMAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_HOSTILE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_AGGRESSIVE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_WEAK, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_NORMAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_MEDIUM, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_STRONG, 1, 100));

            BeeReloadListener.INSTANCE.getData().forEach((location, compoundTag) -> {
                event.accept(Gene.getStack(GeneAttribute.TYPE, location.toString(), 1, 100));
            });
            for (ResourceLocation entityType : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                if (entityType.getNamespace().equals(ProductiveBees.MODID) && entityType.getPath().contains("bee")) {
                    if (entityType.toString().equals("productivebees:configurable_bee")) {
                        continue;
                    }
                    event.accept(Gene.getStack(GeneAttribute.TYPE, entityType.toString(), 1, 100));
                }
            }
        }

        if (event.getTabKey().equals(ProductiveBees.TAB_KEY) || event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            for (DeferredHolder<Item, ? extends Item> spawnEgg: ModItems.SPAWN_EGGS) {
                if (!spawnEgg.equals(ModItems.CONFIGURABLE_SPAWN_EGG)) {
                    event.accept(new ItemStack(spawnEgg));
                }
            }
            for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                ResourceLocation beeType = entry.getKey();
                // Add spawn egg item
                event.accept(BeeCreator.getSpawnEgg(beeType));
            }
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(ModItems.BEE_CAGE.get(), new CageDispenseBehavior());
            DispenserBlock.registerBehavior(ModItems.STURDY_BEE_CAGE.get(), new CageDispenseBehavior());
            DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
        });
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.CONFIGURABLE_BEE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, serverLevel, spawnType, pos, random) -> {
            return random.nextBoolean() && spawnType.equals(MobSpawnType.NATURAL) && serverLevel.getBlockState(pos).canBeReplaced();
        }, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
        // Entity attribute assignments
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
            EntityType<ProductiveBee> bee = (EntityType<ProductiveBee>) registryObject.get();
            event.put(bee, Bee.createAttributes().build());
        }
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
            EntityType<ProductiveBee> bee = (EntityType<ProductiveBee>) registryObject.get();
            if (!bee.getDescriptionId().contains("blue_banded_bee")) {
                event.put(bee, Bee.createAttributes().build());
            }
        }
        event.put(ModEntities.BLUE_BANDED_BEE.get(), BlueBandedBee.getDefaultAttributes().build());
    }

    @SubscribeEvent
    public static void payloadHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ProductiveBees.MODID).versioned("1").optional();
        registrar.playToClient(
                BeeDataMessage.TYPE,
                BeeDataMessage.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        BeeDataMessage::clientHandle,
                        BeeDataMessage::serverHandle
                )
        );
    }

    @SubscribeEvent
    public static void onInterModEnqueue(InterModEnqueueEvent event) {
//        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopPlugin::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityCapabilities(RegisterCapabilitiesEvent event) {
        // Hives
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.ADVANCED_HIVE.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.CANVAS_ADVANCED_HIVE.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.EXPANSION_BOX.get(),
                (myBlockEntity, side) -> {
                    if (side != null && (side.equals(Direction.DOWN) || side.equals(Direction.UP))) {
                        return myBlockEntity.getHiveUpgradeHandler();
                    }
                    return myBlockEntity.getHiveInventoryHandler();
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.CANVAS_EXPANSION_BOX.get(),
                (myBlockEntity, side) -> {
                    if (side != null && (side.equals(Direction.DOWN) || side.equals(Direction.UP))) {
                        return myBlockEntity.getHiveUpgradeHandler();
                    }
                    return myBlockEntity.getHiveInventoryHandler();
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.DRACONIC_BEEHIVE.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        // Centrifuge
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
        // Powered centrifuge
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.POWERED_CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.POWERED_CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.POWERED_CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getEnergyHandler()
        );
        // Heated centrifuge
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.HEATED_CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.HEATED_CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.HEATED_CENTRIFUGE.get(),
                (myBlockEntity, side) -> myBlockEntity.getEnergyHandler()
        );
        // Bottler
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.BOTTLER.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.BOTTLER.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
        // Feeding slab
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.FEEDER.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        // Jar
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.JAR.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        // Honey generator
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.HONEY_GENERATOR.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.HONEY_GENERATOR.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.HONEY_GENERATOR.get(),
                (myBlockEntity, side) -> myBlockEntity.getEnergyHandler()
        );
        // Catcher
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.CATCHER.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        // Incubator
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.INCUBATOR.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.INCUBATOR.get(),
                (myBlockEntity, side) -> myBlockEntity.getEnergyHandler()
        );
        // Gene indexer
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.GENE_INDEXER.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        // Breeding chamber
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.BREEDING_CHAMBER.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.BREEDING_CHAMBER.get(),
                (myBlockEntity, side) -> myBlockEntity.getEnergyHandler()
        );
    }
}
