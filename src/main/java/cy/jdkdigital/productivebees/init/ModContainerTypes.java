package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ProductiveBees.MODID);

    public static final RegistryObject<MenuType<AdvancedBeehiveContainer>> ADVANCED_BEEHIVE = CONTAINER_TYPES.register("advanced_beehive", () ->
            IForgeMenuType.create(AdvancedBeehiveContainer::new)
    );
    public static final RegistryObject<MenuType<CentrifugeContainer>> CENTRIFUGE = CONTAINER_TYPES.register("centrifuge", () ->
            IForgeMenuType.create(CentrifugeContainer::new)
    );
    public static final RegistryObject<MenuType<PoweredCentrifugeContainer>> POWERED_CENTRIFUGE = CONTAINER_TYPES.register("powered_centrifuge", () ->
            IForgeMenuType.create(PoweredCentrifugeContainer::new)
    );
    public static final RegistryObject<MenuType<BottlerContainer>> BOTTLER = CONTAINER_TYPES.register("bottler", () ->
            IForgeMenuType.create(BottlerContainer::new)
    );
    public static final RegistryObject<MenuType<FeederContainer>> FEEDER = CONTAINER_TYPES.register("feeder", () ->
            IForgeMenuType.create(FeederContainer::new)
    );
    public static final RegistryObject<MenuType<IncubatorContainer>> INCUBATOR = CONTAINER_TYPES.register("incubator", () ->
            IForgeMenuType.create(IncubatorContainer::new)
    );
    public static final RegistryObject<MenuType<CatcherContainer>> CATCHER = CONTAINER_TYPES.register("catcher", () ->
            IForgeMenuType.create(CatcherContainer::new)
    );
    public static final RegistryObject<MenuType<HoneyGeneratorContainer>> HONEY_GENERATOR = CONTAINER_TYPES.register("honey_generator", () ->
            IForgeMenuType.create(HoneyGeneratorContainer::new)
    );
    public static final RegistryObject<MenuType<GeneIndexerContainer>> GENE_INDEXER = CONTAINER_TYPES.register("gene_indexer", () ->
            IForgeMenuType.create(GeneIndexerContainer::new)
    );
    public static final RegistryObject<MenuType<BreedingChamberContainer>> BREEDING_CHAMBER = CONTAINER_TYPES.register("breeding_chamber", () ->
            IForgeMenuType.create(BreedingChamberContainer::new)
    );
}
