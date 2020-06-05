package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import java.util.Random;
import java.util.function.Function;

public class WoodSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;

    public WoodSolitaryNestFeature(float probability, Function<Dynamic<?>, ? extends ReplaceBlockConfig> configFactory) {
        super(probability, configFactory, false);
        this.probability = probability;
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get to ground level
        pos = pos.up(generator.getGroundHeight());

        // Go to ground surface
        while (pos.getY() < 127 && !world.isAirBlock(pos)) {
            pos = pos.up();
        }
        // Go up some more
        pos = pos.up(rand.nextInt(4));

        // Locate tree log in chunk
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());

        BlockPos newPos = null;
        blockFound:
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                newPos = pos.add(x, 0, z);
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
