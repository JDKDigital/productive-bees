package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OreSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private int yMin;
    private int yMax;

    public OreSolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory) {
        this(probability, configFactory, 0, 64);
    }

    public OreSolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory, int yMin, int yMax) {
        super(probability, configFactory);
        this.probability = probability;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel world = context.level();
        Random rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (nestShouldNotGenerate(targetBlockState.state) || rand.nextFloat() > this.probability) {
                return false;
            }

            // Get random block in chunk
            blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

            // Go to yMin
            blockPos = blockPos.above(yMin);

            BlockStatePredicate matcher = BlockStatePredicate.forBlock(targetBlockState.state.getBlock());
            while (blockPos.getY() < yMax) {
                blockPos = blockPos.above(2);
                if (matcher.test(world.getBlockState(blockPos))) {
                    // Find air
                    int d = 3;
                    List<BlockPos> blockList = BlockPos.betweenClosedStream(blockPos.offset(-d, -d, -d), blockPos.offset(d, d, d)).map(BlockPos::immutable).collect(Collectors.toList());
                    for (BlockPos pos : blockList) {
                        if (world.isEmptyBlock(pos)) {
                            // Find block around that air pos
                            List<BlockPos> aroundAir = BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
                            for (BlockPos airPos : aroundAir) {
                                if (matcher.test(world.getBlockState(airPos))) {
                                    placeNest(world, blockPos, targetBlockState.state);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
