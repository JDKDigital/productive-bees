package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;

public class DragonEggHiveBlockEntity extends AdvancedBeehiveBlockEntity
{
    public DragonEggHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DRACONIC_BEEHIVE.get(), pos, state);
        MAX_BEES = 3;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DragonEggHiveBlockEntity blockEntity) {
        if (blockEntity.tickCounter % 23 == 0) {
            if (state.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                        if (!bottles.isEmpty()) {
                            final ItemStack filledBottle = level.dimension() == Level.END ? new ItemStack(Items.DRAGON_BREATH) : new ItemStack(Items.HONEY_BOTTLE);
                            boolean addedBottle = ((InventoryHandlerHelper.ItemHandler) inv).addOutput(filledBottle);
                            if (addedBottle) {
                                bottles.shrink(1);
                                level.setBlockAndUpdate(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel - 5));
                            }
                        }
                    });
                }
            }
        }

        blockEntity.hasTicked = true;

        AdvancedBeehiveBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    protected void applyHiveProductionModifier(ItemStack stack) {
        super.applyHiveProductionModifier(stack);
        stack.setCount(stack.getCount() * 2);
    }
}
