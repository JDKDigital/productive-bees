package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class CavernSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private boolean top;

    public CavernSolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory, boolean top) {
        super(probability, configFactory);
        this.probability = probability;
        this.top = top;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();
        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (rand.nextFloat() > this.probability) {
                return false;
            }

            // Get random block in chunk
            blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

            // Go to roof
            BlockStatePredicate matcher = BlockStatePredicate.forBlock(targetBlockState.state.getBlock());
            while (blockPos.getY() < 127 && !matcher.test(world.getBlockState(blockPos))) {
                blockPos = blockPos.above();
            }

            if (top) {
                // Go to surface
                while (blockPos.getY() < 127 && !world.isEmptyBlock(blockPos)) {
                    blockPos = blockPos.above();
                }
                blockPos = blockPos.below();
            }

            BlockState state = placeOntop ? world.getBlockState(blockPos.below()) : world.getBlockState(blockPos);
            if (targetBlockState.target.test(state, rand)) {
                return placeNest(world, blockPos, targetBlockState.state, rand);
            }
        }
        return false;
    }
}
