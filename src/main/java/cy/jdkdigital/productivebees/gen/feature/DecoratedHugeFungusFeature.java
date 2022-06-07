package cy.jdkdigital.productivebees.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.material.Material;

import java.util.*;
import java.util.function.BiConsumer;

public class DecoratedHugeFungusFeature extends Feature<DecoratedHugeFungusConfiguration> {
    public DecoratedHugeFungusFeature(Codec<DecoratedHugeFungusConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<DecoratedHugeFungusConfiguration> pContext) {
        WorldGenLevel worldgenlevel = pContext.level();
        BlockPos blockpos = pContext.origin();
        Random random = pContext.random();
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

            List<BlockPos> pLogPositions = new ArrayList<>();
            List<BlockPos> pLeafPositions = new ArrayList<>();

            this.placeStem(worldgenlevel, random, configuration, blockpos, i, flag, pLogPositions);
            this.placeHat(worldgenlevel, random, configuration, blockpos, i, flag, pLeafPositions);

            if (!configuration.decorators.isEmpty()) {
                BiConsumer<BlockPos, BlockState> biconsumer = (pos, state) -> {
                    worldgenlevel.setBlock(pos, state, 19);
                };

                pLogPositions.sort(Comparator.comparingInt(Vec3i::getY));
                pLeafPositions.sort(Comparator.comparingInt(Vec3i::getY));
                configuration.decorators.forEach((decorator) -> {
                    if (decorator instanceof NetherBeehiveDecorator) {
                        ((NetherBeehiveDecorator) decorator).setNest(configuration.nestState);
                    }
                    decorator.place(worldgenlevel, biconsumer, random, pLogPositions, pLeafPositions);
                });
            }
            return true;
        }
        return false;
    }

    private static boolean isReplaceable(LevelAccessor pLevel, BlockPos pPos, boolean pReplacePlants) {
        return pLevel.isStateAtPosition(pPos, (state) -> state.getMaterial().isReplaceable() || pReplacePlants && state.getMaterial() == Material.PLANT);
    }

    private void placeStem(LevelAccessor pLevel, Random pRandom, HugeFungusConfiguration pConfig, BlockPos pPos, int pHeight, boolean pDoubleWide, List<BlockPos> positions) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockState blockstate = pConfig.stemState;
        int i = pDoubleWide ? 1 : 0;

        for(int j = -i; j <= i; ++j) {
            for(int k = -i; k <= i; ++k) {
                boolean flag = pDoubleWide && Mth.abs(j) == i && Mth.abs(k) == i;

                for(int l = 0; l < pHeight; ++l) {
                    blockpos$mutableblockpos.setWithOffset(pPos, j, l, k);
                    if (isReplaceable(pLevel, blockpos$mutableblockpos, true)) {
                        positions.add(blockpos$mutableblockpos.immutable());
                        if (pConfig.planted) {
                            if (!pLevel.getBlockState(blockpos$mutableblockpos.below()).isAir()) {
                                pLevel.destroyBlock(blockpos$mutableblockpos, true);
                            }

                            pLevel.setBlock(blockpos$mutableblockpos, blockstate, 3);
                        } else if (flag) {
                            if (pRandom.nextFloat() < 0.1F) {
                                this.setBlock(pLevel, blockpos$mutableblockpos, blockstate);
                            }
                        } else {
                            this.setBlock(pLevel, blockpos$mutableblockpos, blockstate);
                        }
                    }
                }
            }
        }

    }

    private void placeHat(LevelAccessor pLevel, Random pRandom, HugeFungusConfiguration pConfig, BlockPos pPos, int pHeight, boolean pDoubleWide, List<BlockPos> positions) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        boolean flag = pConfig.hatState.is(Blocks.NETHER_WART_BLOCK);
        int i = Math.min(pRandom.nextInt(1 + pHeight / 3) + 5, pHeight);
        int j = pHeight - i;

        for(int k = j; k <= pHeight; ++k) {
            int l = k < pHeight - pRandom.nextInt(3) ? 2 : 1;
            if (i > 8 && k < j + 4) {
                l = 3;
            }

            if (pDoubleWide) {
                ++l;
            }

            for(int i1 = -l; i1 <= l; ++i1) {
                for(int j1 = -l; j1 <= l; ++j1) {
                    boolean flag1 = i1 == -l || i1 == l;
                    boolean flag2 = j1 == -l || j1 == l;
                    boolean flag3 = !flag1 && !flag2 && k != pHeight;
                    boolean flag4 = flag1 && flag2;
                    boolean flag5 = k < j + 3;
                    blockpos$mutableblockpos.setWithOffset(pPos, i1, k, j1);
                    if (isReplaceable(pLevel, blockpos$mutableblockpos, false)) {
                        positions.add(blockpos$mutableblockpos.immutable());
                        if (pConfig.planted && !pLevel.getBlockState(blockpos$mutableblockpos.below()).isAir()) {
                            pLevel.destroyBlock(blockpos$mutableblockpos, true);
                        }

                        if (flag5) {
                            if (!flag3) {
                                this.placeHatDropBlock(pLevel, pRandom, blockpos$mutableblockpos, pConfig.hatState, flag);
                            }
                        } else if (flag3) {
                            this.placeHatBlock(pLevel, pRandom, pConfig, blockpos$mutableblockpos, 0.1F, 0.2F, flag ? 0.1F : 0.0F);
                        } else if (flag4) {
                            this.placeHatBlock(pLevel, pRandom, pConfig, blockpos$mutableblockpos, 0.01F, 0.7F, flag ? 0.083F : 0.0F);
                        } else {
                            this.placeHatBlock(pLevel, pRandom, pConfig, blockpos$mutableblockpos, 5.0E-4F, 0.98F, flag ? 0.07F : 0.0F);
                        }
                    }
                }
            }
        }

    }

    private void placeHatBlock(LevelAccessor pLevel, Random pRandom, HugeFungusConfiguration pConfig, BlockPos.MutableBlockPos pPos, float pDecorationChance, float pHatChance, float pWeepingVineChance) {
        if (pRandom.nextFloat() < pDecorationChance) {
            this.setBlock(pLevel, pPos, pConfig.decorState);
        } else if (pRandom.nextFloat() < pHatChance) {
            this.setBlock(pLevel, pPos, pConfig.hatState);
            if (pRandom.nextFloat() < pWeepingVineChance) {
                tryPlaceWeepingVines(pPos, pLevel, pRandom);
            }
        }

    }

    private void placeHatDropBlock(LevelAccessor pLevel, Random pRandom, BlockPos pPos, BlockState pState, boolean pWeepingVines) {
        if (pLevel.getBlockState(pPos.below()).is(pState.getBlock())) {
            this.setBlock(pLevel, pPos, pState);
        } else if ((double)pRandom.nextFloat() < 0.15D) {
            this.setBlock(pLevel, pPos, pState);
            if (pWeepingVines && pRandom.nextInt(11) == 0) {
                tryPlaceWeepingVines(pPos, pLevel, pRandom);
            }
        }

    }

    private static void tryPlaceWeepingVines(BlockPos pPos, LevelAccessor pLevel, Random pRandom) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable().move(Direction.DOWN);
        if (pLevel.isEmptyBlock(blockpos$mutableblockpos)) {
            int i = Mth.nextInt(pRandom, 1, 5);
            if (pRandom.nextInt(7) == 0) {
                i *= 2;
            }

            int j = 23;
            int k = 25;
            WeepingVinesFeature.placeWeepingVinesColumn(pLevel, pRandom, blockpos$mutableblockpos, i, 23, 25);
        }
    }
}
