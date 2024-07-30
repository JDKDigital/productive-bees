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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BottlerBlockEntity extends FluidTankBlockEntity implements MenuProvider
{
    protected int tickCounter = 0;
    public int fluidId = 0;

    public IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(12, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || item == Items.HONEYCOMB;
        }
    };

    public FluidTank fluidHandler = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            BottlerBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
            BottlerBlockEntity.this.updateBottleState();
        }
    };

    private void updateBottleState() {
        if (level != null) {
            ItemStack stack = inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
            boolean hasBottle = !stack.isEmpty() && stack.getItem().equals(Items.GLASS_BOTTLE);
            level.setBlock(getBlockPos(), this.getBlockState().setValue(Bottler.HAS_BOTTLE, hasBottle), 3);
        }
    }

    public BottlerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOTTLER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BottlerBlockEntity blockEntity) {
        BlockState aboveState = level.getBlockState(pos.above());
        if (++blockEntity.tickCounter % 7 == 0 && aboveState.getBlock() == Blocks.PISTON_HEAD && aboveState.getValue(DirectionalBlock.FACING) == Direction.DOWN) {
            // Check for bees on top of block
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(pos).expandTowards(0.0D, 1.0D, 0.0D))).stream().filter(e -> !e.isBaby()).toList();
            if (!bees.isEmpty()) {
                Bee bee = bees.iterator().next();
                ItemStack bottles = blockEntity.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                if (!bottles.isEmpty() && bottles.getItem().equals(Items.GLASS_BOTTLE) && !bee.isBaby() && bee.isAlive()) {
                    ItemStack geneBottle = GeneBottle.getStack(bee);
                    Block.popResource(level, pos.above(), geneBottle);
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    bee.kill();
                    bottles.shrink(1);
                }
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        FluidStack fluidStack = blockEntity.getFluidHandler().getFluidInTank(0);
        if (fluidStack.getAmount() >= 0 && level instanceof ServerLevel && blockEntity instanceof BottlerBlockEntity bottlerBlockEntity) {
            IItemHandler invHandler = bottlerBlockEntity.getItemHandler();
            if (invHandler instanceof ItemStackHandler) {
                ItemStack fluidContainerItem = invHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                ItemStack existingOutput = invHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
                if (fluidContainerItem.getCount() > 0 && (existingOutput.isEmpty() || (existingOutput.getCount() < existingOutput.getMaxStackSize()))) {
                    // Look up bottler recipes from input
                    List<BottlerRecipe> recipes = new ArrayList<>();
                    List<RecipeHolder<BottlerRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.BOTTLER_TYPE.get());
                    for (RecipeHolder<BottlerRecipe> entry : allRecipes) {
                        BottlerRecipe recipe = entry.value();
                        if (recipe.matches(fluidStack, fluidContainerItem)) {
                            recipes.add(recipe);
                        }
                    }

                    if (recipes.size() > 0) {
                        BottlerRecipe recipe = recipes.iterator().next();
                        if (existingOutput.isEmpty() || existingOutput.getItem().equals(recipe.getResultItem(level.registryAccess()).getItem())) {
                            processOutput(fluidHandler, invHandler, recipe.getResultItem(level.registryAccess()).copy(), recipe.fluidInput.amount(), true);
                        }
                    } else if (fluidContainerItem.getCapability(Capabilities.FluidHandler.ITEM) instanceof IFluidHandlerItem itemFluidHandler) {
                        // try filling fluid container
//                        var h = fluidContainerItem.getCapability(Capabilities.FluidHandler.ITEM);
                        int amount = itemFluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        processOutput(fluidHandler, invHandler, itemFluidHandler.getFluidInTank(0).getAmount() == itemFluidHandler.getTankCapacity(0) ? fluidContainerItem : null, amount, false);
                    } else {
                        // try to fill bucket
                        FluidActionResult fillResult = FluidUtil.tryFillContainer(fluidContainerItem, fluidHandler, Integer.MAX_VALUE, null, true);
                        if (fillResult.isSuccess()) {
                            processOutput(fluidHandler, invHandler, fillResult.getResult(), 0, true);
                        }
                    }
                }
            }
        }
    }

    private static void processOutput(IFluidHandler fluidHandler, IItemHandler itemHandler, ItemStack outputItem, int drainedAmount, boolean shrinkInputStack) {
        if (shrinkInputStack) {
            itemHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).shrink(1);
        }
        if (outputItem != null) {
            itemHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, outputItem, false);
        }
        fluidHandler.drain(drainedAmount, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        // set fluid ID for screens
        Fluid fluid = fluidHandler.getFluidInTank(0).getFluid();
        fluidId = BuiltInRegistries.FLUID.getId(fluid);
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
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public FluidTank getFluidHandler() {
        return fluidHandler;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BottlerContainer(pContainerId, pPlayerInventory, this);
    }
}
