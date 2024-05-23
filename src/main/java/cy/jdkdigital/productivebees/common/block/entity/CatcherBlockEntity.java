package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.container.CatcherContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class CatcherBlockEntity extends CapabilityBlockEntity implements UpgradeableBlockEntity, MenuProvider
{
    protected int tickCounter = 0;

    public IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(11, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item instanceof BeeCage;
        }
    };

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this);

    public CatcherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CATCHER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CatcherBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % 69 == 0) {
            if (!blockEntity.inventoryHandler.getStackInSlot(0).isEmpty()) {
                ItemStack invItem = blockEntity.inventoryHandler.getStackInSlot(0);
                if (invItem.getItem() instanceof BeeCage && !BeeCage.isFilled(invItem)) {
                    // We have a valid inventory for catching, look for entities above
                    List<Bee> bees = level.getEntitiesOfClass(Bee.class, blockEntity.getBoundingBox());
                    int babeeUpgrades = blockEntity.getUpgradeCount(ModItems.UPGRADE_BREEDING.get());
                    int notBabeeUpgrades = blockEntity.getUpgradeCount(ModItems.UPGRADE_NOT_BABEE.get());
                    List<ItemStack> filterUpgrades = blockEntity.getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
                    for (Bee bee : bees) {
                        if (babeeUpgrades > 0 && !bee.isBaby()) {
                            continue;
                        }
                        if (notBabeeUpgrades > 0 && bee.isBaby()) {
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

                        if (isAllowed && invItem.getCount() > 0) {
                            bee.setSavedFlowerPos(null);
                            bee.hivePos = null;
                            ItemStack cageStack = new ItemStack(invItem.getItem());
                            BeeCage.captureEntity(bee, cageStack);
                            if (((InventoryHandlerHelper.BlockEntityItemStackHandler) blockEntity.inventoryHandler).addOutput(cageStack).getCount() == 0) {
                                bee.discard();
                                invItem.shrink(1);
                            }
                        }
                    }
                }
            }
        }
    }

    private AABB getBoundingBox() {
        int rangeUpgrades = getUpgradeCount(ModItems.UPGRADE_RANGE.get());
        return new AABB(worldPosition).inflate(rangeUpgrades, 2.0D + rangeUpgrades, rangeUpgrades);
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.CATCHER.get().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new CatcherContainer(windowId, playerInventory, this);
    }
}
