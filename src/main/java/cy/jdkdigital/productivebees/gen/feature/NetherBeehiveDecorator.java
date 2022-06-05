package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModConfiguredFeatures;
import cy.jdkdigital.productivebees.init.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetherBeehiveDecorator extends TreeDecorator {
    public static final Codec<NetherBeehiveDecorator> CODEC = RecordCodecBuilder
            .create((configurationInstance) -> configurationInstance.group(
                            Codec.FLOAT.fieldOf("probability").orElse(0f).forGetter((configuration) -> configuration.probability)
                    )
                    .apply(configurationInstance, NetherBeehiveDecorator::new));

    private static final Direction WORLDGEN_FACING = Direction.SOUTH;
    private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().filter((direction) -> direction != WORLDGEN_FACING.getOpposite()).toArray((i) -> new Direction[i]);

    /** Probability to generate a beehive */
    public final float probability;

    private BlockState nest;

    public NetherBeehiveDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.NETHER_BEEHIVE.get();
    }

    public void setNest(BlockState nest) {
        this.nest = nest;
    }

    @Override
    public void place(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, Random pRandom, List<BlockPos> pLogPositions, List<BlockPos> pLeafPositions) {
        ProductiveBees.LOGGER.info("Look mom, my decorator was called " + nest);
        if (!(pRandom.nextFloat() >= this.probability) && nest != null) {
            int i = !pLeafPositions.isEmpty() ? Math.max(pLeafPositions.get(0).getY() - 1, pLogPositions.get(0).getY() + 1) : Math.min(pLogPositions.get(0).getY() + 1 + pRandom.nextInt(3), pLogPositions.get(pLogPositions.size() - 1).getY());
            List<BlockPos> list = pLogPositions.stream().filter((pos) -> pos.getY() == i).flatMap((pos) -> Stream.of(SPAWN_DIRECTIONS).map(pos::relative)).collect(Collectors.toList());
            if (!list.isEmpty()) {
                Collections.shuffle(list);
                Optional<BlockPos> optional = list.stream().filter((pos) -> Feature.isAir(pLevel, pos) && Feature.isAir(pLevel, pos.relative(WORLDGEN_FACING))).findFirst();
                if (optional.isPresent()) {
                    pBlockSetter.accept(optional.get(), nest.setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
                    pLevel.getBlockEntity(optional.get(), BlockEntityType.BEEHIVE).ifPresent((blockEntity) -> {
                        int j = 2 + pRandom.nextInt(2);

                        for(int k = 0; k < j; ++k) {
                            CompoundTag compoundtag = new CompoundTag();
                            compoundtag.putString("id", Registry.ENTITY_TYPE.getKey(EntityType.BEE).toString());
                            blockEntity.storeBee(compoundtag, pRandom.nextInt(599), false);
                        }
                    });
                }
            }
        }
    }
}
