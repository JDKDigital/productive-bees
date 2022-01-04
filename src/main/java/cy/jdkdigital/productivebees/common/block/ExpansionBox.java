package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class ExpansionBox extends Block
{
    public static final BooleanProperty HAS_HONEY = BooleanProperty.create("has_honey");

    public ExpansionBox(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH)
                .setValue(AdvancedBeehive.EXPANDED, VerticalHive.NONE)
                .setValue(HAS_HONEY, false)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(BeehiveBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BeehiveBlock.FACING, AdvancedBeehive.EXPANDED, HAS_HONEY);
    }

    public void updateState(World world, BlockPos pos, BlockState state, boolean isRemoved) {
        Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentHive(world, pos);
        if (pair != null) {
            Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
            BlockPos hivePos = posAndDirection.getLeft();
            VerticalHive directionProperty = AdvancedBeehive.calculateExpandedDirection(world, hivePos, isRemoved);

            if (!isRemoved) {
                updateStateWithDirection(world, pos, state, directionProperty);
            }
            ((AdvancedBeehive) pair.getRight().getBlock()).updateStateWithDirection(world, hivePos, pair.getRight(), directionProperty);
        } else {
            // No hive
            if (!isRemoved) {
                updateStateWithDirection(world, pos, state, VerticalHive.NONE);
            }
        }
    }

    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        ItemStack heldItem = player.getMainHandItem();
        if (worldIn instanceof ServerWorld && heldItem.getItem().equals(Items.STICK)) {
            if (!state.getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE)) {
                Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentHive(worldIn, pos);
                if (pair != null) {
                    Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
                    BlockPos hivePos = posAndDirection.getLeft();
                    TileEntity hiveTileEntity = worldIn.getBlockEntity(hivePos);
                    if (hiveTileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                        ((AdvancedBeehiveTileEntityAbstract) hiveTileEntity).emptyAllLivingFromHive(player, state, BeehiveTileEntity.State.BEE_RELEASED);
                    }
                }
            }
        }
        super.attack(state, worldIn, pos, player);
    }

    public void updateStateWithDirection(World world, BlockPos pos, BlockState state, VerticalHive directionProperty) {
        world.setBlockAndUpdate(pos, state.setValue(AdvancedBeehive.EXPANDED, directionProperty));
    }

    public static Pair<Pair<BlockPos, Direction>, BlockState> getAdjacentHive(World world, BlockPos pos) {
        for (Direction direction : BlockStateProperties.FACING.getPossibleValues()) {
            if (direction == Direction.UP) {
                continue;
            }
            BlockPos newPos = pos.relative(direction);
            BlockState blockStateAtPos = world.getBlockState(newPos);

            Block blockAtPos = blockStateAtPos.getBlock();
            if (blockAtPos instanceof AdvancedBeehive && !(blockAtPos instanceof DragonEggHive)) {
                return Pair.of(Pair.of(newPos, direction), blockStateAtPos);
            }
        }
        return null;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide()) {
            this.updateState(world, pos, state, false);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        boolean removed = super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

        if (!world.isClientSide()) {
            this.updateState(world, pos, state, true);
        }

        return removed;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.EXPANSION_BOX.get().create();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
        if (!worldIn.isClientSide) {
            // Open the attached beehive, if there is one
            Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentHive(worldIn, pos);
            if (pair != null) {
                final TileEntity tileEntity = worldIn.getBlockEntity(pair.getLeft().getLeft());
                if (tileEntity instanceof AdvancedBeehiveTileEntity) {
                    this.updateState(worldIn, pos, state, false);
                    BlockState blockState = tileEntity.getBlockState();
                    worldIn.sendBlockUpdated(pos, blockState, blockState, Constants.BlockFlags.DEFAULT);
                    ((AdvancedBeehive) blockState.getBlock()).openGui((ServerPlayerEntity) player, (AdvancedBeehiveTileEntity) tileEntity);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }
}
