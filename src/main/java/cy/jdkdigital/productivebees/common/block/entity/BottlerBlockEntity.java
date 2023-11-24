package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BottlerBlockEntity extends FluidTankBlockEntity
{
    protected int tickCounter = 0;
    public int fluidId = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || item == Items.HONEYCOMB;
        }
    });

    private LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            BottlerBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
            BottlerBlockEntity.this.updateBottleState();
        }
    });

    private void updateBottleState() {
        if (level != null) {
            inventoryHandler.ifPresent(inv -> {
                ItemStack stack = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                boolean hasBottle = !stack.isEmpty() && stack.getItem().equals(Items.GLASS_BOTTLE);
                level.setBlock(getBlockPos(), this.getBlockState().setValue(Bottler.HAS_BOTTLE, hasBottle), 3);
            });
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
                blockEntity.inventoryHandler.ifPresent(inv -> {
                    ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    if (!bottles.isEmpty() && bottles.getItem().equals(Items.GLASS_BOTTLE) && !bee.isBaby() && bee.isAlive()) {
                        ItemStack geneBottle = GeneBottle.getStack(bee);
                        Block.popResource(level, pos.above(), geneBottle);
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        bee.kill();
                        bottles.shrink(1);
//                        if (!player.isClientSide()) {
//                            player.awardStat(Stats.ITEM_USED.get(item));
//                        }
                    }
                });
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (fluidStack.getAmount() >= 0 && level instanceof ServerLevel) {
                blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(invHandler -> {
                    ItemStack fluidContainerItem = invHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    ItemStack existingOutput = invHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
                    if (fluidContainerItem.getCount() > 0 && (existingOutput.isEmpty() || (existingOutput.getCount() < existingOutput.getMaxStackSize()))) {
                        // Look up bottler recipes from input
                        List<BottlerRecipe> recipes = new ArrayList<>();
                        Map<ResourceLocation, BottlerRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.BOTTLER_TYPE.get());
                        for (Map.Entry<ResourceLocation, BottlerRecipe> entry : allRecipes.entrySet()) {
                            BottlerRecipe recipe = entry.getValue();
                            if (recipe.matches(fluidStack, fluidContainerItem)) {
                                recipes.add(recipe);
                            }
                        }

                        if (recipes.size() > 0) {
                            BottlerRecipe recipe = recipes.iterator().next();
                            if (existingOutput.isEmpty() || existingOutput.getItem().equals(recipe.getResultItem(level.registryAccess()).getItem())) {
                                processOutput(fluidHandler, invHandler, recipe.getResultItem(level.registryAccess()).copy(), recipe.fluidInput.getSecond(), true);
                            }
                        } else if (fluidContainerItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                            // try filling fluid container
                            fluidContainerItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(h -> {
                                int amount = h.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                processOutput(fluidHandler, invHandler, h.getFluidInTank(0).getAmount() == h.getTankCapacity(0) ? fluidContainerItem : null, amount, false);
                            });
                        } else {
                            // try to fill bucket
                            FluidActionResult fillResult = FluidUtil.tryFillContainer(fluidContainerItem, fluidHandler, Integer.MAX_VALUE, null, true);
                            if (fillResult.isSuccess()) {
                                processOutput(fluidHandler, invHandler, fillResult.getResult(), 0, true);
                            }
                        }
                    }
                });
            }
        });
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
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = BuiltInRegistries.FLUID.getId(fluid);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.BOTTLER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new BottlerContainer(windowId, playerInventory, this);
    }
}
