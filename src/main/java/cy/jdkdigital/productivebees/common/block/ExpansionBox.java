package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.ExpansionBoxBlockEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ExpansionBox extends Block implements EntityBlock
{
    public static final BooleanProperty HAS_HONEY = BooleanProperty.create("has_honey");

    public ExpansionBox(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH)
                .setValue(AdvancedBeehive.EXPANDED, VerticalHive.NONE)
                .setValue(HAS_HONEY, false)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BeehiveBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BeehiveBlock.FACING, AdvancedBeehive.EXPANDED, HAS_HONEY);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExpansionBoxBlockEntity(this, pos, state);
    }

    public void updateState(Level level, BlockPos pos, BlockState state, boolean isRemoved) {
        Pair<Pair<BlockPos, Direction>, BlockState> pair = state.getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE) ? getAdjacentHive(level, pos) : getAttachedHive(state, level, pos);
        if (pair != null) {
            Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
            BlockPos hivePos = posAndDirection.getLeft();
            VerticalHive directionProperty = AdvancedBeehive.calculateExpandedDirection(level, hivePos, isRemoved);

            if (!isRemoved) {
                updateStateWithDirection(level, pos, state, directionProperty, pair.getRight().getValue(BeehiveBlock.FACING));
            }
            ((AdvancedBeehive) pair.getRight().getBlock()).updateStateWithDirection(level, hivePos, pair.getRight(), directionProperty);
        } else {
            // No hive
            if (!isRemoved) {
                updateStateWithDirection(level, pos, state, VerticalHive.NONE, state.getValue(BeehiveBlock.FACING));
            }
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        if (level instanceof ServerLevel && heldItem.getItem().equals(Items.STICK)) {
            if (!state.getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE)) {
                Pair<Pair<BlockPos, Direction>, BlockState> pair = getAttachedHive(state, level, pos);
                if (pair != null) {
                    Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
                    BlockPos hivePos = posAndDirection.getLeft();
                    BlockEntity hiveTileEntity = level.getBlockEntity(hivePos);
                    if (hiveTileEntity instanceof AdvancedBeehiveBlockEntityAbstract) {
                        ((AdvancedBeehiveBlockEntityAbstract) hiveTileEntity).emptyAllLivingFromHive(player, state, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                    }
                }
            }
        }
        super.attack(state, level, pos, player);
    }

    public void updateStateWithDirection(Level level, BlockPos pos, BlockState state, VerticalHive directionProperty, Direction facing) {
        // unregister any hives thinking this is their expansion box which are not facing here
//        ProductiveBees.LOGGER.info("updateStateWithDirection " + facing + " " + directionProperty + " " + pos);
//        for (Direction direction : BlockStateProperties.FACING.getPossibleValues()) {
//            ProductiveBees.LOGGER.info("check direction " + direction);
//            if (!direction.equals(facing)) {
//                var hiveState = level.getBlockState(pos.relative(direction));
//                if (hiveState.getBlock() instanceof AdvancedBeehive hive) {
//                    ProductiveBees.LOGGER.info("other hive state " + hiveState);
//                    ProductiveBees.LOGGER.info("facing " + hiveState.getValue(BeehiveBlock.FACING));
//                    ProductiveBees.LOGGER.info("expanded " + hiveState.getValue(AdvancedBeehive.EXPANDED));
//                }
//            }
//        }
        level.setBlockAndUpdate(pos, state.setValue(AdvancedBeehive.EXPANDED, directionProperty).setValue(BeehiveBlock.FACING, facing));
    }

    public static Pair<Pair<BlockPos, Direction>, BlockState> getAdjacentHive(Level level, BlockPos pos) {
        for (Direction direction : BlockStateProperties.FACING.getPossibleValues()) {
            BlockPos newPos = pos.relative(direction);
            BlockState blockStateAtPos = level.getBlockState(newPos);

            Block blockAtPos = blockStateAtPos.getBlock();
            if (blockAtPos instanceof AdvancedBeehive && !(blockAtPos instanceof DragonEggHive)) {
                return Pair.of(Pair.of(newPos, direction), blockStateAtPos);
            }
        }
        return null;
    }

    public static Pair<Pair<BlockPos, Direction>, BlockState> getAttachedHive(BlockState boxState, Level level, BlockPos pos) {
        if (boxState.is(ModTags.BOXES_BLOCK)) {
            VerticalHive expandDirection = boxState.getValue(AdvancedBeehive.EXPANDED);
            var hiveDirection = expandDirection.getExpandedCardinalDirection(boxState.getValue(BeehiveBlock.FACING)).getOpposite();

            BlockState blockStateAtPos = level.getBlockState(pos.relative(hiveDirection));
            if (blockStateAtPos.getBlock() instanceof AdvancedBeehive && !(blockStateAtPos.getBlock() instanceof DragonEggHive)) {
                return Pair.of(Pair.of(pos.relative(hiveDirection), hiveDirection), blockStateAtPos);
            }
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide()) {
            this.updateState(world, pos, state, false);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);

        if (!world.isClientSide()) {
            this.updateState(world, pos, state, true);
        }

        return removed;
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand handIn, final BlockHitResult hit) {
        if (!level.isClientSide) {
            // Open the attached beehive, if there is one
            if (!state.getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE)) {
                var dir = state.getValue(AdvancedBeehive.EXPANDED).getExpandedCardinalDirection(state.getValue(BeehiveBlock.FACING)).getOpposite();
                if (level.getBlockEntity(pos.relative(dir)) instanceof AdvancedBeehiveBlockEntity hiveBlockEntity) {
                    ((AdvancedBeehive) level.getBlockState(pos.relative(dir)).getBlock()).openGui((ServerPlayer) player, hiveBlockEntity);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
