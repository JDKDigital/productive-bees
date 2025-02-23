package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JarBlockEntity extends CapabilityBlockEntity
{
    @Nullable
    private Entity cachedEntity;

    public int tickCount = 0;

    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(1, this)
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
            if (blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
        }
    };

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.JAR.get(), pos, state);
    }

    @Nullable
    public Entity getCachedEntity(ItemStack cage) {
        if (this.cachedEntity == null) {
            this.cachedEntity = BeeCage.getEntityFromStack(cage, this.getLevel(), false);
        }

        return this.cachedEntity;
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        tickCount = ProductiveBees.random.nextInt(360);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
        componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        if (!items.isEmpty() && !items.getFirst().isEmpty()) {
            this.getItemHandler().insertItem(0, items.getFirst(), false);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (inventoryHandler instanceof ItemStackHandler serializable && level != null) {
            components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.getItemHandler().getStackInSlot(0))));
        };
    }
}
