package cy.jdkdigital.productivebees.gen.feature;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.material.Material;

import java.util.Set;
import java.util.function.BiConsumer;

public class DecoratedHugeFungusFeature extends Feature<DecoratedHugeFungusConfiguration> {
    public DecoratedHugeFungusFeature(Codec<DecoratedHugeFungusConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<DecoratedHugeFungusConfiguration> pContext) {
        WorldGenLevel worldgenlevel = pContext.level();
        BlockPos blockpos = pContext.origin();
        RandomSource random = pContext.random();
        ChunkGenerator chunkgenerator = pContext.chunkGenerator();
        DecoratedHugeFungusConfiguration configuration = pContext.config();
        Block block = configuration.validBaseState.getBlock();
        BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());

        if (blockstate.is(block)) {
            int i = Mth.nextInt(random, 4, 13);
            if (random.nextInt(12) == 0) {
                i *= 2;
            }

            if (!configuration.planted) {
                int j = chunkgenerator.getGenDepth();
                if (blockpos.getY() + i + 1 >= j) {
                    return false;
                }
            }

            boolean flag = !configuration.planted && random.nextFloat() < 0.06F;
            worldgenlevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 4);

            Set<BlockPos> logPositions = Sets.newHashSet();
            Set<BlockPos> leafPositions = Sets.newHashSet();

            this.placeStem(worldgenlevel, random, configuration, blockpos, i, flag, logPositions);
            this.placeHat(worldgenlevel, random, configuration, blockpos, i, flag, leafPositions);

            if (!configuration.decorators.isEmpty()) {
                BiConsumer<BlockPos, BlockState> biconsumer = (pos, state) -> {
                    worldgenlevel.setBlock(pos, state, 19);
                };

                configuration.decorators.forEach((decorator) -> {
                    if (decorator instanceof NetherBeehiveDecorator) {
                        ((NetherBeehiveDecorator) decorator).setNest(configuration.nestState);
                    }
                    TreeDecorator.Context context = new TreeDecorator.Context(worldgenlevel, biconsumer, random, logPositions, leafPositions, Sets.newHashSet());
                    decorator.place(context);
                });
            }
            return true;
        }
        return false;
    }

    private static boolean isReplaceable(LevelAccessor level, BlockPos pos, boolean replacePlant) {
        return level.isStateAtPosition(pos, (state) -> {
            Material material = state.getMaterial();
            return state.getMaterial().isReplaceable() || replacePlant && material == Material.PLANT;
        });
    }

    private void placeStem(LevelAccessor level, RandomSource random, HugeFungusConfiguration config, BlockPos pos, int n, boolean planted, Set<BlockPos> logPositions) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockState blockstate = config.stemState;
        int i = planted ? 1 : 0;

        for(int j = -i; j <= i; ++j) {
            for(int k = -i; k <= i; ++k) {
                boolean flag = planted && Mth.abs(j) == i && Mth.abs(k) == i;

                for(int l = 0; l < n; ++l) {
                    blockpos$mutableblockpos.setWithOffset(pos, j, l, k);
                    if (isReplaceable(level, blockpos$mutableblockpos, true)) {
                        if (config.planted) {
                            if (!level.getBlockState(blockpos$mutableblockpos.below()).isAir()) {
                                level.destroyBlock(blockpos$mutableblockpos, true);
                            }

                            level.setBlock(blockpos$mutableblockpos, blockstate, 3);
                        } else if (flag) {
                            if (random.nextFloat() < 0.1F) {
                                this.setBlock(level, blockpos$mutableblockpos, blockstate);
                            }
                        } else {
                            this.setBlock(level, blockpos$mutableblockpos, blockstate);
                        }
                        logPositions.add(new BlockPos(blockpos$mutableblockpos));
                    }
                }
            }
        }

    }

    private void placeHat(LevelAccessor level, RandomSource random, HugeFungusConfiguration config, BlockPos pos, int n, boolean planted, Set<BlockPos> leavesPositions) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        boolean flag = config.hatState.is(Blocks.NETHER_WART_BLOCK);
        int i = Math.min(random.nextInt(1 + n / 3) + 5, n);
        int j = n - i;

        for(int k = j; k <= n; ++k) {
            int l = k < n - random.nextInt(3) ? 2 : 1;
            if (i > 8 && k < j + 4) {
                l = 3;
            }

            if (planted) {
                ++l;
            }

            for(int i1 = -l; i1 <= l; ++i1) {
                for(int j1 = -l; j1 <= l; ++j1) {
                    boolean flag1 = i1 == -l || i1 == l;
                    boolean flag2 = j1 == -l || j1 == l;
                    boolean flag3 = !flag1 && !flag2 && k != n;
                    boolean flag4 = flag1 && flag2;
                    boolean flag5 = k < j + 3;
                    blockpos$mutableblockpos.setWithOffset(pos, i1, k, j1);
                    if (isReplaceable(level, blockpos$mutableblockpos, false)) {
                        if (config.planted && !level.getBlockState(blockpos$mutableblockpos.below()).isAir()) {
                            level.destroyBlock(blockpos$mutableblockpos, true);
                        }

                        if (flag5) {
                            if (!flag3) {
                                this.placeHatDropBlock(level, random, blockpos$mutableblockpos, config.hatState, flag);
                            }
                        } else if (flag3) {
                            this.placeHatBlock(level, random, config, blockpos$mutableblockpos, 0.1F, 0.2F, flag ? 0.1F : 0.0F);
                        } else if (flag4) {
                            this.placeHatBlock(level, random, config, blockpos$mutableblockpos, 0.01F, 0.7F, flag ? 0.083F : 0.0F);
                        } else {
                            this.placeHatBlock(level, random, config, blockpos$mutableblockpos, 5.0E-4F, 0.98F, flag ? 0.07F : 0.0F);
                        }
                        leavesPositions.add(new BlockPos(blockpos$mutableblockpos));
                    }
                }
            }
        }

    }

    private void placeHatBlock(LevelAccessor level, RandomSource random, HugeFungusConfiguration config, BlockPos.MutableBlockPos pos, float decorChance, float hatChance, float vieChance) {
        if (random.nextFloat() < decorChance) {
            this.setBlock(level, pos, config.decorState);
        } else if (random.nextFloat() < hatChance) {
            this.setBlock(level, pos, config.hatState);
            if (random.nextFloat() < vieChance) {
                tryPlaceWeepingVines(pos, level, random);
            }
        }
    }

    private void placeHatDropBlock(LevelAccessor level, RandomSource random, BlockPos pos, BlockState state, boolean placeVines) {
        if (level.getBlockState(pos.below()).is(state.getBlock())) {
            this.setBlock(level, pos, state);
        } else if ((double)random.nextFloat() < 0.15D) {
            this.setBlock(level, pos, state);
            if (placeVines && random.nextInt(11) == 0) {
                tryPlaceWeepingVines(pos, level, random);
            }
        }
    }

    private static void tryPlaceWeepingVines(BlockPos pos, LevelAccessor level, RandomSource random) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable().move(Direction.DOWN);
        if (level.isEmptyBlock(blockpos$mutableblockpos)) {
            int i = Mth.nextInt(random, 1, 5);
            if (random.nextInt(7) == 0) {
                i *= 2;
            }
            WeepingVinesFeature.placeWeepingVinesColumn(level, random, blockpos$mutableblockpos, i, 23, 25);
        }
    }
}
