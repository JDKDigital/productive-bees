package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class JarBlockEntity extends AbstractBlockEntity
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
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        if (inventoryHandler instanceof ItemStackHandler serializable) {
            tag.put("inv", serializable.serializeNBT(provider));
        };
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        CompoundTag invTag = tag.getCompound("inv");
        if (inventoryHandler instanceof ItemStackHandler serializable) {
            serializable.deserializeNBT(provider, invTag);
        }

        tickCount = ProductiveBees.random.nextInt(360);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        var d = pComponentInput.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (level != null) {
            loadPacketNBT(d, level.registryAccess());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        if (inventoryHandler instanceof ItemStackHandler serializable && level != null) {
            pComponents.set(DataComponents.CUSTOM_DATA, CustomData.of(serializable.serializeNBT(level.registryAccess())));
        };
    }

}
