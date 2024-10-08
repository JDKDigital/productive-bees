package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModFeatures;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class WoodNestDecorator extends TreeDecorator {
    public static final WoodNestDecorator INSTANCE = new WoodNestDecorator();

//    public static final Codec<WoodNestDecorator> CODEC = Codec.unit(WoodNestDecorator::new);
    public static final MapCodec<WoodNestDecorator> CODEC = BlockState.CODEC.fieldOf("nest")
            .xmap(WoodNestDecorator::new, p_69971_ -> p_69971_.nest);

    private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().filter((direction) -> direction != Direction.SOUTH.getOpposite()).toArray(Direction[]::new);

    private BlockState nest;
    private List<RecipeHolder<BeeSpawningRecipe>> recipes;

    public WoodNestDecorator() {
    }

    public WoodNestDecorator(BlockState nest) {
        setNest(nest);
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.WOOD_NEST.get();
    }

    public void setNest(BlockState nest) {
        this.nest = nest;
    }

    public BlockState getNest() {
        return this.nest;
    }

    public void setBeeRecipes(List<RecipeHolder<BeeSpawningRecipe>> recipe) {
        this.recipes = recipe;
    }

    @Override
    public void place(Context context) {
        LevelSimulatedReader pLevel = context.level();
        RandomSource random = context.random();
        List<BlockPos> pLogPositions = context.logs();
        List<BlockPos> pLeafPositions = context.leaves();
        int i = !pLeafPositions.isEmpty() ? Math.max(pLeafPositions.get(0).getY() - 1, pLogPositions.get(0).getY() + 1) : Math.min(pLogPositions.get(0).getY() + 1 + random.nextInt(3), pLogPositions.get(pLogPositions.size() - 1).getY());
        // Find log positions that have air next to it
        List<BlockPos> list = pLogPositions.stream().filter((pos) -> pos.getY() == i).flatMap((pos) -> {
            return Stream.of(SPAWN_DIRECTIONS).map(direction -> {
                return context.isAir(pos.relative(direction)) ? pos : null;
            });
        }).filter(Objects::nonNull).toList();

        if (!list.isEmpty() && this.nest != null) {
            BlockPos nestPos = list.get(list.size() <= 1 ? 0 : random.nextInt(list.size()));
            if (nestPos != null) {
                List<Direction> nestDirections = Stream.of(SPAWN_DIRECTIONS).filter(direction -> context.isAir(nestPos.relative(direction))).toList();
                if (!nestDirections.isEmpty()) {
                    context.setBlock(nestPos, this.nest.getBlock().defaultBlockState().setValue(BlockStateProperties.FACING, nestDirections.size() == 1 ? nestDirections.get(0) : nestDirections.get(random.nextInt(nestDirections.size()))));
                    pLevel.getBlockEntity(nestPos, ModBlockEntityTypes.SOLITARY_NEST.get()).ifPresent((nestBlockEntity) -> {
                        ProductiveBees.LOGGER.debug("Spawned wood nest at " + nestPos + " " + this.nest);
                        if (!this.recipes.isEmpty() && !nestBlockEntity.isFull()) {
                            RecipeHolder<BeeSpawningRecipe> spawningRecipe = this.recipes.get(random.nextInt(this.recipes.size()));
                            if (!spawningRecipe.value().output.isEmpty()) {
                                BeeIngredient beeIngredient = spawningRecipe.value().output.get(random.nextInt(spawningRecipe.value().output.size())).get();

                                try {
                                    CompoundTag bee = BeeHelper.getBeeAsCompoundTag(beeIngredient);
                                    nestBlockEntity.addOccupantFromTag(bee, random.nextInt(599), 600);
                                } catch (CommandSyntaxException e) {
                                    ProductiveBees.LOGGER.warn("Failed to put bees into solitary nest :(" + e.getMessage());
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
