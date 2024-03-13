package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.container.HeatedCentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;

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
        double timeUpgradeModifier = 1D + (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(1, timeUpgradeModifier) * 3;
    }

    @Override
    protected double getProcessingTimeModifier() {
        return super.getProcessingTimeModifier() / 3;
    }

    protected boolean canOperate() {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
        return energy >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    }

    @Override
    public boolean canProcessItemStack(ItemStack stack) {
        var directProcess = super.canProcessItemStack(stack);

        if (stack.is(ModTags.Forge.COMBS) && !directProcess) {
            ItemStack singleComb;
            // config honeycomb
            if (stack.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                singleComb.setTag(stack.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, stack.getItem());
            }
            return !singleComb.isEmpty() && super.canProcessItemStack(singleComb);
        }

        return directProcess;
    }

    static Map<String, CentrifugeRecipe> blockRecipeMap = new HashMap<>();
    @Override
    protected CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler) {
        if (blockRecipeMap.size() > 5000) {
            blockRecipeMap.clear();
        }
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        String cacheKey = ForgeRegistries.ITEMS.getKey(input.getItem()) + (input.getTag() != null ? input.getTag().getAsString() : "");

        var directRecipe = super.getRecipe(inputHandler);
        if (input.is(ModTags.Forge.COMBS) && directRecipe == null) {
            if (!blockRecipeMap.containsKey(cacheKey)) {
                ItemStack singleComb;
                // config honeycomb
                if (input.getItem() instanceof CombBlockItem) {
                    singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    singleComb.setTag(input.getTag());
                } else {
                    singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
                }
                IItemHandlerModifiable inv = new InventoryHandlerHelper.BlockEntityItemStackHandler(2);
                // Look up recipe for the single comb that makes up the input comb block
                inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, singleComb);
                blockRecipeMap.put(cacheKey, super.getRecipe(inv));
            }
            return blockRecipeMap.get(cacheKey);
        }
        return directRecipe;
    }

    @Override
    protected void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, RandomSource random) {
        ItemStack input = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).copy();
        if (input.is(ModTags.Forge.COMBS) && !recipe.ingredient.test(input)) {
            ItemStack singleComb;
            if (input.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), 4);
                singleComb.setTag(input.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
            }
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, singleComb);
            for (int i = 0; i < 4; i++) {
                super.completeRecipeProcessing(recipe, invHandler, random, true);
            }
            input.shrink(1);
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, input);
        } else {
            super.completeRecipeProcessing(recipe, invHandler, random, true);
        }
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.HEATED_CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new HeatedCentrifugeContainer(windowId, playerInventory, this);
    }
}
