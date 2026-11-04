package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.BeeSquisher;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.container.BeeSquisherContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeeSquisherBlockEntity extends CapabilityBlockEntity implements MenuProvider, IUpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    public int recipeProgress = 0;
    public boolean isRunning = false;
    private long lastSoundTime = -1;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(4, this)
    {
        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return
                (slot == BeeSquisherContainer.SLOT_CAGE && item.getItem() instanceof BeeCage && BeeCage.isFilled(item)) ||
                (slot == BeeSquisherContainer.SLOT_BOTTLE && item.is(Items.GLASS_BOTTLE));
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{BeeSquisherContainer.SLOT_OUTPUT, BeeSquisherContainer.SLOT_CAGE_OUTPUT};
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get(),
            LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(),
            LibItems.UPGRADE_PRODUCTIVITY_3.get(),
            LibItems.UPGRADE_PRODUCTIVITY_4.get(),
            LibItems.UPGRADE_STABILITY.get()
    ));

    public SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(10000);

    public BeeSquisherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BEE_SQUISHER.get(), pos, state);
    }

    private void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public RecipeHolder<? extends TimedRecipeInterface> getCurrentRecipe() {
        return null;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe) {
        return Math.max((int) (ProductiveBeesConfig.GENERAL.beeSquisherProcessingTime.get() * getProcessingTimeModifier()), 5);
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier);
    }

    protected double getEnergyConsumptionModifier() {
        int steps = getUpgradeCount(LibItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get()) + getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_2.get()) * 2 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_3.get()) * 3 + getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get()) * 4;

        return Math.max(1, 1 + ProductiveBeesConfig.GENERAL.beeSquisherPowerPerUpgrade.get() * steps);
    }

    public int getPowerUse() {
        return (int) (ProductiveBeesConfig.GENERAL.beeSquisherPowerUse.get() * getEnergyConsumptionModifier());
    }

    public int getPurityBonus() {
        return getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get()) * 4 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_2.get()) * 8 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_3.get()) * 16 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get()) * 32 -
                ProductiveBeesConfig.GENERAL.beeSquisherPurityPenalty.get();
    }

    public boolean isTypeOnly() {
        return getUpgradeCount(LibItems.UPGRADE_STABILITY.get()) > 0;
    }

    public int getTypePurityBonus() {
        return getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get()) * 4 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_2.get()) * 8 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_3.get()) * 16 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get()) * 32 +
                getUpgradeCount(LibItems.UPGRADE_STABILITY.get()) * ProductiveBeesConfig.GENERAL.beeSquisherStabilityPurity.get();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BeeSquisherBlockEntity blockEntity) {
        if (blockEntity.isRunning && level instanceof ServerLevel) {
            try (Transaction tx = Transaction.openRoot()) {
                blockEntity.energyHandler.extract(blockEntity.getPowerUse(), tx);
                tx.commit();
            }
        }
        boolean running = false;
        if (!blockEntity.inventoryHandler.getStackInSlot(BeeSquisherContainer.SLOT_CAGE).isEmpty()) {
            if (blockEntity.isRunning || blockEntity.canProcessInput(blockEntity.inventoryHandler)) {
                running = true;
                blockEntity.setRunning(true);
                int totalTime = blockEntity.getProcessingTime(null);

                if (blockEntity.recipeProgress >= totalTime && blockEntity.completeExtraction(blockEntity.inventoryHandler)) {
                    blockEntity.recipeProgress = 0;
                    blockEntity.playSquishSound(level, pos);
                    blockEntity.setChanged();
                } else {
                    blockEntity.recipeProgress = Math.min(totalTime, blockEntity.recipeProgress + 1);
                }
            }
        } else {
            blockEntity.recipeProgress = 0;
            blockEntity.setRunning(false);
        }

        BlockState currentState = level.getBlockState(pos);
        if (currentState.hasProperty(BeeSquisher.RUNNING) && currentState.getValue(BeeSquisher.RUNNING) != running) {
            level.setBlockAndUpdate(pos, currentState.setValue(BeeSquisher.RUNNING, running));
        }
    }

    private void playSquishSound(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        long now = level.getGameTime();
        if (lastSoundTime >= 0 && now - lastSoundTime < ProductiveBeesConfig.GENERAL.beeSquisherSoundInterval.get()) {
            return;
        }
        lastSoundTime = now;
        serverLevel.playSound(null, pos, SoundEvents.BEE_DEATH, SoundSource.BLOCKS, 0.8F, 1.0F);
    }

    private boolean canProcessInput(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        ItemStack cageItem = invHandler.getStackInSlot(BeeSquisherContainer.SLOT_CAGE);
        ItemStack bottleItem = invHandler.getStackInSlot(BeeSquisherContainer.SLOT_BOTTLE);

        return energyHandler.getAmountAsInt() >= getPowerUse()
                && cageItem.getItem() instanceof BeeCage && BeeCage.isFilled(cageItem)
                && bottleItem.is(Items.GLASS_BOTTLE)
                && invHandler.canFitStacks(resultTemplates(cageItem));
    }

    private List<ItemStack> resultTemplates(ItemStack cageItem) {
        List<ItemStack> results = new ArrayList<>();
        results.add(new ItemStack(ModItems.GENE_BOTTLE.get()));
        if (cageItem.getItem().equals(ModItems.STURDY_BEE_CAGE.get())) {
            results.add(new ItemStack(ModItems.STURDY_BEE_CAGE.get()));
        }
        return results;
    }

    private boolean completeExtraction(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        if (!canProcessInput(invHandler)) {
            return false;
        }

        ItemStack cageItem = invHandler.getStackInSlot(BeeSquisherContainer.SLOT_CAGE);
        if (!(BeeCage.getEntityFromStack(cageItem, level, true) instanceof Bee bee)) {
            return false;
        }

        ItemStack geneBottle = new ItemStack(ModItems.GENE_BOTTLE.get());
        GeneBottle.setGenes(geneBottle, bee, getPurityBonus(), isTypeOnly(), level.getRandom().nextInt(5) + 2, getTypePurityBonus());

        List<ItemStack> results = new ArrayList<>();
        results.add(geneBottle);
        if (cageItem.getItem().equals(ModItems.STURDY_BEE_CAGE.get())) {
            results.add(new ItemStack(ModItems.STURDY_BEE_CAGE.get()));
        }
        if (!invHandler.canFitStacks(results)) {
            return false;
        }

        for (ItemStack result : results) {
            invHandler.addOutput(result);
        }
        invHandler.extractItem(BeeSquisherContainer.SLOT_CAGE, 1, false, false);
        invHandler.extractItem(BeeSquisherContainer.SLOT_BOTTLE, 1, false, false);
        return true;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        setRunning(false);
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        recipeProgress = input.getIntOr("RecipeProgress", 0);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);

        output.putInt("RecipeProgress", recipeProgress);
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.BEE_SQUISHER.get().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BeeSquisherContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return energyHandler;
    }
}
