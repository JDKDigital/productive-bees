package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import javax.annotation.Nonnull;
import java.util.Random;

public class ReedSolitaryNestFeature extends WoodSolitaryNestFeature
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
        Random rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (nestShouldNotGenerate(targetBlockState.state) || rand.nextFloat() > this.probability) {
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

            // Locate tree log in chunk
            BlockStatePredicate matcher = BlockStatePredicate.forBlock(targetBlockState.state.getBlock());

            BlockPos newPos = null;
            blockFound:
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    newPos = blockPos.offset(x, 0, z);
                    if (matcher.test(world.getBlockState(newPos))) {
                        break blockFound;
                    }
                    newPos = null;
                }
            }

            if (newPos != null) {
                if (!matcher.test(world.getBlockState(newPos.below()))) {
                    newPos = newPos.above();
                }
                return placeNest(world, newPos, targetBlockState.state);
            }
        }
        return false;
    }
}
