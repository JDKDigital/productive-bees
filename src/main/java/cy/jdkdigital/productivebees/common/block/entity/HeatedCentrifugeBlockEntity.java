package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.container.HeatedCentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class HeatedCentrifugeBlockEntity extends PoweredCentrifugeBlockEntity
{
    public HeatedCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HEATED_CENTRIFUGE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HeatedCentrifugeBlockEntity blockEntity) {
        PoweredCentrifugeBlockEntity.tick(level, pos, state, blockEntity);
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = 1D + (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(1, timeUpgradeModifier) * 3;
    }

    @Override
    protected double getProcessingTimeModifier() {
        return super.getProcessingTimeModifier() / 3;
    }

    protected boolean canOperate() {
        int energy = energyHandler.getAmountAsInt();
        return energy >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    }

    @Override
    public boolean canProcessItemStack(ItemStack stack) {
        if (super.canProcessItemStack(stack)) {
            return true;
        }

        if (stack.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
            ItemStack singleComb = BeeHelper.getSingleComb(stack);
            return !singleComb.isEmpty() && super.canProcessItemStack(singleComb);
        }

        return false;
    }

    static Map<String, RecipeHolder<CentrifugeRecipe>> blockRecipeMap = new HashMap<>();
    @Override
    protected RecipeHolder<CentrifugeRecipe> getRecipe(InventoryHandlerHelper.BlockEntityItemStackHandler inputHandler) {
        if (blockRecipeMap.size() > 5000) {
            blockRecipeMap.clear();
        }

        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        if (input.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
            var directRecipe = super.getRecipe(inputHandler);
            if (directRecipe != null) {
                return directRecipe;
            }
        }

        String cacheKey = BeeHelper.itemCacheKey(input);
        if (!blockRecipeMap.containsKey(cacheKey)) {
            ItemStack singleComb = BeeHelper.getSingleComb(input);
            if (singleComb.isEmpty()) {
                singleComb = input;
            }
            var inv = new InventoryHandlerHelper.BlockEntityItemStackHandler(2);
            // Look up recipe for the single comb that makes up the input comb block
            inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, singleComb);
            blockRecipeMap.put(cacheKey, super.getRecipe(inv));
        }
        return blockRecipeMap.get(cacheKey);
    }

    @Override
    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, RandomSource random) {
        ItemStack input = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).copy();
        int productivityModifier = Math.min(input.getCount(), Math.min(64, getProductivityModifier()));
        if (input.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS) && !recipe.value().ingredient.test(input)) {
            ItemStack singleComb = BeeHelper.getSingleComb(input);
            singleComb.setCount(productivityModifier);
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, singleComb);
            for (int i = 0; i < 4; i++) {
                super.completeRecipeProcessing(recipe, invHandler, random, true, productivityModifier);
            }
            input.shrink(productivityModifier);
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, input);
        } else {
            super.completeRecipeProcessing(recipe, invHandler, random, true, productivityModifier);
        }
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.HEATED_CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new HeatedCentrifugeContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return energyHandler;
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
    }
}
