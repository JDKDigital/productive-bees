package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

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
    public boolean func_241855_a(@Nonnull ISeedReader world, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (rand.nextFloat() > this.probability) {
            return false;
        }

        // Get to ground level
        blockPos = blockPos.up(chunkGenerator.getGroundHeight());

        // Go to ground surface
        while (blockPos.getY() < 127 && !world.isAirBlock(blockPos)) {
            blockPos = blockPos.up();
        }

        // Go up some more
        blockPos = blockPos.up(rand.nextInt(2));

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
            if (!matcher.test(world.getBlockState(newPos.down()))) {
                newPos = newPos.up();
            }
            return placeNest(world, newPos, featureConfig);
        }
        return false;
    }
}
