package cy.jdkdigital.productivebees.dispenser;

import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

public class CageDispenseBehavior extends OptionalDispenseItemBehavior {
    private final DefaultDispenseItemBehavior fallbackDispenseBehavior = new DefaultDispenseItemBehavior();

    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
        if (stack.getItem() instanceof BeeCage && BeeCage.isFilled(stack)) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);

            Bee entity = BeeCage.getEntityFromStack(stack, source.getLevel(), true);
            if (entity != null) {
                entity.hivePos = null;

                BlockPos spawnPos = source.getPos().relative(direction);

                entity.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

                if (source.getLevel().addFreshEntity(entity)) {
                    if (stack.getItem().equals(ModItems.BEE_CAGE.get())) {
                        stack.shrink(1);
                    } else if (stack.getItem().equals(ModItems.STURDY_BEE_CAGE.get())) {
                        stack.shrink(1);
                        if (source.getLevel().getBlockEntity(source.getPos()) instanceof DispenserBlockEntity dispenser) {
                            dispenser.addItem(new ItemStack(ModItems.STURDY_BEE_CAGE.get()));
                        }
                    }
                }
                return stack;
            }
        }
        return fallbackDispenseBehavior.dispense(source, stack);
    }
}
