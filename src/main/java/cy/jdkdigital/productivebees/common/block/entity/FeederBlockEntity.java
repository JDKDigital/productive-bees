package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.container.FeederContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FeederBlockEntity extends CapabilityBlockEntity
{
    public Block baseBlock;
    private int tickCounter = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler;

    public FeederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FEEDER.get(), pos, state);
        refreshInventoryHandler();
    }

    public boolean isDouble() {
        return getBlockState().getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE;
    }

    public Block getRandomBlockFromInventory(TagKey<Block> tag, RandomSource random) {
        List<Block> possibleBlocks = new ArrayList<>();
        for (ItemStack stack: getInventoryItems()) {
            if (stack.getItem() instanceof BlockItem blockItem) {
                Block itemBlock = blockItem.getBlock();
                if (tag == null || itemBlock.builtInRegistryHolder().is(tag)) {
                    possibleBlocks.add(itemBlock);
                }
            }
        }
        return possibleBlocks.size() > 0 ? possibleBlocks.get(random.nextInt(possibleBlocks.size())) : Blocks.AIR;
    }

    public ItemStack getSpecificItemFromInventory(Item item, RandomSource random) {
        List<ItemStack> possibleItems = new ArrayList<>();
        for (ItemStack stack: getInventoryItems()) {
            if (stack.is(item)) {
                possibleItems.add(stack);
            }
        }
        return possibleItems.size() > 0 ? possibleItems.get(random.nextInt(possibleItems.size())) : ItemStack.EMPTY;
    }

    public List<ItemStack> getInventoryItems() {
        return inventoryHandler.map(h -> {
            List<ItemStack> items = new ArrayList<>();
            for (int slot = 0; slot < h.getSlots(); ++slot) {
                items.add(h.getStackInSlot(slot));
            }
            return items;
        }).filter(itemStacks -> !itemStacks.isEmpty()).orElse(new ArrayList<>());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public Component getName() {
        if (isDouble()) {
            return Component.translatable(ModBlocks.FEEDER.get().getDescriptionId() + "_double");
        }
        return Component.translatable(ModBlocks.FEEDER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new FeederContainer(windowId, playerInventory, this);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level instanceof ServerLevel && blockEntity instanceof FeederBlockEntity && ++((FeederBlockEntity) blockEntity).tickCounter%164 == 0) {
            if (state.getValue(Feeder.HONEYLOGGED)) {
                List<Bee> entities = level.getEntitiesOfClass(Bee.class, new AABB(pos));
                for (Bee entity : entities) {
                    if (entity != null) {
                        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, true));
                    }
                }
            }
        }
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);

        if (baseBlock != null) {
            tag.putString("baseBlock", ForgeRegistries.BLOCKS.getKey(baseBlock).toString());
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        if (tag.contains("baseBlock")) {
            baseBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("baseBlock")));
        }
    }

    public void refreshInventoryHandler() {
        this.inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(isDouble() ? 6 : 3, this)
        {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
                return true;
            }

            @Override
            public boolean isInputSlot(int slot) {
                return true;
            }

            @Override
            public boolean isInputSlotItem(int slot, ItemStack item) {
                return true;
            }

            public int[] getOutputSlots() {
                if (isDouble()) {
                    return new int[]{0, 1, 2, 3, 4, 5};
                }
                return new int[]{0, 1, 2};
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        });
    }
}
