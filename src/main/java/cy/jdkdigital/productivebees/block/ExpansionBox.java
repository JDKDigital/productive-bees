package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ExpansionBox extends Block {

	public static final BooleanProperty HAS_HONEY = BooleanProperty.create("has_honey");

	public ExpansionBox(final Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState()
			.with(AdvancedBeehive.EXPANDED, false)
			.with(HAS_HONEY, false)
		);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING, AdvancedBeehive.EXPANDED, HAS_HONEY);
	}

	public void updateState(World world, BlockPos pos, BlockState state, boolean isRemoved) {
		BlockPos posDown = pos.down();
		BlockState blockStateBelow = world.getBlockState(posDown);
		Block blockBelow = blockStateBelow.getBlock();

		if (!isRemoved) {
			// Set this block to expanded if there's an advanced beehive below and the block has not been removed
			state = state.with(AdvancedBeehive.EXPANDED, blockBelow instanceof AdvancedBeehive);
			world.setBlockState(pos, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
		}

		if (blockBelow instanceof AdvancedBeehive) {
			if (!isRemoved) {
				int honeyLevel = blockStateBelow.get(BeehiveBlock.HONEY_LEVEL);
				int maxHoneyLevel = ((AdvancedBeehiveAbstract)blockBelow).getMaxHoneyLevel();
				world.setBlockState(pos, state
					// Fix expansion box direction to match the beehive
					.with(BlockStateProperties.FACING, blockStateBelow.get(BlockStateProperties.FACING))
					// Set honey state based on the beehive below
					.with(HAS_HONEY, honeyLevel >= maxHoneyLevel)
				, 3);
			}
			// Update beehive
			world.setBlockState(posDown, blockStateBelow.with(AdvancedBeehive.EXPANDED, !isRemoved), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
			if (!world.isAirBlock(pos)) {
				BlockState freshState = world.getBlockState(pos);
				((AdvancedBeehiveTileEntity)world.getTileEntity(posDown)).MAX_BEES = freshState.get(AdvancedBeehive.EXPANDED) ? 5 : 3;
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (!world.isRemote()) {
			this.updateState(world, pos, state, false);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		boolean removed = super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

		if (!world.isRemote()) {
			this.updateState(world, pos, state, true);
		}

		return removed;
	}

	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			// Open the beehive below, if there is one
			final TileEntity tileEntity = worldIn.getTileEntity(pos.down());
			if (tileEntity instanceof AdvancedBeehiveTileEntity) {
				Block block = tileEntity.getBlockState().getBlock();
				((AdvancedBeehive)block).openGui((ServerPlayerEntity) player, (AdvancedBeehiveTileEntity) tileEntity);
			}
		}
		return ActionResultType.SUCCESS;
	}
}
