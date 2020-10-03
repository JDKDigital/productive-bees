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

public class CavernSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private boolean top;

    public CavernSolitaryNestFeature(float probability, Function<Dynamic<?>, ? extends ReplaceBlockConfig> configFactory, boolean top) {
        super(probability, configFactory);
        this.probability = probability;
        this.top = top;
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        pos = pos.south(rand.nextInt(14)).east(rand.nextInt(14));

        // Go to roof
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        while (pos.getY() < 127 && !matcher.test(world.getBlockState(pos))) {
            pos = pos.up();
        }

        if (top) {
            // Go to surface
            while (pos.getY() < 127 || !world.isAirBlock(pos)) {
                pos = pos.up();
            }
            pos = pos.down();
        }

        return placeNest(world, pos, featureConfig);
    }
}
