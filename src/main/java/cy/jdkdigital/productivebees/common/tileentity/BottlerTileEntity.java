package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BottlerTileEntity extends FluidTankTileEntity implements INamedContainerProvider
{
    protected int tickCounter = 0;
    public int fluidId = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isBottleItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || item == Items.HONEYCOMB;
        }
    });

    public LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            BottlerTileEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            BottlerTileEntity.this.setChanged();
        }
    });



    @Override
    public void setChanged() {
        super.setChanged();

        if (level != null) {
            inventoryHandler.ifPresent(inv -> {
                ItemStack stack = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                boolean hasBottle = !stack.isEmpty() && stack.getItem().equals(Items.GLASS_BOTTLE);
                level.setBlock(getBlockPos(), this.getBlockState().setValue(Bottler.HAS_BOTTLE, hasBottle), 3);
            });
        }
    }

    public BottlerTileEntity() {
        super(ModTileEntityTypes.BOTTLER.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition.above());
            if (++tickCounter % 10 == 0 && state.getBlock() == Blocks.PISTON_HEAD && state.getValue(DirectionalBlock.FACING) == Direction.DOWN) {
                // Check for ProductiveBeeEntity on top of block
                List<BeeEntity> bees = level.getEntitiesOfClass(BeeEntity.class, (new AxisAlignedBB(worldPosition).expandTowards(0.0D, 1.0D, 0.0D)));
                if (!bees.isEmpty()) {
                    BeeEntity bee = bees.iterator().next();
                    inventoryHandler.ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                        if (!bottles.isEmpty() && bottles.getItem().equals(Items.GLASS_BOTTLE) && !bee.isBaby() && bee.isAlive()) {
                            ItemStack geneBottle = GeneBottle.getStack(bee);
                            Block.popResource(level, worldPosition.above(), geneBottle);
                            level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            bee.kill();
                            bottles.shrink(1);
                        }
                    });
                }
            }
        }
        super.tick();
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.BOTTLER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new BottlerContainer(windowId, playerInventory, this);
    }
}
