package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModFeatures;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
    public static final Codec<NetherBeehiveDecorator> CODEC = Codec.unit(NetherBeehiveDecorator::new);

    private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().filter((direction) -> direction != Direction.SOUTH.getOpposite()).toArray(Direction[]::new);

    private BlockState nest;

    public NetherBeehiveDecorator() {
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
        if (context.leaves().isEmpty() || context.logs().isEmpty()) {
            return;
        }

        int i = Math.max(context.leaves().get(0).getY() - 1, context.logs().get(0).getY() + 1);

        List<BlockPos> list = context.logs().stream().filter((pos) -> pos.getY() == i).flatMap((pos) -> Stream.of(SPAWN_DIRECTIONS).map(pos::relative)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            Collections.shuffle(list);
            Optional<BlockPos> optional = list.stream().filter((pos) -> context.isAir(pos) && context.isAir(pos.relative(Direction.SOUTH))).findFirst();
            if (optional.isPresent()) {
                // Find log position
                Direction facing = Direction.SOUTH;
                for (Direction d: Direction.Plane.HORIZONTAL) {
                    if (context.logs().contains(optional.get().relative(d))) {
                        facing = d.getOpposite();
                        break;
                    }
                }

                context.setBlock(optional.get(), nest.setValue(BeehiveBlock.FACING, facing));
                context.level().getBlockEntity(optional.get(), ModBlockEntityTypes.NETHER_BEE_NEST.get()).ifPresent((blockEntity) -> {
                    int j = 2 + context.random().nextInt(2);

                    String type = ForgeRegistries.BLOCKS.getKey(nest.getBlock()).getPath().equals("warped_bee_nest") ? "warped" : "crimson";

                    for(int k = 0; k < j; ++k) {
                        try {
                            var beeIngredient = BeeIngredientFactory.getIngredient("productivebees:" + type);
                            if (beeIngredient.get() != null) {
                                CompoundTag bee = BeeHelper.getBeeAsCompoundTag(beeIngredient.get());
                                blockEntity.addBee(bee, context.random().nextInt(599), 600, null, Component.translatable("entity.productivebees." + type + "_bee").getString());
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
