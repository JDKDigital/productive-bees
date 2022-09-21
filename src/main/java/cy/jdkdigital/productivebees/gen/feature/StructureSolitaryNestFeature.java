package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class StructureSolitaryNestFeature extends SolitaryNestFeature
{
    private final int offsetSpan;

    public StructureSolitaryNestFeature(String probability, Codec<ReplaceBlockConfiguration> configFactory, int offsetSpan) {
        super(probability, configFactory);
        this.offsetSpan = offsetSpan;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (rand.nextFloat() > ProductiveBeesConfig.WORLD_GEN.nestConfigs.get(configKey).get().floatValue()) {
                return false;
            }

            // Get random block in chunk
            blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14)).above(50);

            // Go to nearby structure
            nearby:
            if (!targetBlockState.target.test(world.getBlockState(blockPos), rand)) {
                // Skip or look around?
                for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
                    if (targetBlockState.target.test(world.getBlockState(blockPos.relative(dir, 2)), rand)) {
                        blockPos = blockPos.relative(dir, 3);
                        break nearby;
                    }
                }
                return false;
            }

            // Expand up
            blockPos = blockPos.relative(Direction.UP, rand.nextInt(this.offsetSpan));

            // Move to structure edge
            edgeFinding:
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
                int i = 0;
                while (++i <= 5) {
                    if (world.isEmptyBlock(blockPos.relative(dir, i))) {
                        blockPos = blockPos.relative(dir, i - 1);
                        break edgeFinding;
                    }
                }
            }

            BlockState state = placeOntop ? world.getBlockState(blockPos.below()) : world.getBlockState(blockPos);
            if (targetBlockState.target.test(state, rand)) {
                return placeNest(world, blockPos, targetBlockState.state, rand);
            }
        }
        return false;
    }
}
