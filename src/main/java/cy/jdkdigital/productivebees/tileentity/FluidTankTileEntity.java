package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public abstract class FluidTankTileEntity extends TileEntity implements ITickableTileEntity
{
    private int tankTick = 0;

    public FluidTankTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (++this.tankTick > 20) {
                this.tankTick = 0;
                tickFluidTank();
            }
        }
    }

    public void tickFluidTank() {
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(honeyHandler -> {
            FluidStack honeyFluid = honeyHandler.getFluidInTank(0);
            if (honeyFluid.getAmount() >= 250) {
                this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(invHandler -> {
                    ItemStack honeyContainerItem = invHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    int drainedHoney = 0;
                    ItemStack outputItem = null;
                    if (honeyContainerItem.getItem() == Items.GLASS_BOTTLE) {
                        drainedHoney = 250;
                        outputItem = new ItemStack(Items.HONEY_BOTTLE);
                    }
                    else if (honeyContainerItem.getItem() == Items.BUCKET && honeyFluid.getAmount() >= 1000) {
                        drainedHoney = 1000;
                        outputItem = new ItemStack(ModItems.HONEY_BUCKET.get());
                    }

                    if (drainedHoney > 0 && honeyContainerItem.getCount() > 0) {
                        ItemStack existingOutput = invHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
                        if (existingOutput.isEmpty() || (existingOutput.getItem() == outputItem.getItem() && existingOutput.getCount() < outputItem.getMaxStackSize())) {
                            honeyContainerItem.shrink(1);
                            honeyHandler.drain(drainedHoney, IFluidHandler.FluidAction.EXECUTE);
                            invHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, outputItem, false);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT tag) {
        super.func_230337_a_(state, tag);

        CompoundNBT invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT fluidTag = tag.getCompound("fluid");
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> ((INBTSerializable<CompoundNBT>) fluid).deserializeNBT(fluidTag));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) fluid).serializeNBT();
            tag.put("fluid", compound);
        });

        return tag;
    }
}
