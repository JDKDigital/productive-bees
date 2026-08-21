package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivelib.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.List;

public class BottlerBlockEntity extends FluidTankBlockEntity implements MenuProvider
{
    protected int tickCounter = 0;
    public int fluidId = 0;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(12, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || item == Items.HONEYCOMB;
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack previousContents) {
            super.onContentsChanged(slot, previousContents);
            if (slot == InventoryHandlerHelper.BOTTLE_SLOT) {
                BottlerBlockEntity.this.updateBottleState();
            }
        }
    };

    public FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, 10000)
    {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            super.onContentsChanged(index, previousContents);
            BottlerBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getResource(index).getFluid());
            BottlerBlockEntity.this.updateBottleState();
        }
    };

    private void updateBottleState() {
        if (level != null) {
            ItemStack stack = inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
            boolean hasBottle = !stack.isEmpty() && stack.getItem().equals(Items.GLASS_BOTTLE);
            if (hasBottle != this.getBlockState().getValue(Bottler.HAS_BOTTLE)) {
                level.setBlock(getBlockPos(), this.getBlockState().setValue(Bottler.HAS_BOTTLE, hasBottle), 3);
            }
        }
    }

    public BottlerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOTTLER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BottlerBlockEntity blockEntity) {
        BlockState aboveState = level.getBlockState(pos.above());
        if (++blockEntity.tickCounter % 7 == 0 && aboveState.getBlock() == Blocks.PISTON_HEAD && aboveState.getValue(DirectionalBlock.FACING) == Direction.DOWN) {
            // Check for bees on top of block
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, new AABB(pos).expandTowards(0.0D, 1.0D, 0.0D));
            Bee bee = null;
            for (Bee candidate : bees) {
                if (!candidate.isBaby()) {
                    bee = candidate;
                    break;
                }
            }
            if (bee != null) {
                ItemStack bottles = blockEntity.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                if (!bottles.isEmpty() && bottles.getItem().equals(Items.GLASS_BOTTLE) && bee.isAlive()) {
                    // Generate item
                    ItemStack geneBottle = GeneBottle.getStack(bee);
                    if (!geneBottle.isEmpty()) {
                        // Determine drop position
                        Direction facing = aboveState.getValue(DirectionalBlock.FACING);
                        BlockPos dropPos = pos.relative(facing.getOpposite());
                        
                        // Create ItemEntity directly instead of using Block.popResource
                        ItemEntity itemEntity = new ItemEntity(level, 
                            dropPos.getX() + 0.5, 
                            dropPos.getY() + 0.5, 
                            dropPos.getZ() + 0.5, 
                            geneBottle);
                        itemEntity.setDefaultPickUpDelay(); // Set default pickup delay
                        level.addFreshEntity(itemEntity); // Add to world immediately
                        
                        // Play sound effect
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        
                        // Consume resources and process entity (moved to after item generation to ensure items appear first)
                        blockEntity.inventoryHandler.extractItem(InventoryHandlerHelper.BOTTLE_SLOT, 1, false, false);
                        if (level instanceof ServerLevel sl) {
                            bee.kill(sl);
                        }
                    }
                }
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        FluidResource tankResource = fluidHandler.getResource(0);
        int tankAmount = fluidHandler.getAmountAsInt(0);
        if (tankResource.isEmpty() || tankAmount <= 0) {
            return;
        }
        ItemStack fluidContainerItem = inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
        ItemStack existingOutput = inventoryHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
        if (fluidContainerItem.isEmpty() || (!existingOutput.isEmpty() && existingOutput.getCount() >= existingOutput.getMaxStackSize())) {
            return;
        }

        FluidStack tankStack = tankResource.toStack(tankAmount);

        // 1) Bottler recipe match — produces a configured result item.
        RecipeHolder<BottlerRecipe> matched = null;
        for (RecipeHolder<BottlerRecipe> entry : serverLevel.recipeAccess().recipeMap().byType(ModRecipeTypes.BOTTLER_TYPE.get())) {
            if (entry.value().matches(tankStack, fluidContainerItem)) {
                matched = entry;
                break;
            }
        }
        if (matched != null) {
            BottlerRecipe recipe = matched.value();
            ItemStack resultItem = recipe.getResult().copy();
            // Simulate first: without room for the result the bottle and fluid would be eaten for nothing.
            if (inventoryHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, resultItem, true).isEmpty()) {
                int drainAmount = recipe.fluidInput.amount();
                boolean drained = false;
                try (Transaction tx = Transaction.openRoot()) {
                    if (fluidHandler.extract(0, tankResource, drainAmount, tx) == drainAmount) {
                        tx.commit();
                        drained = true;
                    }
                }
                if (drained) {
                    inventoryHandler.extractItem(InventoryHandlerHelper.BOTTLE_SLOT, 1, false, false);
                    inventoryHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, resultItem, false);
                }
            }
            return;
        }

        // 2) Generic fluid-container item — fill the item from the tank in place.
        ResourceHandler<FluidResource> itemFluidHandler = ItemAccess.forHandlerIndex(inventoryHandler, InventoryHandlerHelper.BOTTLE_SLOT).getCapability(Capabilities.Fluid.ITEM);
        if (itemFluidHandler != null) {
            boolean filledItem = false;
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = itemFluidHandler.insert(tankResource, tankAmount, tx);
                // Commit only a balanced move; a short extract would mint the difference.
                if (inserted > 0 && fluidHandler.extract(0, tankResource, inserted, tx) == inserted) {
                    tx.commit();
                    filledItem = true;
                }
            }
            // Once the container is at capacity, hand one of them to the output slot.
            if (filledItem && itemFluidHandler.getAmountAsInt(0) >= itemFluidHandler.getCapacityAsInt(0, tankResource)) {
                ItemStack filled = inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                if (!filled.isEmpty() && (existingOutput.isEmpty() || existingOutput.getItem().equals(filled.getItem()))
                        && inventoryHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, filled.copyWithCount(1), false).isEmpty()) {
                    inventoryHandler.extractItem(InventoryHandlerHelper.BOTTLE_SLOT, 1, false, false);
                }
            }
        }
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        // set fluid ID for screens
        fluidId = BuiltInRegistries.FLUID.getId(fluidHandler.getResource(0).getFluid());
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.BOTTLER.get().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BottlerContainer(pContainerId, pPlayerInventory, this);
    }
}
