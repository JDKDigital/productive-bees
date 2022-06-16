package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class WoodSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;

    public WoodSolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory) {
        super(probability, configFactory, false);
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
            if (nestShouldNotGenerate(targetBlockState.state) || rand.nextFloat() > this.probability) {
                return false;
            }

            // Get to ground level
            // TODO better way to end un surface, probably placement modifiers
            blockPos = blockPos.atY(chunkGenerator.getSeaLevel());

            // Go to ground surface
            while (blockPos.getY() < 127 && !world.isEmptyBlock(blockPos)) {
                blockPos = blockPos.above();
            }
            // Go up some more
            blockPos = blockPos.above(rand.nextInt(4));

            // Locate tree log in chunk
            BlockPos newPos = null;
            blockFound:
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    newPos = blockPos.offset(x, 0, z);
                    if (targetBlockState.target.test(world.getBlockState(newPos), world.getRandom())) {
                        break blockFound;
                    }
                    newPos = null;
                }
            }

            if (newPos != null) {
                // For thicc trees, we need to move the nest to the outside of the tree
                while (targetBlockState.target.test(world.getBlockState(newPos.east(1)), world.getRandom())) {
                    newPos = newPos.east(1);
                }

                if (targetBlockState.target.test(world.getBlockState(newPos), world.getRandom())) {
                    return placeNest(world, newPos, targetBlockState.state);
                }
            }
        }
        return false;
    }
}
