package cy.jdkdigital.productivebees.gen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

public class SolitaryNestTreeFeature extends TreeFeature
{
    public SolitaryNestTreeFeature(Codec<TreeConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> pContext) {
        WorldGenLevel worldgenlevel = pContext.level();
        Random random = pContext.random();
        BlockPos blockpos = pContext.origin();
        TreeConfiguration treeconfiguration = pContext.config();
        Set<BlockPos> set = Sets.newHashSet();
        Set<BlockPos> set1 = Sets.newHashSet();
        Set<BlockPos> set2 = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> logConsumer = (pos, blockState) -> {
            set.add(pos.immutable());
            worldgenlevel.setBlock(pos, blockState, 19);
        };
        BiConsumer<BlockPos, BlockState> leafConsumer = (pos, blockState) -> {
            set1.add(pos.immutable());
            worldgenlevel.setBlock(pos, blockState, 19);
        };
        BiConsumer<BlockPos, BlockState> decorationConsumer = (pos, blockState) -> {
            set2.add(pos.immutable());
            worldgenlevel.setBlock(pos, blockState, 19);
        };
        boolean flag = this.doPlace(worldgenlevel, random, blockpos, logConsumer, leafConsumer, treeconfiguration);
        if (flag && (!set.isEmpty() || !set1.isEmpty())) {
            if (!treeconfiguration.decorators.isEmpty()) {
                List<BlockPos> logPositions = Lists.newArrayList(set);
                List<BlockPos> leafPositions = Lists.newArrayList(set1);
                logPositions.sort(Comparator.comparingInt(Vec3i::getY));
                leafPositions.sort(Comparator.comparingInt(Vec3i::getY));
                treeconfiguration.decorators.forEach((decorator) -> {
                    if (decorator instanceof WoodNestDecorator woodNestDecorator) {
                        BlockState logBlock = worldgenlevel.getBlockState(logPositions.get(0));
                        if (logBlock.getBlock().equals(Blocks.DIRT)) {
                            logBlock = worldgenlevel.getBlockState(logPositions.get(0).above());
                        }
                        Block nest = SolitaryNest.BLOCK_TO_NEST.get().get(logBlock.getBlock());
                        if (nest instanceof WoodNest woodNest) {
                            woodNestDecorator.setNest(woodNest.defaultBlockState());
                            woodNestDecorator.setBeeRecipes(woodNest.getSpawningRecipes(worldgenlevel.getLevel(), worldgenlevel.getBiome(blockpos).value()));
                        } else {
                            woodNestDecorator.setNest(null); // reset so next tree does not inherit
                        }
                    }
                    decorator.place(worldgenlevel, decorationConsumer, random, logPositions, leafPositions);
                });
            }

            return BoundingBox.encapsulatingPositions(Iterables.concat(set, set1, set2)).map((boundingBox) -> {
                DiscreteVoxelShape discretevoxelshape = TreeFeature.updateLeaves(worldgenlevel, boundingBox, set, set2);
                StructureTemplate.updateShapeAtEdge(worldgenlevel, 3, discretevoxelshape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }
}
