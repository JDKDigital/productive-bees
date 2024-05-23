package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(Registries.MENU, ProductiveBees.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AdvancedBeehiveContainer>> ADVANCED_BEEHIVE = CONTAINER_TYPES.register("advanced_beehive", () ->
            IMenuTypeExtension.create(AdvancedBeehiveContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<CentrifugeContainer>> CENTRIFUGE = CONTAINER_TYPES.register("centrifuge", () ->
            IMenuTypeExtension.create(CentrifugeContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<PoweredCentrifugeContainer>> POWERED_CENTRIFUGE = CONTAINER_TYPES.register("powered_centrifuge", () ->
            IMenuTypeExtension.create(PoweredCentrifugeContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<HeatedCentrifugeContainer>> HEATED_CENTRIFUGE = CONTAINER_TYPES.register("heated_centrifuge", () ->
            IMenuTypeExtension.create(HeatedCentrifugeContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<BottlerContainer>> BOTTLER = CONTAINER_TYPES.register("bottler", () ->
            IMenuTypeExtension.create(BottlerContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<FeederContainer>> FEEDER = CONTAINER_TYPES.register("feeder", () ->
            IMenuTypeExtension.create(FeederContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<IncubatorContainer>> INCUBATOR = CONTAINER_TYPES.register("incubator", () ->
            IMenuTypeExtension.create(IncubatorContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<CatcherContainer>> CATCHER = CONTAINER_TYPES.register("catcher", () ->
            IMenuTypeExtension.create(CatcherContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<HoneyGeneratorContainer>> HONEY_GENERATOR = CONTAINER_TYPES.register("honey_generator", () ->
            IMenuTypeExtension.create(HoneyGeneratorContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<GeneIndexerContainer>> GENE_INDEXER = CONTAINER_TYPES.register("gene_indexer", () ->
            IMenuTypeExtension.create(GeneIndexerContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<BreedingChamberContainer>> BREEDING_CHAMBER = CONTAINER_TYPES.register("breeding_chamber", () ->
            IMenuTypeExtension.create(BreedingChamberContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<CryoStasisContainer>> CRYO_STASIS = CONTAINER_TYPES.register("cryo_stasis", () ->
            IMenuTypeExtension.create(CryoStasisContainer::new)
    );
}
