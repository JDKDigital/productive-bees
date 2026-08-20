package cy.jdkdigital.productivebees.gen.feature;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class NestFeature extends Feature<NestConfiguration>
{
    public NestFeature() {
        super(NestConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NestConfiguration> context) {
        NestConfiguration config = context.config();
        RandomSource rand = context.random();

        if (config.configKey().isPresent()) {
            String key = config.configKey().get();
            var gate = ProductiveBeesConfig.WORLD_GEN.nestConfigs.get(key);
            if (gate != null && rand.nextFloat() > gate.get().floatValue()) {
                return false;
            }
        }

        WorldGenLevel level = context.level();
        for (OreConfiguration.TargetBlockState target : config.targets()) {
            BlockPos found = config.search().search(level, context.chunkGenerator(), rand, context.origin(), target);
            if (found != null) {
                return placeNest(level, found, target.state, rand);
            }
        }
        return false;
    }

    private static boolean placeNest(WorldGenLevel level, BlockPos pos, BlockState nestState, RandomSource random) {
        Direction direction = nestState.getBlock() instanceof WoodNest ? Direction.SOUTH : Direction.UP;
        for (Direction dir : BlockStateProperties.FACING.getPossibleValues()) {
            if (level.isEmptyBlock(pos.relative(dir, 1))) {
                direction = dir;
                break;
            }
        }
        BlockState newState = nestState.setValue(BlockStateProperties.FACING, direction);
        boolean placed = level.setBlock(pos, newState, 1);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SolitaryNestBlockEntity nestBlockEntity && nestState.getBlock() instanceof SolitaryNest nestBlock) {
            ProductiveBees.LOGGER.debug("Spawned nest at " + pos + " " + newState);
            try {
                var recipes = SolitaryNest.getSpawningRecipes(nestBlock, level.getLevel(), level.getBiome(pos), ItemStack.EMPTY);
                if (!recipes.isEmpty()) {
                    RecipeHolder<BeeSpawningRecipe> spawningRecipe = recipes.size() == 1 ? recipes.getFirst() : recipes.get(random.nextInt(recipes.size()));
                    if (!spawningRecipe.value().output.isEmpty()) {
                        BeeIngredient beeIngredient = spawningRecipe.value().output.get(random.nextInt(spawningRecipe.value().output.size())).get();
                        CompoundTag bee = BeeHelper.getBeeAsCompoundTag(beeIngredient);
                        nestBlockEntity.addOccupantFromTag(bee, random.nextInt(599), 600);
                    }
                }
            } catch (Exception e) {
                ProductiveBees.LOGGER.warn("Failed to put bees into solitary nest :(" + e.getMessage());
            }
        }
        return placed;
    }
}
