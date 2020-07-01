package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import javax.annotation.Nonnull;
import java.util.Random;

public class StructureSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private final int offsetSpan;

    public StructureSolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory, int offsetSpan) {
        super(probability, configFactory);
        this.offsetSpan = offsetSpan;
        this.probability = probability;
    }

    @Override
    public boolean func_230362_a_(@Nonnull ISeedReader world, @Nonnull StructureManager structureManager, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14)).up(50);

        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());

        // Go to nearby structure
        nearby:
        if (!matcher.test(world.getBlockState(blockPos))) {
            // Skip or look around?
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
                if (matcher.test(world.getBlockState(blockPos.offset(dir, 2)))) {
                    blockPos = blockPos.offset(dir, 3);
                    break nearby;
                }
            }
            return false;
        }

        // Expand up
        blockPos = blockPos.offset(Direction.UP, world.getRandom().nextInt(this.offsetSpan));

        // Move to structure edge
        edgeFinding:
        for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
            int i = 0;
            while (++i <= 5) {
                if (world.isAirBlock(blockPos.offset(dir, i))) {
                    blockPos = blockPos.offset(dir, i - 1);
                    break edgeFinding;
                }
            }
        }

        return placeNest(world, blockPos, featureConfig);
    }
}
