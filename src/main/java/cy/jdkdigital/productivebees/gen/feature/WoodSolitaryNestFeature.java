package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import javax.annotation.Nonnull;
import java.util.Random;

public class WoodSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;

    public WoodSolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory) {
        super(probability, configFactory, false);
        this.probability = probability;
    }

    @Override
    public boolean func_241855_a(@Nonnull ISeedReader world, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get to ground level
        blockPos = blockPos.up(chunkGenerator.getGroundHeight());

        // Go to ground surface
        while (blockPos.getY() < 127 && !world.isAirBlock(blockPos)) {
            blockPos = blockPos.up();
        }
        // Go up some more
        blockPos = blockPos.up(rand.nextInt(4));

        // Locate tree log in chunk
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());

        BlockPos newPos = null;
        blockFound:
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                newPos = blockPos.add(x, 0, z);
                if (matcher.test(world.getBlockState(newPos))) {
                    break blockFound;
                }
                newPos = null;
            }
        }

        if (newPos != null) {
            // For thicc trees, we need to move the nest to the outside of the tree
            while (matcher.test(world.getBlockState(newPos.east(1)))) {
                newPos = newPos.east(1);
            }

            return placeNest(world, newPos, featureConfig);
        }
        return false;
    }
}
