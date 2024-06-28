package cy.jdkdigital.productivebees.dispenser;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior extends OptionalDispenseItemBehavior  {
    @Override
    protected ItemStack execute(BlockSource pBlockSource, ItemStack stack) {
        ServerLevel level = pBlockSource.level();
        BlockPos blockpos = pBlockSource.pos().relative(pBlockSource.state().getValue(DispenserBlock.FACING));
        this.setSuccess(tryShearBeehive(level, blockpos) || tryShearLivingEntity(level, blockpos));
        if (this.isSuccess()) {
            stack.hurtAndBreak(1, level, null, item -> stack.setCount(0));
        }
        return stack;
    }

    private static boolean tryShearBeehive(Level level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        if (blockstate.is(BlockTags.BEEHIVES)) {
            int i = blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
            if (i >= 5) {
                level.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                BeehiveBlock.dropHoneycomb(level, pos);
                if (blockstate.getBlock() instanceof BeehiveBlock beehiveBlock) {
                    beehiveBlock.releaseBeesAndResetHoneyLevel(level, blockstate, pos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                } else if (blockstate.getBlock() instanceof AdvancedBeehive beehiveBlock) {
                    level.setBlockAndUpdate(pos, blockstate.setValue(BeehiveBlock.HONEY_LEVEL, beehiveBlock.getMaxHoneyLevel() - 5));
                }
                level.gameEvent(null, GameEvent.SHEAR, pos);
                return true;
            }
        }
        return false;
    }

    private static boolean tryShearLivingEntity(Level level, BlockPos pos) {
        for(LivingEntity livingentity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos), EntitySelector.NO_SPECTATORS)) {
            if (livingentity instanceof Shearable shearable) {
                if (shearable.readyForShearing()) {
                    shearable.shear(SoundSource.BLOCKS);
                    level.gameEvent(null, GameEvent.SHEAR, pos);
                    return true;
                }
            }
        }
        return false;
    }
}
