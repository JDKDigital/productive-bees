package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.init.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
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
        if (world != null && !world.isRemote) {
            if (++this.tankTick > 20) {
                this.tankTick = 0;
                tickFluidTank();
            }
        }
    }

    public void tickFluidTank() {
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (fluidStack.getAmount() >= 0) {
                this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(invHandler -> {
                    ItemStack fluidContainerItem = invHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    ItemStack existingOutput = invHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
                    if (fluidContainerItem.getCount() > 0 && (existingOutput.isEmpty() || (existingOutput.getCount() < existingOutput.getMaxStackSize()))) {
                        ItemStack outputItem = null;
                        if (fluidContainerItem.getItem() == Items.GLASS_BOTTLE && fluidStack.getAmount() >= 250 && fluidStack.getFluid().isEquivalentTo(ModFluids.HONEY.get())) {
                            outputItem = new ItemStack(Items.HONEY_BOTTLE);
                        }
                        else {
                            FluidActionResult fillResult = FluidUtil.tryFillContainer(fluidContainerItem, fluidHandler, Integer.MAX_VALUE, null, true);
                            if (fillResult.isSuccess()) {
                                outputItem = fillResult.getResult();
                            }
                        }

                        if (outputItem != null) {
                            if (invHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, outputItem, true).equals(ItemStack.EMPTY)) {
                                boolean bottleOutput = outputItem.getItem().equals(Items.HONEY_BOTTLE);
                                int drainedFluid = bottleOutput ? 250 : 0;

                                if (outputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent()) {
                                    drainedFluid = outputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(h -> h.getFluidInTank(0).getAmount()).orElse(0);
                                }
                                fluidHandler.drain(drainedFluid, IFluidHandler.FluidAction.EXECUTE);

                                // If item container is full or internal tank is empty, move the item to the output @TODO doesn't work
                                boolean doneFilling = outputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(h -> h.getTankCapacity(0) > h.getFluidInTank(0).getAmount()).orElse(true);
                                if (bottleOutput || doneFilling) {
                                    fluidContainerItem.shrink(1);
                                    invHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, outputItem, false);
                                } else {
                                    invHandler.insertItem(InventoryHandlerHelper.BOTTLE_SLOT, outputItem, false);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);

        CompoundNBT invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT fluidTag = tag.getCompound("fluid");
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> ((INBTSerializable<CompoundNBT>) fluid).deserializeNBT(fluidTag));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);

        CompoundNBT finalTag = tag;
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            finalTag.put("inv", compound);
        });

        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) fluid).serializeNBT();
            finalTag.put("fluid", compound);
        });

        return finalTag;
    }
}
