package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OreSolitaryNestFeature extends SolitaryNestFeature
{
    private final float probability;
    private int yMin;
    private int yMax;

    public OreSolitaryNestFeature(float probability, Function<Dynamic<?>, ? extends ReplaceBlockConfig> configFactory) {
        this(probability, configFactory, 0, 64);
    }

    public OreSolitaryNestFeature(float probability, Function<Dynamic<?>, ? extends ReplaceBlockConfig> configFactory, int yMin, int yMax) {
        super(probability, configFactory);
        this.probability = probability;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        pos = pos.south(rand.nextInt(14)).east(rand.nextInt(14));

        // Go to yMin
        pos = pos.up(yMin);

        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        while (pos.getY() < yMax) {
            pos = pos.up(2);
            if (matcher.test(world.getBlockState(pos))) {
                // Find air
                int d = 3;
                List<BlockPos> blockList = BlockPos.getAllInBox(pos.add(-d, -d, -d), pos.add(d, d, d)).map(BlockPos::toImmutable).collect(Collectors.toList());
                for (BlockPos blockPos: blockList) {
                    if (world.isAirBlock(blockPos)) {
                        // Find block around that air pos
                        List<BlockPos> aroundAir = BlockPos.getAllInBox(blockPos.add(-1, -1, -1), pos.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
                        for (BlockPos airPos: aroundAir) {
                            if (matcher.test(world.getBlockState(airPos))) {
                                placeNest(world, pos, featureConfig);
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
