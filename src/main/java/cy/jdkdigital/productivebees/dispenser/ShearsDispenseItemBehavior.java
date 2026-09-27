package cy.jdkdigital.productivebees.dispenser;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShearsDispenseItemBehavior implements DispenseItemBehavior
{
    private final DispenseItemBehavior fallback;

    public ShearsDispenseItemBehavior(DispenseItemBehavior fallback) {
        this.fallback = fallback;
    }

    @Override
    public ItemStack dispense(BlockSource blockSource, ItemStack stack) {
        ServerLevel level = blockSource.level();
        Direction facing = blockSource.state().getValue(DispenserBlock.FACING);

        if (!tryShearBeehive(level, blockSource.pos().relative(facing), stack)) {
            return fallback.dispense(blockSource, stack);
        }

        stack.hurtAndBreak(1, level, null, item -> stack.setCount(0));
        level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, blockSource.pos(), 0);
        level.levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, blockSource.pos(), facing.get3DDataValue());
        return stack;
    }

    private static boolean tryShearBeehive(ServerLevel level, BlockPos pos, ItemStack tool) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof AdvancedBeehive hive) || !state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
            return false;
        }

        if (state.getValue(BeehiveBlock.HONEY_LEVEL) < hive.getMaxHoneyLevel()) {
            return false;
        }

        level.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        BeehiveBlock.dropHoneycomb(level, tool, state, level.getBlockEntity(pos), null, pos);
        level.setBlockAndUpdate(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, hive.getMaxHoneyLevel() - 5));
        level.gameEvent(null, GameEvent.SHEAR, pos);
        return true;
    }
}
