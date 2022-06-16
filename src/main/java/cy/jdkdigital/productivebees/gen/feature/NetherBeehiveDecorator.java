package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetherBeehiveDecorator extends TreeDecorator {
    public static NetherBeehiveDecorator INSTANCE = new NetherBeehiveDecorator(0.02F);

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
    public void place(Context context) {
        if (!(context.random().nextFloat() >= this.probability) && nest != null) {
            if (context.leaves().isEmpty() || context.logs().isEmpty()) {
                return;
            }

            int i = Math.max(context.leaves().get(0).getY() - 1, context.logs().get(0).getY() + 1);

            List<BlockPos> list = context.logs().stream().filter((pos) -> pos.getY() == i).flatMap((pos) -> Stream.of(SPAWN_DIRECTIONS).map(pos::relative)).collect(Collectors.toList());
            if (!list.isEmpty()) {
                Collections.shuffle(list);
                Optional<BlockPos> optional = list.stream().filter((pos) -> context.isAir(pos) && context.isAir(pos.relative(WORLDGEN_FACING))).findFirst();
                if (optional.isPresent()) {
                    // Find log position
                    Direction facing = WORLDGEN_FACING;
                    for (Direction d: Direction.Plane.HORIZONTAL) {
                        if (context.logs().contains(optional.get().relative(d))) {
                            facing = d.getOpposite();
                            break;
                        }
                    }

                    context.setBlock(optional.get(), nest.setValue(BeehiveBlock.FACING, facing));
                    context.level().getBlockEntity(optional.get(), ModBlockEntityTypes.NETHER_BEE_NEST.get()).ifPresent((blockEntity) -> {
                        int j = 2 + context.random().nextInt(2);

                        for(int k = 0; k < j; ++k) {
                            EntityType<ConfigurableBee> beeType = ModEntities.CONFIGURABLE_BEE.get();
                            ConfigurableBee newBee = beeType.create(ProductiveBees.proxy.getWorld());
                            if (newBee != null) {
                                newBee.setBeeType(ForgeRegistries.BLOCKS.getKey(nest.getBlock()).getPath().equals("warped_bee_nest") ? "productivebees:warped" : "productivebees:crimson");
                                newBee.setAttributes();
                                newBee.hivePos = optional.get();

                                blockEntity.addOccupant(newBee, false);
                            }
                        }
                    });
                }
            }
        }
    }
}