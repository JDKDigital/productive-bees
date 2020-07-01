package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import javax.annotation.Nonnull;
import java.util.Random;

public class CavernSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;

    public CavernSolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory) {
        super(probability, configFactory);
        this.probability = probability;
    }

    @Override
    public boolean func_230362_a_(@Nonnull ISeedReader world, @Nonnull StructureManager structureManager, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

        // Go to roof
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        while (blockPos.getY() < 127 && !matcher.test(world.getBlockState(blockPos))) {
            blockPos = blockPos.up();
        }

        return placeNest(world, blockPos, featureConfig);
    }
}
