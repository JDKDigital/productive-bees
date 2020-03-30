package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.tileentity.DragonEggHiveTileEntity;
import cy.jdkdigital.productivebees.tileentity.SolitaryNestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ProductiveBees.MODID);

	public static final RegistryObject<TileEntityType<AdvancedBeehiveTileEntity>> ADVANCED_BEEHIVE = TILE_ENTITY_TYPES.register("advanced_beehive", () ->
			TileEntityType.Builder.create(AdvancedBeehiveTileEntity::new,
					ModBlocks.ADVANCED_BEEHIVE.get()
			).build(null)
	);
	public static final RegistryObject<TileEntityType<DragonEggHiveTileEntity>> DRACONIC_BEEHIVE = TILE_ENTITY_TYPES.register("draconic_beehive", () ->
			TileEntityType.Builder.create(DragonEggHiveTileEntity::new,
					ModBlocks.DRAGON_EGG_HIVE.get()
			).build(null)
	);
	public static final RegistryObject<TileEntityType<SolitaryNestTileEntity>> SOLITARY_NEST = TILE_ENTITY_TYPES.register("solitary_nest", () ->
		TileEntityType.Builder.create(SolitaryNestTileEntity::new,
			ModBlocks.SAND_NEST.get(),
			ModBlocks.STONE_NEST.get(),
			ModBlocks.COARSE_DIRT_NEST.get(),
			ModBlocks.SLIMY_NEST.get(),
			ModBlocks.NETHER_QUARTZ_NEST.get(),
			ModBlocks.NETHER_BRICK_NEST.get(),
			ModBlocks.GLOWSTONE_NEST.get(),
			ModBlocks.END_NEST.get(),
			ModBlocks.OBSIDIAN_PILLAR_NEST.get()
		).build(null)
	);
	
}
