package cy.jdkdigital.productivebees.mixin;

import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(value = Bee.BeePollinateGoal.class)
public abstract class BeePollinateGoalMixin
{
    @Final
    @Shadow
    Bee this$0;

    @Unique
    private final Predicate<BlockPos> PB_VALID_POLLINATION_BLOCKS = blockPos -> {
        var blockState = this$0.level().getBlockState(blockPos);
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            return false;
        } else if (blockState.is(BlockTags.FLOWERS)) {
            return !blockState.is(Blocks.SUNFLOWER) || blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
        } else if (this$0.level().getBlockEntity(blockPos) instanceof FeederBlockEntity feederBlockEntity) {
            return ProductiveBee.isValidFeeder(this$0, feederBlockEntity, (state) -> state.is(BlockTags.FLOWERS), (stack) -> stack.is(ItemTags.FLOWERS));
        } else {
            return false;
        }
    };

    @Unique
    private Optional<BlockPos> productivebees$findNearestBlockByBlockPos(Predicate<BlockPos> predicate, double distance) {
        BlockPos blockpos = this$0.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i = 0; (double)i <= distance; i = i > 0 ? -i : 1 - i) {
            for (int j = 0; (double)j < distance; j++) {
                for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                    for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
                        if (blockpos.closerThan(blockpos$mutableblockpos, distance)
                                && predicate.test(blockpos$mutableblockpos)) {
                            return Optional.of(blockpos$mutableblockpos);
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Inject(at = {@At(value = "RETURN")}, method = {"findNearbyFlower"}, cancellable = true)
    public void findNearbyFlower(CallbackInfoReturnable<Optional<BlockPos>> ci) {
        if (ci.getReturnValue().isEmpty()) {
            ci.setReturnValue(this.productivebees$findNearestBlockByBlockPos(PB_VALID_POLLINATION_BLOCKS, 5.0));
        }
    }
}
