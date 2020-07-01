package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class AdvancedBeehive extends AdvancedBeehiveAbstract
{
    public static final BooleanProperty EXPANDED = BooleanProperty.create("expanded");

    public AdvancedBeehive(final Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(EXPANDED, false));
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(EXPANDED);
    }

    public void updateState(World world, BlockPos pos, BlockState state, boolean isRemoved) {
        if (this instanceof DragonEggHive) {
            return;
        }
        BlockPos posUp = pos.up();
        BlockState blockStateAbove = world.getBlockState(posUp);
        Block blockAbove = blockStateAbove.getBlock();

        if (!isRemoved) {
            // Set this block to expanded if there's an expansion box on top and the block has not been removed
            world.setBlockState(pos, state.with(EXPANDED, blockAbove instanceof ExpansionBox), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            ((AdvancedBeehiveTileEntity) world.getTileEntity(pos)).MAX_BEES = world.getBlockState(pos).get(EXPANDED) ? 5 : 3;
        }
        if (blockAbove instanceof ExpansionBox) {
            // Set this block to expanded if there's an advanced beehive below and the block has not been removed
            world.setBlockState(posUp, blockStateAbove
                    .with(AdvancedBeehive.EXPANDED, !isRemoved)
                    // Fix expansion box direction to match the beehive
                    .with(BlockStateProperties.FACING, state.get(BlockStateProperties.FACING))
                    // Set honey state based on the beehive below
                    .with(ExpansionBox.HAS_HONEY, !isRemoved && state.get(BeehiveBlock.HONEY_LEVEL) >= getMaxHoneyLevel()), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
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
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
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
            }
        }
        super.onReplaced(oldState, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        int honeyLevel = state.get(BeehiveBlock.HONEY_LEVEL);
        boolean itemUsed = false;
        if (honeyLevel >= getMaxHoneyLevel()) {
            if (heldItem.getItem() == Items.SHEARS) {
                world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                BeehiveBlock.dropHoneyComb(world, pos);
                heldItem.damageItem(1, player, (entity) -> {
                    entity.sendBreakAnimation(hand);
                });
                itemUsed = true;
            }
            else if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                heldItem.shrink(1);
                world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (heldItem.isEmpty()) {
                    player.setHeldItem(hand, new ItemStack(Items.HONEY_BOTTLE));
                }
                else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE))) {
                    player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                itemUsed = true;
            }
        }

        if (itemUsed) {
            this.takeHoney(world, state, pos);
        }
        else if (!world.isRemote()) {
            final TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntity) {
                this.updateState(world, pos, state, false);
                openGui((ServerPlayerEntity) player, (AdvancedBeehiveTileEntity) tileEntity);
            }
        }
        return ActionResultType.SUCCESS;
    }

    public void openGui(ServerPlayerEntity player, AdvancedBeehiveTileEntity tileEntity) {
        tileEntity.inhabitantList.clear();
        tileEntity.getCapability(CapabilityBee.BEE).ifPresent(handler -> {
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant bee : handler.getInhabitants()) {
                tileEntity.inhabitantList.add(bee.nbt.getString("id"));
            }
        });
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> {
            packetBuffer.writeBlockPos(tileEntity.getPos());
            packetBuffer.writeString(String.join(",", tileEntity.inhabitantList));
        });
    }
}
