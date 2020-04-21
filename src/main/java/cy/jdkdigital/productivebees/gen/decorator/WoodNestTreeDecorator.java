package cy.jdkdigital.productivebees.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import cy.jdkdigital.productivebees.block.nest.WoodNest;
import cy.jdkdigital.productivebees.tileentity.SolitaryNestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class WoodNestTreeDecorator extends TreeDecorator {
    private WoodNest nest;
    private float probability;
    private Block wood;

    public WoodNestTreeDecorator(WoodNest nest, Block wood, float probability) {
        super(TreeDecoratorType.BEEHIVE);
        this.nest = nest;
        this.wood = wood;
        this.probability = probability;
    }

    public <T> WoodNestTreeDecorator(Dynamic<T> dynamicOps) {
        this(
            (WoodNest) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dynamicOps.get("nest").asString(""))),
            ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dynamicOps.get("wood").asString(""))),
            dynamicOps.get("probability").asFloat(0.0F)
        );
    }

    @Override
    public void func_225576_a_(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list1, Set<BlockPos> set, MutableBoundingBox mutableBoundingBox) {
        if (random.nextFloat() < this.probability) {
            int y = !list1.isEmpty() ? Math.max(list1.get(0).getY() - 1, list.get(0).getY()) : Math.min(list.get(0).getY() + 1 + random.nextInt(3), list.get(list.size() - 1).getY());
            List<BlockPos> blocksAtY = list.stream().filter((pos) -> pos.getY() == y).collect(Collectors.toList());
            if (!blocksAtY.isEmpty()) {
                BlockPos trunkPos = blocksAtY.get(random.nextInt(blocksAtY.size()));
                if (isWood(iWorld, trunkPos)) {
                    BlockState nestState = this.nest.getDefaultState();
                    this.func_227423_a_(iWorld, trunkPos, nestState, set, mutableBoundingBox);
                    TileEntity tileEntity = iWorld.getTileEntity(trunkPos);
                    if (tileEntity instanceof SolitaryNestTileEntity) {
                        SolitaryNestTileEntity nestTileEntity = (SolitaryNestTileEntity) tileEntity;

                        BeeEntity bee = this.nest.getNestingBeeType(iWorld.getWorld()).create(iWorld.getWorld());
                        nestTileEntity.tryEnterHive(bee, false, random.nextInt(599));
                    }

                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (new Dynamic<T>(
                dynamicOps,
                dynamicOps.createMap(
                        ImmutableMap.of(
                                dynamicOps.createString("type"),
                                dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getKey(this.field_227422_a_).toString()),
                                dynamicOps.createString("nest"),
                                dynamicOps.createString(this.nest.getTranslationKey()),
                                dynamicOps.createString("wood"),
                                dynamicOps.createString(this.wood.getTranslationKey()),
                                dynamicOps.createString("probability"),
                                dynamicOps.createFloat(this.probability)
                        )
                )
        )).getValue();
    }

    protected boolean isWood(IWorldGenerationBaseReader worldIn, BlockPos pos) {
        return worldIn.hasBlockState(pos, (state) -> {
            Block block = state.getBlock();
            return block == this.wood;
        });
    }
}
