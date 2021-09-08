package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.container.GeneIndexerContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.CombineGeneRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeneIndexerTileEntity extends CapabilityTileEntity implements INamedContainerProvider, ITickableTileEntity
{
    private int tickCounter = 0;
    private boolean needsReindexing = true;
    private boolean isRunning = true;
    private Map<String, Map<Integer, Integer>> index = new HashMap<>(); // Map<String(name of attribute or bee type), Map<Slot, Purity>>

    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(104, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            return stack.getItem().equals(ModItems.GENE.get());
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (tileEntity instanceof GeneIndexerTileEntity) {
                ((GeneIndexerTileEntity) tileEntity).setDirty();
            }
        }

        @Override
        public int[] getOutputSlots() {
            return IntStream.range(0, getSlots()).toArray();
        }
    });

    public GeneIndexerTileEntity() {
        super(ModTileEntityTypes.GENE_INDEXER.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            if (needsReindexing && !isRunning) {
                buildIndex();
            }

            if (--tickCounter <= 0) {
                tickCounter = 2;
                if (isRunning && index.size() > 0) {
                    if (!this.getBlockState().getValue(BlockStateProperties.ENABLED)) {
                        isRunning = false;
                        return;
                    }
                    Iterator<Map.Entry<String, Map<Integer, Integer>>> indexIterator = index.entrySet().iterator();
                    if (indexIterator.hasNext()) {
                        Map.Entry<String, Map<Integer, Integer>> first = indexIterator.next();
                        if (first.getValue().size() > 1) {
                            // Minimum 2 entries of the gene, merge first and last entry
                            Supplier<Stream<Map.Entry<Integer, Integer>>> sorted = () -> first.getValue().entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
                            Optional<Map.Entry<Integer, Integer>> firstEntry = sorted.get().findFirst();
                            Optional<Map.Entry<Integer, Integer>> otherEntry = sorted.get().reduce((entry1, entry2) -> entry2);

                            if (firstEntry.isPresent() && otherEntry.isPresent()) {
                                inventoryHandler.ifPresent(inventory -> {
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
                            inventoryHandler.ifPresent(inventory -> {
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
                    isRunning = false; // no more work to do
                }
            }
        }
    }

    private void buildIndex() {
        index.clear();

        // Iterate items and find out which should attempt a merge
        inventoryHandler.ifPresent(inventory -> {
            for (int slot = 0; slot < inventory.getSlots(); ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.getItem().equals(ModItems.GENE.get())) {
                    // Check purity, 100% pure can be ignored
                    int purity = Gene.getPurity(stack);
                    if (purity < 100) {
                        // Create a list for each attribute variant having the same attribute but different purity
                        String key = Gene.getAttributeName(stack);

                        // Initiate internal map
                        if (!index.containsKey(key)) {
                            index.put(key, new HashMap<>());
                        }
                        Map<Integer, Integer> internalMap = index.get(key);

                        internalMap.put(slot, purity);
                    }
                }
            }
        });
        if (this.getBlockState().getValue(BlockStateProperties.ENABLED)) {
            isRunning = true;
        }
        needsReindexing = false;
    }

    public void setDirty() {
        needsReindexing = true;
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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.GENE_INDEXER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new GeneIndexerContainer(windowId, playerInventory, this);
    }
}
