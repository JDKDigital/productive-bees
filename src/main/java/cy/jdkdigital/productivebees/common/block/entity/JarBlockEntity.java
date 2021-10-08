package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JarBlockEntity extends AbstractBlockEntity
{
    @Nullable
    private Entity cachedEntity;

    public int tickCount = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(1, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem().asItem() instanceof BeeCage && BeeCage.isFilled(stack);
        }

        @Override
        public boolean isContainerItem(Item item) {
            return item instanceof BeeCage;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (tileEntity.hasLevel()) {
                tileEntity.getLevel().sendBlockUpdated(tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity.getBlockState(), Constants.BlockFlags.DEFAULT);
            }
        }
    });

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.JAR.get(), pos, state);
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
    public Entity getCachedEntity(ItemStack cage) {
        if (this.cachedEntity == null) {
            this.cachedEntity = BeeCage.getEntityFromStack(cage, this.getLevel(), false);
        }

        return this.cachedEntity;
    }

    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        CompoundTag finalTag = tag;
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            finalTag.put("inv", compound);
        });
    }

    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);
        CompoundTag invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(invTag));

        tickCount = ProductiveBees.rand.nextInt(360);
    }
}
