package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.container.CatcherContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class CatcherTileEntity extends FluidTankTileEntity implements INamedContainerProvider, ITickableTileEntity, UpgradeableTileEntity
{
    protected int tickCounter = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(11, this)
    {
        @Override
        public boolean isBottleItem(Item item) {
            return item == ModItems.BEE_CAGE.get();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public CatcherTileEntity() {
        super(ModTileEntityTypes.CATCHER.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && ++tickCounter % 69 == 0) {
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    ItemStack invItem = invHandler.getStackInSlot(0);
                    if (invItem.getItem() instanceof BeeCage && !BeeCage.isFilled(invItem)) {
                        // We have a valid inventory for catching, look for entities above
                        List<BeeEntity> bees = level.getEntitiesOfClass(BeeEntity.class, getBoundingBox());
                        int babeeUpgrades = getUpgradeCount(ModItems.UPGRADE_BREEDING.get());
                        List<ItemStack> filterUpgrades = getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
                        for (BeeEntity bee : bees) {
                            if (babeeUpgrades > 0 && !bee.isBaby()) {
                                continue;
                            }

                            boolean isAllowed = false;
                            if (filterUpgrades.size() > 0) {
                                for (ItemStack filter: filterUpgrades) {
                                    List<Supplier<BeeIngredient>> allowedBees = FilterUpgradeItem.getAllowedBees(filter);
                                    for (Supplier<BeeIngredient> allowedBee: allowedBees) {
                                        String type = BeeIngredientFactory.getIngredientKey(bee);
                                        if (allowedBee.get().getBeeType().toString().equals(type)) {
                                            isAllowed = true;
                                        }
                                    }
                                }
                            } else {
                                isAllowed = true;
                            }

                            if (isAllowed) {
                                ItemStack cageStack = new ItemStack(invItem.getItem());
                                BeeCage.captureEntity(bee, cageStack);
                                if (((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(cageStack)) {
                                    bee.remove(true);
                                    invItem.shrink(1);
                                }
                            }
                        }
                    }
                }
            });
        }
        super.tick();
    }

    private AxisAlignedBB getBoundingBox() {
        int rangeUpgrades = getUpgradeCount(ModItems.UPGRADE_RANGE.get());
        return new AxisAlignedBB(worldPosition).expandTowards(rangeUpgrades, 2.0D + rangeUpgrades, rangeUpgrades);
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
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
        return new TranslationTextComponent(ModBlocks.CATCHER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new CatcherContainer(windowId, playerInventory, this);
    }
}
