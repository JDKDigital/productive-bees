package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.DragonEggHive;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DragonEggHiveBlockEntity extends AdvancedBeehiveBlockEntity
{
    public DragonEggHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DRACONIC_BEEHIVE.get(), pos, state);
        MAX_BEES = 3;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedBeehiveBlockEntity blockEntity) {
        if (blockEntity.tickCounter % 23 == 0) {
            if (state.getBlock() instanceof DragonEggHive) {
                int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    ItemStack bottles = blockEntity.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    if (!bottles.isEmpty()) {
                        final ItemStack filledBottle = new ItemStack(Items.DRAGON_BREATH);
                        boolean addedBottle = ((InventoryHandlerHelper.BlockEntityItemStackHandler) blockEntity.inventoryHandler).addOutput(filledBottle).getCount() == 0;
                        if (addedBottle) {
                            bottles.shrink(1);
                            level.setBlockAndUpdate(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel - 5));
                        }
                    }
                }
            }
        }

        if (--blockEntity.abandonCountdown < 0) {
            blockEntity.abandonCountdown = 0;
        }

        AdvancedBeehiveBlockEntityAbstract.tick(level, pos, state, blockEntity);
    }
}
