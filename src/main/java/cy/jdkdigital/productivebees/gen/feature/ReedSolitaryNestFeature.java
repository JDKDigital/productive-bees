package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import javax.annotation.Nonnull;
import java.util.Random;

public class ReedSolitaryNestFeature extends WoodSolitaryNestFeature
{
    private final float probability;

    public ReedSolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory) {
        super(probability, configFactory);
        this.probability = probability;
    }

    @Override
    public boolean place(@Nonnull ISeedReader world, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get to ground level
        blockPos = blockPos.above(chunkGenerator.getSpawnHeight());

        // Go to ground surface
        while (blockPos.getY() < 127 && !world.isEmptyBlock(blockPos)) {
            blockPos = blockPos.above();
        }

        // Go up some more
        blockPos = blockPos.above(rand.nextInt(2));

        // Locate tree log in chunk
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());

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
            return placeNest(world, newPos, featureConfig);
        }
        return false;
    }
}
