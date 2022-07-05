package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.container.CatcherContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class CatcherBlockEntity extends FluidTankBlockEntity implements UpgradeableBlockEntity
{
    protected int tickCounter = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(11, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item == ModItems.BEE_CAGE.get();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public CatcherBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.CATCHER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CatcherBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % 69 == 0) {
            blockEntity.inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    ItemStack invItem = invHandler.getStackInSlot(0);
                    if (invItem.getItem() instanceof BeeCage && !BeeCage.isFilled(invItem)) {
                        // We have a valid inventory for catching, look for entities above
                        List<Bee> bees = level.getEntitiesOfClass(Bee.class, blockEntity.getBoundingBox());
                        int babeeUpgrades = blockEntity.getUpgradeCount(ModItems.UPGRADE_BREEDING.get());
                        List<ItemStack> filterUpgrades = blockEntity.getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
                        for (Bee bee : bees) {
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
                                    bee.discard();
                                    invItem.shrink(1);
                                }
                            }
                        }
                    }
                }
            });
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    private AABB getBoundingBox() {
        int rangeUpgrades = getUpgradeCount(ModItems.UPGRADE_RANGE.get());
        return new AABB(worldPosition).inflate(rangeUpgrades, 2.0D + rangeUpgrades, rangeUpgrades);
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
    public Component getName() {
        return new TranslatableComponent(ModBlocks.CATCHER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new CatcherContainer(windowId, playerInventory, this);
    }
}
