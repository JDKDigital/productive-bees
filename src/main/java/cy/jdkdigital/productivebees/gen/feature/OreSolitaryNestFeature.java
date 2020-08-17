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

public class OreSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private int yMin;
    private int yMax;

    public OreSolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory) {
        this(probability, configFactory, 0, 64);
    }

    public OreSolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory, int yMin, int yMax) {
        super(probability, configFactory);
        this.probability = probability;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    @Override
    public boolean func_230362_a_(@Nonnull ISeedReader world, @Nonnull StructureManager structureManager, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

        // Go to yMin
        blockPos = blockPos.up(yMin);

        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        while (blockPos.getY() < yMax) {
            blockPos = blockPos.up();
            if (matcher.test(world.getBlockState(blockPos))) {
                placeNest(world, blockPos, featureConfig);
            }
        }

        return true;
    }
}
