package cy.jdkdigital.productivebees.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class AdvancedBeehive extends AdvancedBeehiveAbstract
{
    public static final MapCodec<AdvancedBeehive> CODEC = simpleCodec(AdvancedBeehive::new);
    public static final EnumProperty<VerticalHive> EXPANDED = EnumProperty.create("expanded", VerticalHive.class);

    public AdvancedBeehive(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH)
                .setValue(EXPANDED, VerticalHive.NONE)
                .setValue(BeehiveBlock.HONEY_LEVEL, 0)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedBeehiveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntityTypes.ADVANCED_HIVE.get(), AdvancedBeehiveBlockEntity::tick);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BeehiveBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EXPANDED, BeehiveBlock.HONEY_LEVEL, BeehiveBlock.FACING);
    }

    public void updateState(Level world, BlockPos pos, BlockState state, boolean isRemoved) {
        if (this instanceof DragonEggHive) {
            return;
        }

        Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentBox(world, pos, false);
        if (pair != null) {
            Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
            BlockPos boxPos = posAndDirection.getLeft();

            VerticalHive directionProperty = AdvancedBeehive.calculateExpandedDirection(world, pos, isRemoved);

            if (!isRemoved) {
                updateStateWithDirection(world, pos, state, directionProperty);
            }
            ((ExpansionBox) pair.getRight().getBlock()).updateStateWithDirection(world, boxPos, pair.getRight(), directionProperty, state.getValue(BeehiveBlock.FACING));
        } else {
            // No expansion box
            if (!isRemoved) {
                updateStateWithDirection(world, pos, state, VerticalHive.NONE);
            }
        }
    }

    public void updateStateWithDirection(Level world, BlockPos pos, BlockState state, VerticalHive directionProperty) {
        world.setBlockAndUpdate(pos, state.setValue(AdvancedBeehive.EXPANDED, directionProperty));
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
            advancedBeehiveBlockEntity.MAX_BEES = world.getBlockState(pos).getValue(EXPANDED) != VerticalHive.NONE ? 5 : 3;
            if (directionProperty.equals(VerticalHive.NONE)) {
                var handler = advancedBeehiveBlockEntity.getUpgradeHandler();
                for (int slot = 0; slot < handler.getSlots(); ++slot) {
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                }
            }
        }
    }

    public static Pair<Pair<BlockPos, Direction>, BlockState> getAdjacentBox(Level world, BlockPos pos, boolean onlyFreeBoxes) {
        for (Direction direction : BlockStateProperties.FACING.getPossibleValues()) {
            BlockPos newPos = pos.relative(direction);
            BlockState blockStateAtPos = world.getBlockState(newPos);

            if (blockStateAtPos.getBlock() instanceof ExpansionBox) {
                return Pair.of(Pair.of(newPos, direction), blockStateAtPos);
            }
        }
        return null;
    }

    public static VerticalHive calculateExpandedDirection(Level world, BlockPos hivePos, boolean isRemoved) {
        Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentBox(world, hivePos, true);
        VerticalHive directionProperty = VerticalHive.NONE;
        if (!isRemoved && pair != null) {
            BlockState hiveBlockState = world.getBlockState(hivePos);
            Direction hiveDirection = hiveBlockState.getValue(BeehiveBlock.FACING);
            Direction boxDirection = pair.getLeft().getRight();

            Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
            BlockPos boxPos = posAndDirection.getLeft();

            boolean isValidExpandedPos = !boxPos.equals(hivePos.relative(hiveDirection));
            if (!isValidExpandedPos && hiveDirection == Direction.NORTH) {
                isValidExpandedPos = boxDirection == Direction.WEST || boxDirection == Direction.EAST || boxDirection == Direction.SOUTH;
            } else if (!isValidExpandedPos && hiveDirection == Direction.SOUTH) {
                isValidExpandedPos = boxDirection == Direction.WEST || boxDirection == Direction.EAST || boxDirection == Direction.NORTH;
            } else if (!isValidExpandedPos && hiveDirection == Direction.WEST) {
                isValidExpandedPos = boxDirection == Direction.SOUTH || boxDirection == Direction.NORTH || boxDirection == Direction.EAST;
            } else if (!isValidExpandedPos && hiveDirection == Direction.EAST) {
                isValidExpandedPos = boxDirection == Direction.SOUTH || boxDirection == Direction.NORTH || boxDirection == Direction.WEST;
            }

            if (isValidExpandedPos) {
                if (hivePos.getY() - boxPos.getY() > 0) {
                    directionProperty = VerticalHive.DOWN;
                } else if (hivePos.getY() - boxPos.getY() < 0) {
                    directionProperty = VerticalHive.UP;
                } else if (hivePos.getX() < boxPos.getX()) {
                    directionProperty =  hiveDirection == Direction.WEST ? VerticalHive.BACK : hiveDirection == Direction.NORTH ? VerticalHive.LEFT : VerticalHive.RIGHT;
                } else if (hivePos.getX() > boxPos.getX()) {
                    directionProperty =  hiveDirection == Direction.EAST ? VerticalHive.BACK : hiveDirection == Direction.SOUTH ? VerticalHive.LEFT : VerticalHive.RIGHT;
                } else if (hivePos.getZ() < boxPos.getZ()) {
                    directionProperty = hiveDirection == Direction.NORTH ? VerticalHive.BACK : hiveDirection == Direction.EAST ? VerticalHive.LEFT : VerticalHive.RIGHT;
                } else if (hivePos.getZ() > boxPos.getZ()) {
                    directionProperty = hiveDirection == Direction.SOUTH ? VerticalHive.BACK : hiveDirection == Direction.WEST ? VerticalHive.LEFT : VerticalHive.RIGHT;
                }
            }
        }
        return directionProperty;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide()) {
            this.updateState(level, pos, state, false);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);

        if (!level.isClientSide()) {
            this.updateState(level, pos, state, true);
        }

        return removed;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState oldState, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
                // Drop inventory
                for (int slot = 0; slot < advancedBeehiveBlockEntity.inventoryHandler.getSlots(); ++slot) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), advancedBeehiveBlockEntity.inventoryHandler.getStackInSlot(slot));
                }
                var upgradeHandler = advancedBeehiveBlockEntity.getUpgradeHandler();
                for (int slot = 0; slot < upgradeHandler.getSlots(); ++slot) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), upgradeHandler.getStackInSlot(slot));
                }
            }
        }
        super.onRemove(oldState, worldIn, pos, newState, isMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            if (pLevel.getBlockEntity(pPos) instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
                pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                openGui((ServerPlayer) pPlayer, advancedBeehiveBlockEntity);
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        int honeyLevel = pState.getValue(BeehiveBlock.HONEY_LEVEL);
        boolean itemUsed = false;
        if (honeyLevel >= getMaxHoneyLevel()) {
            if (heldItem.getItem() == Items.SHEARS) {
                pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
                BeehiveBlock.dropHoneycomb(pLevel, pPos);
                EquipmentSlot equipmentslot = pHand.equals(InteractionHand.OFF_HAND) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                heldItem.hurtAndBreak(1, pPlayer, equipmentslot);
                itemUsed = true;
            } else if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                heldItem.shrink(1);
                pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                if (heldItem.isEmpty()) {
                    pPlayer.setItemInHand(pHand, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!pPlayer.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
                    pPlayer.drop(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                itemUsed = true;
            }
        }

        if (itemUsed) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BeehiveBlock.HONEY_LEVEL, getMaxHoneyLevel() - 5));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void openGui(ServerPlayer player, AdvancedBeehiveBlockEntity tileEntity) {
        this.updateState(tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getBlockState(), false);
        player.openMenu(tileEntity, tileEntity.getBlockPos());
    }
}
