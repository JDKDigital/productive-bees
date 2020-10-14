package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

        // Go to yMin
        blockPos = blockPos.up(yMin);

        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        while (blockPos.getY() < yMax) {
            blockPos = blockPos.up(2);
            if (matcher.test(world.getBlockState(blockPos))) {
                // Find air
                int d = 3;
                List<BlockPos> blockList = BlockPos.getAllInBox(blockPos.add(-d, -d, -d), blockPos.add(d, d, d)).map(BlockPos::toImmutable).collect(Collectors.toList());
                for (BlockPos pos: blockList) {
                    if (world.isAirBlock(pos)) {
                        // Find block around that air pos
                        List<BlockPos> aroundAir = BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
                        for (BlockPos airPos: aroundAir) {
                            if (matcher.test(world.getBlockState(airPos))) {
                                placeNest(world, blockPos, featureConfig);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
