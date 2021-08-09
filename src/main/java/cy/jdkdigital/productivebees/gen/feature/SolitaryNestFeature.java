package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.SugarbagNestBlockEntity;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.Random;

public class SolitaryNestFeature extends Feature<ReplaceBlockConfiguration>
{
    private final float probability;
    private boolean placeOntop;

    public SolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory) {
        this(probability, configFactory, false);
    }

    public SolitaryNestFeature(float probability, Codec<ReplaceBlockConfiguration> configFactory, boolean placeOntop) {
        super(configFactory);
        this.probability = probability;
        this.placeOntop = placeOntop;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel world = context.level();
        Random rand = context.random();
        BlockPos blockPos = context.origin();
        ReplaceBlockConfiguration featureConfig = context.config();

        for(OreConfiguration.TargetBlockState targetBlockState : featureConfig.targetStates) {
            if (nestShouldNotGenerate(targetBlockState.state) || rand.nextFloat() > this.probability) {
                return false;
            }

            // Get random block in chunk
            blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

            // Go to surface
            while (blockPos.getY() < 50 || !world.isEmptyBlock(blockPos)) {
                blockPos = blockPos.above();
            }

            if (!placeOntop) {
                blockPos = blockPos.below();
            }

            return placeNest(world, blockPos, targetBlockState.state);
        }
        return false;
    }

    protected boolean nestShouldNotGenerate(BlockState state) {
        return !ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("enable_" + state.getBlock().getRegistryName()).get();
    }

    protected boolean placeNest(WorldGenLevel world, BlockPos pos, BlockState state) {
        // Check if we are at target block
        BlockStatePredicate matcher = BlockStatePredicate.forBlock(state.getBlock());
        boolean match = placeOntop ? matcher.test(world.getBlockState(pos.below())) : matcher.test(world.getBlockState(pos));
        if (match) {
            // Check if there's air around and face that way, default to UP
            Direction direction = state.getBlock() instanceof WoodNest ? Direction.SOUTH : Direction.UP;
            for (Direction dir : BlockStateProperties.FACING.getPossibleValues()) {
                BlockPos blockPos = pos.relative(dir, 1);
                if (world.isEmptyBlock(blockPos)) {
                    direction = dir;
                    break;
                }
            }

            // Replace target block with nest
            BlockState newState = state.setValue(BlockStateProperties.FACING, direction);

            // Tiny chance to spawn a sugarbag nest instead
            if (world.getRandom().nextFloat() < 0.001 && newState.is(ModTags.SOLITARY_OVERWORLD_NESTS) && newState.getBlock() instanceof WoodNest) {
                Direction facing = Direction.SOUTH;

                // Find air position to put it on
                for (Direction dir : BlockStateProperties.FACING.getPossibleValues()) {
                    BlockPos blockPos = pos.relative(dir, 1);
                    if (world.isEmptyBlock(blockPos)) {
                        if (!dir.equals(Direction.DOWN) && !dir.equals(Direction.UP)) {
                            facing = dir;
                        }
                        pos = blockPos;
                        break;
                    }
                }

                // Move up a bit
                for (int i = 1; i <= 3; i++) {
                    if (!world.isEmptyBlock(pos.above(i))) {
                        pos = pos.above(i - 1);
                        break;
                    }
                }

                if (!world.isEmptyBlock(pos.relative(facing))) {
                    facing = facing.getOpposite();
                }

                newState = ModBlocks.SUGARBAG_NEST.get().defaultBlockState().setValue(BeehiveBlock.FACING, facing);
            }

            boolean result = world.setBlock(pos, newState, 1);

            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof SolitaryNestBlockEntity) {
                ProductiveBees.LOGGER.debug("Spawned nest at " + pos + " " + newState);
                Entity newBee = ((SolitaryNest) world.getBlockState(pos).getBlock()).getNestingBeeType(world.getLevel(), world.getLevel().getBiome(pos));
                if (newBee instanceof Bee) {
                    ((Bee) newBee).setHealth(((Bee) newBee).getMaxHealth());
                    newBee.setPos(pos.getX(), pos.getY(), pos.getZ());
                    ((SolitaryNestBlockEntity) tileEntity).addOccupantWithPresetTicks(newBee, false, world.getRandom().nextInt(599));
                }
            } else if (tileEntity instanceof SugarbagNestBlockEntity) {
                ProductiveBees.LOGGER.debug("Spawned sugarbag nest at " + pos + " " + newState);
                ConfigurableBee newBee = ModEntities.CONFIGURABLE_BEE.get().create(world.getLevel());
                if (newBee != null) {
                    newBee.setBeeType("productivebees:sugarbag");
                    newBee.setAttributes();
                    newBee.setHealth(newBee.getMaxHealth());
                    newBee.setPos(pos.getX(), pos.getY(), pos.getZ());
                    ((SugarbagNestBlockEntity) tileEntity).addOccupantWithPresetTicks(newBee, false, world.getRandom().nextInt(599));
                }
            }

            return result;
        }
        return false;
    }
}
