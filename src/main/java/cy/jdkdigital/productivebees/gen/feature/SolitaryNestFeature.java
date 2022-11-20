package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class SolitaryNestFeature extends Feature<ReplaceBlockConfiguration>
{
    protected final String configKey;
    protected boolean placeOntop;

    public SolitaryNestFeature(String configKey, Codec<ReplaceBlockConfiguration> configFactory) {
        this(configKey, configFactory, false);
    }

    public SolitaryNestFeature(String configKey, Codec<ReplaceBlockConfiguration> configFactory, boolean placeOntop) {
        super(configFactory);
        this.configKey = configKey;
        this.placeOntop = placeOntop;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (rand.nextFloat() > ProductiveBeesConfig.WORLD_GEN.nestConfigs.get(configKey).get().floatValue()) {
                return false;
            }

            // Get random block in chunk
            blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

            // Go to surface
            while (blockPos.getY() < 50 || !level.isEmptyBlock(blockPos)) {
                blockPos = blockPos.above();
            }

            if (!placeOntop) {
                blockPos = blockPos.below();
            }

            BlockState state = placeOntop ? level.getBlockState(blockPos.below()) : level.getBlockState(blockPos);
            if (targetBlockState.target.test(state, rand)) {
                return placeNest(level, blockPos, targetBlockState.state, rand);
            }
        }
        return false;
    }

    protected boolean placeNest(WorldGenLevel level, BlockPos pos, BlockState state, RandomSource random) {
        // Check if there's air around and face that way, default to UP
        Direction direction = state.getBlock() instanceof WoodNest ? Direction.SOUTH : Direction.UP;
        for (Direction dir : BlockStateProperties.FACING.getPossibleValues()) {
            BlockPos blockPos = pos.relative(dir, 1);
            if (level.isEmptyBlock(blockPos)) {
                direction = dir;
                break;
            }
        }

        // Replace target block with nest
        BlockState newState = state.setValue(BlockStateProperties.FACING, direction);

        boolean result = level.setBlock(pos, newState, 1);

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof SolitaryNestBlockEntity nestBlockEntity && state.getBlock() instanceof SolitaryNest nestBlock) {
            ProductiveBees.LOGGER.debug("Spawned nest at " + pos + " " + newState);
            var recipes = nestBlock.getSpawningRecipes(level.getLevel(), level.getBiome(pos).value());
            if (recipes.size() > 0) {
                BeeSpawningRecipe spawningRecipe = recipes.size() == 1 ? recipes.get(0) : recipes.get(random.nextInt(recipes.size()));
                if (spawningRecipe.output.size() > 0) {
                    BeeIngredient beeIngredient = spawningRecipe.output.get(random.nextInt(spawningRecipe.output.size())).get();
                    try {
                        CompoundTag bee = BeeHelper.getBeeAsCompoundTag(beeIngredient);
                        nestBlockEntity.addBee(bee, random.nextInt(599), 600, null, Component.translatable("entity.productivebees." + beeIngredient.getBeeType().getPath()).getString());
                    } catch (CommandSyntaxException e) {
                        ProductiveBees.LOGGER.warn("Failed to put bees into solitary nest :(" + e.getMessage());
                    }
                }
            }
        }

        return result;
    }
}
