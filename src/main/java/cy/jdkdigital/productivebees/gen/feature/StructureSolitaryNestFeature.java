package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import java.util.Random;
import java.util.function.Function;

public class StructureSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private final int offsetSpan;

    public StructureSolitaryNestFeature(float probability, Function<Dynamic<?>, ? extends ReplaceBlockConfig> configFactory, int offsetSpan) {
        super(probability, configFactory);
        this.offsetSpan = offsetSpan;
        this.probability = probability;
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        pos = pos.south(rand.nextInt(14)).east(rand.nextInt(14)).up(50);

        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());

        // Go to nearby structure
        nearby:
        if (!matcher.test(world.getBlockState(pos))) {
            // Skip or look around?
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
                if (matcher.test(world.getBlockState(pos.offset(dir, 2)))) {
                    pos = pos.offset(dir, 3);
                    break nearby;
                }
            }
            return false;
        }

        // Expand up
        pos = pos.offset(Direction.UP, world.getRandom().nextInt(this.offsetSpan));

        // Move to structure edge
        edgeFinding:
        for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
            int i = 0;
            while (++i <= 5) {
                if (world.isAirBlock(pos.offset(dir, i))) {
                    pos = pos.offset(dir, i - 1);
                    break edgeFinding;
                }
            }
        }

        return placeNest(world, pos, featureConfig);
    }
}
