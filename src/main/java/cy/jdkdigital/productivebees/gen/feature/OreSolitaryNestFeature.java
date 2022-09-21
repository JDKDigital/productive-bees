package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class OreSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private final int yMin;
    private final int yMax;

    public OreSolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory, int yMin, int yMax) {
        super(probability, configFactory);
        this.probability = probability;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (rand.nextFloat() > this.probability) {
                return false;
            }

            // Get random block in chunk
            blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

            // Go to yMin
            blockPos = blockPos.above(yMin);

            while (blockPos.getY() < yMax) {
                blockPos = blockPos.above(2);
                if (targetBlockState.target.test(world.getBlockState(blockPos), rand)) {
                    return placeNest(world, blockPos, targetBlockState.state, rand);
                }
            }
        }

        return true;
    }
}
