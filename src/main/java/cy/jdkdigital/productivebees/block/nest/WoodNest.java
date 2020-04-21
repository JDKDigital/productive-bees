package cy.jdkdigital.productivebees.block.nest;

import cy.jdkdigital.productivebees.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.tileentity.SolitaryHiveTileEntity;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import javax.annotation.Nullable;

public class WoodNest extends SolitaryNest {
	public static final EnumProperty<Direction.Axis> AXIS;

	public WoodNest(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Y));
	}

	@Override
	public boolean canRepopulateIn(Dimension dimension, Biome biome) {
		return dimension.isSurfaceWorld();
	}

	@Override
	public EntityType<BeeEntity> getNestingBeeType(World world) {
		switch (world.rand.nextInt(5)) {
			case 0:
				return ModEntities.MASON_BEE.get();
			case 1:
				return ModEntities.DIGGER_BEE.get();
			case 2:
				return ModEntities.MINING_BEE.get();
			default:
				return ModEntities.LEAFCUTTER_BEE.get();
		}
	}

	public BlockState rotate(BlockState state, Rotation rotation) {
		switch(rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch(state.get(AXIS)) {
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING, BeehiveBlock.HONEY_LEVEL, AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(AXIS, context.getFace().getAxis());
	}

	static {
		AXIS = BlockStateProperties.AXIS;
	}
}
