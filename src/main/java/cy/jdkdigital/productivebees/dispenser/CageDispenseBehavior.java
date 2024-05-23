package cy.jdkdigital.productivebees.dispenser;

import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

public class CageDispenseBehavior extends OptionalDispenseItemBehavior {
    private final DefaultDispenseItemBehavior fallbackDispenseBehavior = new DefaultDispenseItemBehavior();

    @Override
    protected ItemStack execute(BlockSource pBlockSource, ItemStack stack) {
        if (stack.getItem() instanceof BeeCage && BeeCage.isFilled(stack)) {
            Direction direction = pBlockSource.state().getValue(DispenserBlock.FACING);

            Bee entity = BeeCage.getEntityFromStack(stack, pBlockSource.level(), true);
            if (entity != null) {
                entity.hivePos = null;

                BlockPos spawnPos = pBlockSource.pos().relative(direction);

                entity.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

                if (pBlockSource.level().addFreshEntity(entity)) {
                    if (stack.getItem().equals(ModItems.STURDY_BEE_CAGE.get())) {
                        if (pBlockSource.blockEntity().addItem(new ItemStack(ModItems.STURDY_BEE_CAGE.get())) < 0) {
                            Block.popResource(pBlockSource.level(), pBlockSource.pos().above(), new ItemStack(ModItems.STURDY_BEE_CAGE.get()));
                        }
                    }
                    stack.shrink(1);
                }
                if (stack.getCount() > 0) {
                    return stack;
                }
            }
        }
        return fallbackDispenseBehavior.dispense(pBlockSource, stack);
    }
}
