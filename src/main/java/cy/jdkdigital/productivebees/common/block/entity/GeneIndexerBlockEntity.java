package cy.jdkdigital.productivebees.common.block.entity;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.recipe.CombineGeneRecipe;
import cy.jdkdigital.productivebees.container.GeneIndexerContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

public class GeneIndexerBlockEntity extends CapabilityBlockEntity implements MenuProvider
{

    private boolean isProcessing = false;
    private final Queue<InsertionAction> queue = new LinkedList<>();
    private final Map<String, List<SlotEntry>> index = new HashMap<>();
    
    private record SlotEntry(int slot, int purity) { }
    private record InsertionAction(String key, SlotEntry entry, ItemStack stack, int slot) { }
    
    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(104, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            return stack.getItem() instanceof Gene;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (!(blockEntity instanceof GeneIndexerBlockEntity indexer)) return;
            
            ItemStack stack = indexer.inventoryHandler.getStackInSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof Gene)) {
                updateSlot(indexer, stack, slot);
                return;
            }
            
            GeneGroup group = Gene.getGenes(stack);
            if (group == null || group.purity() == 100) {
                updateSlot(indexer, stack, slot);
                return;
            }

            String key = group.attribute().getSerializedName() + "-" + group.value();
            if (!indexer.index.containsKey(key)) {
                updateSlot(indexer, stack, slot);
                return;
            }
            
            List<SlotEntry> entries = indexer.index.get(key);
            if (entries == null || entries.isEmpty()) {
                updateSlot(indexer, stack, slot);
                return;
            }

            Optional<SlotEntry> optional = entries.stream().filter(e -> e.slot() != slot).findFirst();
            
            if (optional.isEmpty()) {
                updateSlot(indexer, stack, slot);
                return;
            }

            if (indexer.isProcessing) {
                updateSlot(indexer, stack, slot);
                return;
            }

            indexer.isProcessing = true;
            try {
                indexer.queue.add(new InsertionAction(key, optional.get(), stack, slot));
            } finally {
                indexer.isProcessing = false;
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
    };

    public GeneIndexerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GENE_INDEXER.get(), pos, state);
    }
    
    public static void tick(Level world, BlockPos pos,  BlockState state, GeneIndexerBlockEntity indexer) {
        if (!(world instanceof ServerLevel)) return;
        while (!indexer.queue.isEmpty()) {
            InsertionAction stack = indexer.queue.poll();
            indexer.process(indexer, stack.key(), stack.entry(), stack.stack(), stack.slot());
        }
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide) buildIndex(this);
        super.onLoad();
    }
    
    private void process(GeneIndexerBlockEntity indexer, String key, SlotEntry entry, ItemStack stack, int slot) {

        List<SlotEntry> entries = indexer.index.get(key);
        if (entries == null || entries.isEmpty()) {
            updateSlot(indexer, stack, slot);
            return;
        }
        
        indexer.isProcessing = true;
        try {
            
            InventoryHandlerHelper.BlockEntityItemStackHandler handler = (InventoryHandlerHelper.BlockEntityItemStackHandler) indexer.inventoryHandler;

            ItemStack entryStack = handler.getStackInSlot(entry.slot());

            int stackCount = stack.getCount();
            int entryCount = entryStack.getCount();

            List<ItemStack> genesToCombine = new ArrayList<>();
            for (int i = 0; i < stackCount; i++) {
                genesToCombine.add(stack.copyWithCount(1));
            }
            for (int i = 0; i < entryCount; i++) {
                genesToCombine.add(entryStack.copyWithCount(1));
            }

            stack.shrink(stackCount);
            entryStack.shrink(entryCount);
            updateSlot(indexer, stack, slot);
            updateSlot(indexer, entryStack, entry.slot());

            while (!genesToCombine.isEmpty()) {
                Pair<ItemStack, ItemStack> combination = CombineGeneRecipe.mergeGenes(genesToCombine);

                if (!combination.getFirst().isEmpty()) {
                    handler.addOutput(combination.getFirst());

                    if (Gene.getPurity(combination.getFirst()) == 100) {
                        entries.remove(entry);
                    }
                }

                genesToCombine.clear();
                if (!combination.getSecond().isEmpty()) {
                    genesToCombine.add(combination.getSecond());
                } else {
                    break;
                }
            }

            indexer.index.entrySet().removeIf(mapEntry -> mapEntry.getValue().isEmpty());

        } finally {
            indexer.isProcessing = false;
        }
    }

    private static void buildIndex(GeneIndexerBlockEntity blockEntity) {
        blockEntity.index.clear();

        for (int slot = 0; slot < blockEntity.inventoryHandler.getSlots(); ++slot) {
            ItemStack stack = blockEntity.inventoryHandler.getStackInSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof Gene)) continue;

            GeneGroup group = Gene.getGenes(stack);
            if (group == null || group.purity() == 100) continue;
            
            String key = group.attribute().getSerializedName() + "-" + group.value();
            blockEntity.index.computeIfAbsent(key, k -> new ArrayList<>()).add(new SlotEntry(slot, group.purity()));
        }
    }
    
    private static void updateSlot(GeneIndexerBlockEntity blockEntity, ItemStack stack, int slot) {
        blockEntity.index.values().forEach(list -> list.removeIf(entry -> entry.slot() == slot));
        
        GeneGroup group = Gene.getGenes(stack);
        if (group == null || group.purity() == 100) {
            return;
        }
        
        String key = group.attribute().getSerializedName() + "-" + group.value();
        
        if (!stack.isEmpty()) {
            blockEntity.index.computeIfAbsent(key, k -> new ArrayList<>()).add(new SlotEntry(slot, group.purity()));
        }
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.GENE_INDEXER.get().getDescriptionId());
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.GENE_INDEXER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new GeneIndexerContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }
}
