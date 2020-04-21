package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.tileentity.SolitaryNestTileEntity;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import javax.annotation.Nullable;

abstract public class SolitaryNest extends AdvancedBeehiveAbstract {

	public SolitaryNest(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH));
	}

	public int getMaxHoneyLevel() {
		return 0;
	}

	abstract public EntityType<BeeEntity> getNestingBeeType(World world);

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.SOLITARY_NEST.get().create();
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new SolitaryNestTileEntity();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
		return this.getDefaultState().with(BlockStateProperties.FACING, itemUseContext.getNearestLookingDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.with(BlockStateProperties.FACING, rotation.rotate(state.get(BlockStateProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.toRotation(state.get(BlockStateProperties.FACING)));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING, BeehiveBlock.HONEY_LEVEL);
	}

	public boolean canRepopulateIn(Dimension dimension, Biome biome) {
		return dimension.isSurfaceWorld();
	}

	public int getRepopulationCooldown() {
		return 36000; // 30 minutes
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote()) {
			SolitaryNestTileEntity tileEntity = (SolitaryNestTileEntity) world.getTileEntity(pos);
			ProductiveBees.LOGGER.info("Nest tilentity: " + tileEntity);
			ProductiveBees.LOGGER.info("Nest sealed: " + tileEntity.isSealed());
			ProductiveBees.LOGGER.info("Bee count: " + tileEntity.getBeeList().size());
			ProductiveBees.LOGGER.info("Occupants: " + tileEntity.getBeeList());
			ProductiveBees.LOGGER.info("Egg count: " + tileEntity.getEggs().size());
			ProductiveBees.LOGGER.info("Eggs: " + tileEntity.getEggListAsNBTList());

			return ActionResultType.PASS;
		}
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}
}
