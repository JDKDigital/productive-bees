package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BottlerBlockEntity extends FluidTankBlockEntity
{
    protected int tickCounter = 0;
    public int fluidId = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || item == Items.HONEYCOMB;
        }
    });

    public LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            BottlerBlockEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            BottlerBlockEntity.this.updateBottleState();
        }
    });

    private void updateBottleState() {
        if (level != null) {
            inventoryHandler.ifPresent(inv -> {
                ItemStack stack = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                boolean hasBottle = !stack.isEmpty() && stack.getItem().equals(Items.GLASS_BOTTLE);
                level.setBlock(getBlockPos(), this.getBlockState().setValue(Bottler.HAS_BOTTLE, hasBottle), 3);
            });
        }
    }

    public BottlerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOTTLER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BottlerBlockEntity blockEntity) {
        BlockState aboveState = level.getBlockState(pos.above());
        if (++blockEntity.tickCounter % 7 == 0 && aboveState.getBlock() == Blocks.PISTON_HEAD && aboveState.getValue(DirectionalBlock.FACING) == Direction.DOWN) {
            // Check for ProductiveBeeEntity on top of block
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(pos).expandTowards(0.0D, 1.0D, 0.0D)));
            if (!bees.isEmpty()) {
                Bee bee = bees.iterator().next();
                blockEntity.inventoryHandler.ifPresent(inv -> {
                    ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    if (!bottles.isEmpty() && bottles.getItem().equals(Items.GLASS_BOTTLE) && !bee.isBaby() && bee.isAlive()) {
                        ItemStack geneBottle = GeneBottle.getStack(bee);
                        Block.popResource(level, pos.above(), geneBottle);
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        bee.kill();
                        bottles.shrink(1);
                    }
                });
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = Registry.FLUID.getId(fluid);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.BOTTLER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new BottlerContainer(windowId, playerInventory, this);
    }
}
