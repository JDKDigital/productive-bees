package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class AdvancedBeehive extends AdvancedBeehiveAbstract
{
    public static final EnumProperty<VerticalHive> EXPANDED = EnumProperty.create("expanded", VerticalHive.class);

    public AdvancedBeehive(final Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState()
            .with(BeehiveBlock.FACING, Direction.NORTH)
            .with(EXPANDED, VerticalHive.NONE)
            .with(BeehiveBlock.HONEY_LEVEL, 0)
        );
    }

    @Override
    public boolean hasTileEntity(final BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return ModTileEntityTypes.ADVANCED_BEEHIVE.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new AdvancedBeehiveTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BeehiveBlock.FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(EXPANDED, BeehiveBlock.HONEY_LEVEL, BeehiveBlock.FACING);
    }

    public void updateState(World world, BlockPos pos, BlockState state, boolean isRemoved) {
        if (this instanceof DragonEggHive) {
            return;
        }

        Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentBox(world, pos);
        if (pair != null) {
            Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
            BlockPos boxPos = posAndDirection.getLeft();

            VerticalHive directionProperty = AdvancedBeehive.calculateExpandedDirection(world, pos, isRemoved);

            if (!isRemoved) {
                updateStateWithDirection(world, pos, state, directionProperty);
            }
            ((ExpansionBox) pair.getRight().getBlock()).updateStateWithDirection(world, boxPos, pair.getRight(), directionProperty);
        }
    }

    public void updateStateWithDirection(World world, BlockPos pos, BlockState state, VerticalHive directionProperty) {
        world.setBlockState(pos, state.with(AdvancedBeehive.EXPANDED, directionProperty), Constants.BlockFlags.DEFAULT);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof AdvancedBeehiveTileEntity) {
            ((AdvancedBeehiveTileEntity) te).MAX_BEES = world.getBlockState(pos).get(EXPANDED) != VerticalHive.NONE ? 5 : 3;
            if (directionProperty.equals(VerticalHive.NONE)) {
                ((AdvancedBeehiveTileEntity) te).upgradeHandler.ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
            }
        }
    }

    public static Pair<Pair<BlockPos, Direction>, BlockState> getAdjacentBox(World world, BlockPos pos) {
        for(Direction direction: BlockStateProperties.FACING.getAllowedValues()) {
            if (direction == Direction.DOWN) {
                continue;
            }
            BlockPos newPos = pos.offset(direction);
            BlockState blockStateAtPos = world.getBlockState(newPos);

            Block blockAtPos = blockStateAtPos.getBlock();
            if (blockAtPos instanceof ExpansionBox) {
                return Pair.of(Pair.of(newPos, direction), blockStateAtPos);
            }
        }
        return null;
    }

    public static VerticalHive calculateExpandedDirection(World world, BlockPos pos, boolean isRemoved) {
        Pair<Pair<BlockPos, Direction>, BlockState> pair = getAdjacentBox(world, pos);

        VerticalHive directionProperty = VerticalHive.NONE;
        if (!isRemoved && pair != null) {
            BlockState state = world.getBlockState(pos);
            Direction hiveDirection = state.get(BeehiveBlock.FACING);
            Direction boxDirection = pair.getLeft().getRight();

            Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
            BlockPos boxPos = posAndDirection.getLeft();

            boolean isValidExpandedPos = pos.getY() != boxPos.getY();
            if (!isValidExpandedPos && (hiveDirection == Direction.NORTH || hiveDirection == Direction.SOUTH)) {
                isValidExpandedPos = boxDirection == Direction.WEST || boxDirection == Direction.EAST;
            }
            else if (!isValidExpandedPos && (hiveDirection == Direction.WEST || hiveDirection == Direction.EAST)) {
                isValidExpandedPos = boxDirection == Direction.SOUTH || boxDirection == Direction.NORTH;
            }

            if (isValidExpandedPos) {
                directionProperty =
                    pos.getY() != boxPos.getY() ? VerticalHive.UP : (
                    pos.getX() < boxPos.getX() || pos.getZ() < boxPos.getZ() ? hiveDirection == Direction.NORTH || hiveDirection == Direction.EAST ? VerticalHive.LEFT : VerticalHive.RIGHT : (
                    pos.getX() > boxPos.getX() || pos.getZ() > boxPos.getZ() ? hiveDirection == Direction.NORTH || hiveDirection == Direction.EAST ? VerticalHive.RIGHT : VerticalHive.LEFT : (
                VerticalHive.NONE)));
            }
        }
        return directionProperty;
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

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState oldState, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntity) {
                // Drop inventory
                tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
                ((AdvancedBeehiveTileEntity) tileEntity).upgradeHandler.ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
            }
        }
        super.onReplaced(oldState, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(handIn);
        int honeyLevel = state.get(BeehiveBlock.HONEY_LEVEL);
        boolean itemUsed = false;
        if (honeyLevel >= getMaxHoneyLevel()) {
            if (heldItem.getItem() == Items.SHEARS) {
                world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                BeehiveBlock.dropHoneyComb(world, pos);
                heldItem.damageItem(1, player, (entity) -> {
                    entity.sendBreakAnimation(handIn);
                });
                itemUsed = true;
            }
            else if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                heldItem.shrink(1);
                world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (heldItem.isEmpty()) {
                    player.setHeldItem(handIn, new ItemStack(Items.HONEY_BOTTLE));
                }
                else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE))) {
                    player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                itemUsed = true;
            }
        }

        if (itemUsed) {
            this.takeHoney(world, state, pos);
            if (player instanceof ServerPlayerEntity) {
                CriteriaTriggers.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity) player, pos, heldItem.copy());
            }
        }
        else if (!world.isRemote()) {
            final TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntity) {
                this.updateState(world, pos, state, false);
                world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.DEFAULT);
                openGui((ServerPlayerEntity) player, (AdvancedBeehiveTileEntity) tileEntity);
            }
        }
        return ActionResultType.SUCCESS;
    }

    public void openGui(ServerPlayerEntity player, AdvancedBeehiveTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> {
            packetBuffer.writeBlockPos(tileEntity.getPos());
        });
    }
}
