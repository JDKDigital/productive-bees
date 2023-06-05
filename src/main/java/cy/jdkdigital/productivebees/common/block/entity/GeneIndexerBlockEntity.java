package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.recipe.CombineGeneRecipe;
import cy.jdkdigital.productivebees.container.GeneIndexerContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeneIndexerBlockEntity extends CapabilityBlockEntity
{
    private int tickCounter = 0;
    private boolean needsReindexing = true;
    private boolean isRunning = true;
    private final Map<String, Map<Integer, Integer>> index = new HashMap<>(); // Map<String(name of attribute or bee type), Map<Slot, Purity>>

    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(104, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            return stack.getItem() instanceof Gene;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (tileEntity instanceof GeneIndexerBlockEntity geneIndexerBlockEntity) {
                geneIndexerBlockEntity.setDirty();
            }
        }

        @Override
        public int[] getOutputSlots() {
            return IntStream.range(0, getSlots()).toArray();
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return true;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return this.isItemValid(slot, item);
        }
    });

    public GeneIndexerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GENE_INDEXER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GeneIndexerBlockEntity blockEntity) {
        if (level != null && !level.isClientSide) {
            if (blockEntity.needsReindexing && !blockEntity.isRunning) {
                buildIndex(blockEntity, state);
            }

            if (--blockEntity.tickCounter <= 0) {
                blockEntity.tickCounter = 2;
                if (blockEntity.isRunning && blockEntity.index.size() > 0) {
                    if (!state.getValue(BlockStateProperties.ENABLED)) {
                        blockEntity.isRunning = false;
                        return;
                    }
                    Iterator<Map.Entry<String, Map<Integer, Integer>>> indexIterator = blockEntity.index.entrySet().iterator();
                    if (indexIterator.hasNext()) {
                        Map.Entry<String, Map<Integer, Integer>> first = indexIterator.next();
                        if (first.getValue().size() > 1) {
                            // Minimum 2 entries of the gene, merge first and last entry
                            Supplier<Stream<Map.Entry<Integer, Integer>>> sorted = () -> first.getValue().entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
                            Optional<Map.Entry<Integer, Integer>> firstEntry = sorted.get().findFirst();
                            Optional<Map.Entry<Integer, Integer>> otherEntry = sorted.get().reduce((entry1, entry2) -> entry2);

                            if (firstEntry.isPresent() && otherEntry.isPresent()) {
                                blockEntity.inventoryHandler.ifPresent(inventory -> {
                                    ItemStack firstStack = inventory.getStackInSlot(firstEntry.get().getKey());
                                    ItemStack secondStack = inventory.getStackInSlot(otherEntry.get().getKey());
                                    ItemStack combinedGene = CombineGeneRecipe.mergeGenes(Arrays.asList(firstStack, secondStack));

                                    if (!firstStack.isEmpty() && !secondStack.isEmpty() && !combinedGene.isEmpty()) {
                                        if (inventory instanceof InventoryHandlerHelper.ItemHandler && ((InventoryHandlerHelper.ItemHandler) inventory).addOutput(combinedGene)) {
                                            firstStack.setCount(firstStack.getCount() - 1);
                                            secondStack.setCount(secondStack.getCount() - 1);
                                            inventory.setStackInSlot(firstEntry.get().getKey(), firstStack);
                                            inventory.setStackInSlot(otherEntry.get().getKey(), secondStack);

                                            if (Gene.getPurity(combinedGene) == 100) {
                                                indexIterator.remove();
                                            }
                                        }
                                    } else {
                                        // If either stack is empty, the type no longer has enough stacks to merge
                                        indexIterator.remove();
                                    }
                                });
                            }
                        } else if (first.getValue().size() == 1) {
                            // There's only one of the type of gene present
                            Map.Entry<Integer, Integer> innerEntry = first.getValue().entrySet().iterator().next();
                            blockEntity.inventoryHandler.ifPresent(inventory -> {
                                ItemStack stack = inventory.getStackInSlot(innerEntry.getKey());
                                if (stack.getCount() == 1) {
                                    indexIterator.remove();
                                } else if (stack.getCount() > 1) {
                                    // Merge with self
                                    ItemStack combinedGene = CombineGeneRecipe.mergeGenes(Arrays.asList(stack, stack.copy()));
                                    if (!stack.isEmpty() && !combinedGene.isEmpty()) {
                                        if (inventory instanceof InventoryHandlerHelper.ItemHandler && ((InventoryHandlerHelper.ItemHandler) inventory).addOutput(combinedGene)) {
                                            stack.setCount(stack.getCount() - 2);
                                            inventory.setStackInSlot(innerEntry.getKey(), stack);

                                            if (Gene.getPurity(combinedGene) == 100) {
                                                indexIterator.remove();
                                            }
                                        }
                                    }
                                } else {
                                    indexIterator.remove();
                                }
                            });
                        } else {
                            indexIterator.remove();
                        }
                    }
                } else {
                    blockEntity.isRunning = false; // no more work to do
                }
            }
        }
    }

    private static void buildIndex(GeneIndexerBlockEntity blockEntity, BlockState state) {
        blockEntity.index.clear();

        // Iterate items and find out which should attempt a merge
        blockEntity.inventoryHandler.ifPresent(inventory -> {
            for (int slot = 0; slot < inventory.getSlots(); ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.getItem().equals(ModItems.GENE.get())) {
                    // Check purity, 100% pure can be ignored
                    int purity = Gene.getPurity(stack);
                    if (purity < 100) {
                        // Create a list for each attribute variant having the same attribute but different purity
                        String key = Gene.getAttributeName(stack) + "-" + Gene.getValue(stack);

                        // Initiate internal map
                        if (!blockEntity.index.containsKey(key)) {
                            blockEntity.index.put(key, new HashMap<>());
                        }
                        Map<Integer, Integer> internalMap = blockEntity.index.get(key);

                        internalMap.put(slot, purity);
                    }
                }
            }
        });
        if (state.getValue(BlockStateProperties.ENABLED)) {
            blockEntity.isRunning = true;
        }
        blockEntity.needsReindexing = false;
    }

    public void setDirty() {
        needsReindexing = true;
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.GENE_INDEXER.get().getDescriptionId());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.GENE_INDEXER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new GeneIndexerContainer(windowId, playerInventory, this);
    }
}
