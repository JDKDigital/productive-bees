package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReedSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;

    public ReedSolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory) {
        super(probability, configFactory);
        this.probability = probability;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel world = context.level();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (rand.nextFloat() > this.probability) {
                return false;
            }

            // Get to ground level
            blockPos = blockPos.above(chunkGenerator.getSpawnHeight(world));

            // Go to ground surface
            while (blockPos.getY() < 127 && !world.isEmptyBlock(blockPos)) {
                blockPos = blockPos.above();
            }

            // Go up some more
            blockPos = blockPos.above(rand.nextInt(2));

            BlockPos newPos = null;
            blockFound:
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    newPos = blockPos.offset(x, 0, z);
                    if (targetBlockState.target.test(world.getBlockState(newPos), rand)) {
                        break blockFound;
                    }
                    newPos = null;
                }
            }

            if (newPos != null) {
                if (!targetBlockState.target.test(world.getBlockState(newPos.below()), rand)) {
                    newPos = newPos.above();
                }
                BlockState state = placeOntop ? world.getBlockState(newPos.below()) : world.getBlockState(newPos);
                if (targetBlockState.target.test(state, rand)) {
                    return placeNest(world, newPos, targetBlockState.state, rand);
                }
            }
        }
        return false;
    }
}
