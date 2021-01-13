package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JarTileEntity extends TileEntity
{
    @Nullable
    private Entity cachedEntity;

    public int ticksExisted = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(1, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem().getItem() instanceof BeeCage && BeeCage.isFilled(stack);
        }

        @Override
        public boolean isBottleItem(Item item) {
            return item instanceof BeeCage;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (tileEntity.hasWorld()) {
                tileEntity.getWorld().notifyBlockUpdate(tileEntity.getPos(), tileEntity.getBlockState(), tileEntity.getBlockState(), Constants.BlockFlags.DEFAULT);
            }
        }
    });

    public JarTileEntity() {
        super(ModTileEntityTypes.JAR.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        deserializeNBT(tag);
    }

    @Nullable
    public Entity getCachedEntity(ItemStack cage) {
        if (this.cachedEntity == null) {
            this.cachedEntity = BeeCage.getEntityFromStack(cage, this.getWorld(), true);
        }

        return this.cachedEntity;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        ticksExisted = ProductiveBees.rand.nextInt(360);
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

        return tag;
    }
}
