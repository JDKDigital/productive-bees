package cy.jdkdigital.productivebees.gen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class SolitaryNestTreeFeature extends TreeFeature
{
    public SolitaryNestTreeFeature(Codec<TreeConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(TreeConfiguration pConfig, WorldGenLevel pLevel, ChunkGenerator pChunkGenerator, RandomSource pRandom, BlockPos pOrigin) {
        Set<BlockPos> rootPositions = Sets.newHashSet();
        Set<BlockPos> logPositions = Sets.newHashSet();
        Set<BlockPos> leavesPositions = Sets.newHashSet();
        Set<BlockPos> set3 = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> rootPlacer = (pos, state) -> {
            rootPositions.add(pos.immutable());
            pLevel.setBlock(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> trunkPlacer = (pos, state) -> {
            logPositions.add(pos.immutable());
            pLevel.setBlock(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> leavesPlacer = (pos, state) -> {
            leavesPositions.add(pos.immutable());
            pLevel.setBlock(pos, state, 19);
        };
        FoliagePlacer.FoliageSetter foliageSetter = new FoliagePlacer.FoliageSetter() {
            public void set(BlockPos pos, BlockState state) {
                leavesPositions.add(pos.immutable());
                pLevel.setBlock(pos, state, 19);
            }

            public boolean isSet(BlockPos pos) {
                return leavesPositions.contains(pos);
            }
        };
        BiConsumer<BlockPos, BlockState> decorationPlacer = (pos, state) -> {
            set3.add(pos.immutable());
            pLevel.setBlock(pos, state, 19);
        };
        boolean flag = this.doPlace(pLevel, pRandom, pOrigin, rootPlacer, trunkPlacer, foliageSetter, pConfig);
        if (flag && (!logPositions.isEmpty() || !leavesPositions.isEmpty())) {
            if (!pConfig.decorators.isEmpty()) {
                TreeDecorator.Context decoratorContext = new TreeDecorator.Context(pLevel, decorationPlacer, pRandom, logPositions, leavesPositions, rootPositions);
                pConfig.decorators.forEach((decorator) -> {
                    if (decorator instanceof WoodNestDecorator woodNestDecorator) {
                        List<BlockPos> logList = logPositions.stream().toList();
                        BlockState logBlock = pLevel.getBlockState(logList.get(0));
                        if (logBlock.getBlock().equals(Blocks.DIRT)) {
                            logBlock = pLevel.getBlockState(logList.get(0).above());
                        }
                        Block nest = SolitaryNest.BLOCK_TO_NEST.get().get(logBlock.getBlock());
                        if (nest instanceof WoodNest woodNest) {
                            woodNestDecorator.setNest(woodNest.defaultBlockState());
                            woodNestDecorator.setBeeRecipes(SolitaryNest.getSpawningRecipes(woodNest, pLevel.getLevel(), pLevel.getBiome(pOrigin), ItemStack.EMPTY));
                        } else {
                            woodNestDecorator.setNest(null); // reset so next tree does not inherit
                        }
                    }
                    decorator.place(decoratorContext);
                });
            }

            return BoundingBox.encapsulatingPositions(Iterables.concat(rootPositions, logPositions, leavesPositions, set3)).map((boundingBox) -> {
                DiscreteVoxelShape discretevoxelshape = TreeFeature.updateLeaves(pLevel, boundingBox, logPositions, set3, rootPositions);
                StructureTemplate.updateShapeAtEdge(pLevel, 3, discretevoxelshape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }
}
