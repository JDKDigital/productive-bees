package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.container.FeederContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FeederTileEntity extends CapabilityTileEntity implements INamedContainerProvider, ITickableTileEntity
{
    private int tickCounter = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(3, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            return true;
        }
    });

    public FeederTileEntity() {
        super(ModTileEntityTypes.FEEDER.get());
    }

    public Block getRandomBlockFromInventory(ITag<Block> tag) {
        return inventoryHandler.map(h -> {
            List<Block> possibleBlocks = new ArrayList<>();
            for (int slot = 0; slot < h.getSlots(); ++slot) {
                Item slotItem = h.getStackInSlot(slot).getItem();
                if (slotItem instanceof BlockItem) {
                    Block itemBlock = ((BlockItem) slotItem).getBlock();
                    if (itemBlock.isIn(tag)) {
                        possibleBlocks.add(itemBlock);
                    }
                }
            }

            return possibleBlocks.get(ProductiveBees.rand.nextInt(possibleBlocks.size()));
        }).orElse(Blocks.AIR);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.FEEDER.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new FeederContainer(windowId, playerInventory, this);
    }

    @Override
    public void tick() {
        if (world instanceof ServerWorld && ++tickCounter%164 == 0) {
            BlockState state = world.getBlockState(pos);
            if (state.get(Feeder.HONEYLOGGED)) {
                List<Entity> entities = world.getLoadedEntitiesWithinAABB(BeeEntity.class, new AxisAlignedBB(pos));
                for (Entity entity : entities) {
                    if (entity instanceof BeeEntity) {
                        ((BeeEntity) entity).addPotionEffect(new EffectInstance(Effects.REGENERATION, 80, 0, false, true));
                    }
                }
            }
        }
    }
}
