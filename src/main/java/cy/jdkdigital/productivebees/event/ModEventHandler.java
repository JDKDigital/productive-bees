package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BlueBandedBee;
import cy.jdkdigital.productivebees.dispenser.CageDispenseBehavior;
import cy.jdkdigital.productivebees.dispenser.ShearsDispenseItemBehavior;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.network.packets.BeeDataMessage;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(ModItems.BEE_CAGE.get(), new CageDispenseBehavior());
            DispenserBlock.registerBehavior(ModItems.STURDY_BEE_CAGE.get(), new CageDispenseBehavior());
            DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
        });
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
