package cy.jdkdigital.productivebees.common.block.entity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import net.minecraft.world.phys.AABB;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivelib.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivelib.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CentrifugeBlockEntity extends FluidTankBlockEntity implements MenuProvider, IUpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    private RecipeHolder<? extends CentrifugeRecipe> currentRecipe = null;
    public int recipeProgress = 0;
    public int fluidId = 0;
    public int transferCooldown = -1;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(11, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack, boolean fromAutomation) {
            if (slot == InventoryHandlerHelper.BOTTLE_SLOT) return false;

            return super.isItemValid(slot, stack, fromAutomation);
        }

        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            if (fromAutomation) {
                // Skip lookup if the item is different
                ItemStack existing = this.stacks.get(slot);
                if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing)) {
                    return stack;
                }
            }
            return super.insertItem(slot, stack, simulate, fromAutomation);
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            var currentStack = getStackInSlot(slot);

            if (currentStack.getCount() == currentStack.getMaxStackSize()) {
                return false;
            }

            boolean isProcessableItem =
                    ItemStack.isSameItemSameComponents(currentStack, item) ||
                    item.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                    item.getItem().equals(ModItems.HONEY_TREAT.get()) ||
                    CentrifugeBlockEntity.this.canProcessItemStack(item);

            return (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT) || (!isProcessableItem && !super.isInputSlot(slot));
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack previousContents) {
            super.onContentsChanged(slot, previousContents);
            if (CentrifugeBlockEntity.this.level instanceof ServerLevel serverLevel && slot == InventoryHandlerHelper.INPUT_SLOT && this.getStackInSlot(slot).isEmpty()) {
                CentrifugeBlockEntity.this.recipeProgress = 0;
                serverLevel.setBlockAndUpdate(CentrifugeBlockEntity.this.getBlockPos(), CentrifugeBlockEntity.this.getBlockState().setValue(Centrifuge.RUNNING, false));
            }
        }
    };

    public FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, 10000)
    {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            super.onContentsChanged(index, previousContents);
            CentrifugeBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getResource(index).getFluid());
            CentrifugeBlockEntity.this.setChanged();
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get(),
            LibItems.UPGRADE_ENTITY_FILTER.get(),
            LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(),
            LibItems.UPGRADE_PRODUCTIVITY_3.get(),
            LibItems.UPGRADE_PRODUCTIVITY_4.get(),
            LibItems.UPGRADE_STABILITY.get()
    ));

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE.get(), pos, state);
    }

    public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public RecipeHolder<? extends TimedRecipeInterface> getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe) {
        return Math.max((int) (
            (recipe != null ? recipe.value().getProcessingTime() : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get()) * getProcessingTimeModifier()
        ), 5);
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity blockEntity) {
        if (blockEntity.inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler itemStackHandler) {
            ItemStack invItem = itemStackHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
            if (!invItem.isEmpty() && blockEntity.canOperate()) {
                // Process
                if (state.getValue(Centrifuge.RUNNING) && --blockEntity.recipeProgress <= 0) {
                    // Progress and complete
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        blockEntity.completeGeneProcessing(itemStackHandler, level.getRandom());
                    } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                        blockEntity.completeTreatProcessing(itemStackHandler);
                    } else if (!invItem.isEmpty()) {
                        RecipeHolder<CentrifugeRecipe> recipe = blockEntity.getRecipe(itemStackHandler);
                        if (blockEntity.canProcessRecipe(recipe, itemStackHandler)) {
                            blockEntity.completeRecipeProcessing(recipe, itemStackHandler, level.getRandom());
                        }
                    }
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, false));
                    blockEntity.setChanged();
                }

                if (!state.getValue(Centrifuge.RUNNING)) {
                    // Start
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        blockEntity.recipeProgress = blockEntity.getProcessingTime(null);
                    } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                        blockEntity.recipeProgress = blockEntity.getProcessingTime(null);
                    } else if (!invItem.isEmpty()) {
                        RecipeHolder<CentrifugeRecipe> recipe = blockEntity.getRecipe(itemStackHandler);
                        blockEntity.recipeProgress = blockEntity.getProcessingTime(recipe);
                    }
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    blockEntity.setChanged();
                }
            } else {
                level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, false));
            }

            // Pull items dropped on top
            if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                blockEntity.transferCooldown = 22;
                blockEntity.suckInItems(itemStackHandler);
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel) || getUpgradeCount(LibItems.UPGRADE_STABILITY.get()) > 0) {
            return;
        }
        FluidResource resource = fluidHandler.getResource(0);
        long available = fluidHandler.getAmountAsLong(0);
        if (resource.isEmpty() || available <= 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            if (available <= 0) {
                break;
            }
            ResourceHandler<FluidResource> neighbour = serverLevel.getCapability(Capabilities.Fluid.BLOCK, pos.relative(direction.getOpposite()), direction);
            if (neighbour == null) {
                continue;
            }
            try (Transaction tx = Transaction.openRoot()) {
                int sendable = (int) Math.min(available, Integer.MAX_VALUE);
                int inserted = neighbour.insert(resource, sendable, tx);
                if (inserted > 0) {
                    int extracted = fluidHandler.extract(0, resource, inserted, tx);
                    if (extracted > 0) {
                        tx.commit();
                        available -= extracted;
                    }
                }
            }
        }
    }

    private void suckInItems(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        for (ItemEntity itemEntity : getCaptureItems()) {
            ItemStack itemStack = itemEntity.getItem();
            if (
                    !itemEntity.isRemoved() && (
                        canProcessItemStack(itemStack) ||
                        itemStack.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                        itemStack.getItem().equals(ModItems.HONEY_TREAT.get()) && HoneyTreat.hasGene(itemStack)
                    )
            ) {
                captureItem(invHandler, itemEntity);
            }
        }
    }

    private List<ItemEntity> getCaptureItems() {
        assert level != null;
        List<AABB> boxes = Centrifuge.COLLECTION_AREA_SHAPE.toAabbs();
        List<ItemEntity> result = new ArrayList<>();
        for (AABB box : boxes) {
            result.addAll(level.getEntitiesOfClass(ItemEntity.class, box.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE));
        }
        return result;
    }

    private static void captureItem(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, ItemEntity itemEntity) {
        ItemStack leftoverStack = invHandler.insertItem(InventoryHandlerHelper.INPUT_SLOT, itemEntity.getItem(), false);
        if (leftoverStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(leftoverStack);
        }
    }

    protected boolean canOperate() {
        return true;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    public boolean canProcessItemStack(ItemStack stack) {
        var inv = new InventoryHandlerHelper.BlockEntityItemStackHandler(2, null);
        inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, stack);

        boolean isAllowedByFilter = true;
        List<ItemStack> filterUpgrades = getInstalledUpgrades(LibItems.UPGRADE_ENTITY_FILTER.get());
        if (!filterUpgrades.isEmpty()) {
            isAllowedByFilter = false;
            for (ItemStack filter : filterUpgrades) {
                List<Identifier> entities = filter.getOrDefault(ModDataComponents.ENTITY_TYPE_LIST, new ArrayList<>());
                for (Identifier beeType : entities) {
                    var allowedBee = BeeIngredientFactory.getIngredient(beeType);
                    if (allowedBee.get() != null) {
                        List<ItemStack> produceList = BeeHelper.getBeeProduce(level, (Bee) allowedBee.get().getCachedEntity(level), false, 1.0);
                        for (ItemStack pStack : produceList) {
                            if (pStack.getItem().equals(stack.getItem())) {
                                isAllowedByFilter = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        RecipeHolder<CentrifugeRecipe> recipe = this.getRecipe(inv);

        return isAllowedByFilter && recipe != null;
    }

    static Map<String, RecipeHolder<CentrifugeRecipe>> recipeMap = new HashMap<>();
    protected RecipeHolder<CentrifugeRecipe> getRecipe(InventoryHandlerHelper.BlockEntityItemStackHandler inputHandler) {
        if (recipeMap.size() > 5000) {
            recipeMap.clear();
        }
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        if (input.isEmpty() || level == null) {
            return null;
        }

        String cacheKey = BeeHelper.itemCacheKey(input);
        if (!recipeMap.containsKey(cacheKey)) {
            recipeMap.put(cacheKey, BeeHelper.getCentrifugeRecipe(level, inputHandler));
        }

        return recipeMap.getOrDefault(cacheKey, null);
    }

    protected boolean canProcessRecipe(@Nullable RecipeHolder<CentrifugeRecipe> recipe, InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();

            recipe.value().getRecipeOutputs().forEach((stack, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(stack.getItem(), value.max());
                outputList.add(item);
            });

            // Allow overfilling of fluid but don't process if the tank has a different fluid
//            FluidStack fluidOutput = recipe.value().getFluidOutputs();
//            boolean fluidFlag = true;
//            if (!fluidOutput.isEmpty()) {
//                fluidFlag = fluidHandler.getFluidInTank(0).isEmpty() || fluidHandler.getFluidInTank(0).getFluid().equals(fluidOutput.getFluid());
//            }

            return invHandler.canFitStacks(outputList);
        }
        return false;
    }

    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, RandomSource random) {
        var inputStack = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        int productivityModifier = Math.min(inputStack.getCount(), Math.min(64, getProductivityModifier()));

        this.completeRecipeProcessing(recipe, invHandler, random, false, productivityModifier);
    }

    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, RandomSource random, boolean stripWax, int productivityModifier) {
        var inputStack = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        double stabilityBonus = (getUpgradeCount(LibItems.UPGRADE_STABILITY.get()) + 1) * ProductiveBeesConfig.UPGRADES.stabilityChanceIncrease.get();
        recipe.value().getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if ((!stripWax || !itemStack.is(ModTags.Common.WAXES)) && random.nextFloat() <= (recipeValues.chance() + stabilityBonus)) {
                int count = Mth.nextInt(random, Mth.floor(recipeValues.min()), Mth.floor(recipeValues.max()));
                ItemStack output = itemStack.copy();
                output.setCount(count * productivityModifier);
                ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(output);
            }
        });

        invHandler.extractItem(InventoryHandlerHelper.INPUT_SLOT, productivityModifier, false, false);

        FluidStack fluidOutput = recipe.value().getFluidOutputs().copy();
        if (!fluidOutput.isEmpty()) {
            fluidOutput.setAmount(fluidOutput.getAmount() * productivityModifier);
            try (Transaction tx = Transaction.openRoot()) {
                fluidHandler.insert(0, FluidResource.of(fluidOutput), fluidOutput.getAmount(), tx);
                tx.commit();
            }
        }
    }

    private void completeGeneProcessing(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, RandomSource random) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        List<GeneGroup> entityData = GeneBottle.getGenes(geneBottle);
        if (entityData.isEmpty()) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (GeneGroup geneGroup : entityData) {
            if (random.nextDouble() <= chance) {
                ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(Gene.getStack(geneGroup, 1));
            }
        }

        invHandler.extractItem(InventoryHandlerHelper.INPUT_SLOT, 1, false, false);
    }

    private void completeTreatProcessing(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        ItemStack honeyTreat = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        List<GeneGroup> genes = HoneyTreat.getGenes(honeyTreat);
        if (!genes.isEmpty()) {
            for (GeneGroup geneGroup : genes) {
                ItemStack insertedGene = Gene.getStack(geneGroup, 1);
                ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(insertedGene);
            }
        }

        invHandler.extractItem(InventoryHandlerHelper.INPUT_SLOT, 1, false, false);
    }

    protected int getProductivityModifier() {
        return Math.max(1,
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get()) * 4 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_2.get()) * 8 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_3.get()) * 16 +
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get()) * 32);
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        recipeProgress = input.getIntOr("RecipeProgress", 0);

        // set fluid ID for screens
        fluidId = BuiltInRegistries.FLUID.getId(fluidHandler.getResource(0).getFluid());
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        output.putInt("RecipeProgress", recipeProgress);
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CentrifugeContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
    }
}
