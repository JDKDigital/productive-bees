package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModFeatures;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.ForgeRegistries;

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

    private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().filter((direction) -> direction != Direction.SOUTH.getOpposite()).toArray(Direction[]::new);

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
    public void place(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, Random random, List<BlockPos> pLogPositions, List<BlockPos> pLeafPositions) {
        if (!(random.nextFloat() >= this.probability) && nest != null) {
            int i = !pLeafPositions.isEmpty() ? Math.max(pLeafPositions.get(0).getY() - 1, pLogPositions.get(0).getY() + 1) : Math.min(pLogPositions.get(0).getY() + 1 + random.nextInt(3), pLogPositions.get(pLogPositions.size() - 1).getY());
            List<BlockPos> list = pLogPositions.stream().filter((pos) -> pos.getY() == i).flatMap((pos) -> Stream.of(SPAWN_DIRECTIONS).map(pos::relative)).collect(Collectors.toList());
            if (!list.isEmpty()) {
                Collections.shuffle(list);
                Optional<BlockPos> optional = list.stream().filter((pos) -> Feature.isAir(pLevel, pos) && Feature.isAir(pLevel, pos.relative(Direction.SOUTH))).findFirst();
                if (optional.isPresent()) {

                    // Find log position
                    Direction facing = Direction.SOUTH;
                    for (Direction d: Direction.Plane.HORIZONTAL) {
                        if (pLogPositions.contains(optional.get().relative(d))) {
                            facing = d.getOpposite();
                            break;
                        }
                    }

                    pBlockSetter.accept(optional.get(), nest.setValue(BeehiveBlock.FACING, facing));
                    pLevel.getBlockEntity(optional.get(), ModTileEntityTypes.NETHER_BEE_NEST.get()).ifPresent((blockEntity) -> {
                        int j = 2 + random.nextInt(2);

                        String type = ForgeRegistries.BLOCKS.getKey(nest.getBlock()).getPath().equals("warped_bee_nest") ? "warped" : "crimson";

                        for(int k = 0; k < j; ++k) {
                            try {
                                var beeIngredient = BeeIngredientFactory.getIngredient("productivebees:" + type);
                                if (beeIngredient.get() != null) {
                                    CompoundTag bee = BeeHelper.getBeeAsCompoundTag(beeIngredient.get());
                                    blockEntity.addBee(bee, random.nextInt(599), 600, null, new TranslatableComponent("entity.productivebees." + type + "_bee").getString());
                                }
                            } catch (CommandSyntaxException e) {
                                ProductiveBees.LOGGER.warn("Failed to put bees into nether nest :(" + e.getMessage());
                            }
                        }
                    });
                }
            }
        }
    }
}
