package cy.jdkdigital.productivebees.gen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
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
    public final boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();
        BlockPos blockpos = context.origin();
        TreeConfiguration treeconfiguration = context.config();
        Set<BlockPos> rootPositions = Sets.newHashSet();
        Set<BlockPos> logPositions = Sets.newHashSet();
        Set<BlockPos> leavesPositions = Sets.newHashSet();
        Set<BlockPos> set3 = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> rootPlacer = (pos, state) -> {
            rootPositions.add(pos.immutable());
            worldgenlevel.setBlock(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> trunkPlacer = (pos, state) -> {
            logPositions.add(pos.immutable());
            worldgenlevel.setBlock(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> leavesPlacer = (pos, state) -> {
            leavesPositions.add(pos.immutable());
            worldgenlevel.setBlock(pos, state, 19);
        };
        FoliagePlacer.FoliageSetter foliageSetter = new FoliagePlacer.FoliageSetter() {
            public void set(BlockPos pos, BlockState state) {
                leavesPositions.add(pos.immutable());
                worldgenlevel.setBlock(pos, state, 19);
            }

            public boolean isSet(BlockPos pos) {
                return leavesPositions.contains(pos);
            }
        };
        BiConsumer<BlockPos, BlockState> decorationPlacer = (pos, state) -> {
            set3.add(pos.immutable());
            worldgenlevel.setBlock(pos, state, 19);
        };
        boolean flag = this.doPlace(worldgenlevel, randomsource, blockpos, rootPlacer, trunkPlacer, foliageSetter, treeconfiguration);
        if (flag && (!logPositions.isEmpty() || !leavesPositions.isEmpty())) {
            if (!treeconfiguration.decorators.isEmpty()) {
                TreeDecorator.Context decoratorContext = new TreeDecorator.Context(worldgenlevel, decorationPlacer, randomsource, logPositions, leavesPositions, rootPositions);
                treeconfiguration.decorators.forEach((decorator) -> {
                    if (decorator instanceof WoodNestDecorator woodNestDecorator) {
                        List<BlockPos> logList = logPositions.stream().toList();
                        BlockState logBlock = worldgenlevel.getBlockState(logList.get(0));
                        if (logBlock.getBlock().equals(Blocks.DIRT)) {
                            logBlock = worldgenlevel.getBlockState(logList.get(0).above());
                        }
                        Block nest = SolitaryNest.BLOCK_TO_NEST.get().get(logBlock.getBlock());
                        if (nest instanceof WoodNest woodNest) {
                            woodNestDecorator.setNest(woodNest.defaultBlockState());
                            woodNestDecorator.setBeeRecipes(woodNest.getSpawningRecipes(worldgenlevel.getLevel(), worldgenlevel.getBiome(blockpos).value()));
                        } else {
                            woodNestDecorator.setNest(null); // reset so next tree does not inherit
                        }
                    }
                    decorator.place(decoratorContext);
                });
            }

            return BoundingBox.encapsulatingPositions(Iterables.concat(rootPositions, logPositions, leavesPositions, set3)).map((boundingBox) -> {
                DiscreteVoxelShape discretevoxelshape = TreeFeature.updateLeaves(worldgenlevel, boundingBox, logPositions, set3, rootPositions);
                StructureTemplate.updateShapeAtEdge(worldgenlevel, 3, discretevoxelshape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }
}
