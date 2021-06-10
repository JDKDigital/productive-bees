package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.SolitaryNestTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.SugarbagNestTileEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;

import javax.annotation.Nonnull;
import java.util.Random;

public class SolitaryNestFeature extends Feature<ReplaceBlockConfig>
{
    private final float probability;
    private boolean placeOntop;

    public SolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory) {
        this(probability, configFactory, false);
    }

    public SolitaryNestFeature(float probability, Codec<ReplaceBlockConfig> configFactory, boolean placeOntop) {
        super(configFactory);
        this.probability = probability;
        this.placeOntop = placeOntop;
    }

    @Override
    public boolean place(@Nonnull ISeedReader world, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
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

        return placeNest(world, blockPos, featureConfig);
    }

    protected boolean nestShouldNotGenerate(ReplaceBlockConfig featureConfig) {
        return !ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("enable_" + featureConfig.state.getBlock().getRegistryName()).get();
    }

    protected boolean placeNest(ISeedReader world, BlockPos pos, ReplaceBlockConfig featureConfig) {
        // Check if we are at target block
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        boolean match = placeOntop ? matcher.test(world.getBlockState(pos.below())) : matcher.test(world.getBlockState(pos));
        if (match) {
            // Check if there's air around and face that way, default to UP
            Direction direction = featureConfig.state.getBlock() instanceof WoodNest ? Direction.SOUTH : Direction.UP;
            for (Direction dir : BlockStateProperties.FACING.getPossibleValues()) {
                BlockPos blockPos = pos.relative(dir, 1);
                if (world.isEmptyBlock(blockPos)) {
                    direction = dir;
                    break;
                }
            }

            // Replace target block with nest
            BlockState newState = featureConfig.state.setValue(BlockStateProperties.FACING, direction);

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

            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof SolitaryNestTileEntity) {
                ProductiveBees.LOGGER.debug("Spawned nest at " + pos + " " + newState);
                Entity newBee = ((SolitaryNest) world.getBlockState(pos).getBlock()).getNestingBeeType(world.getLevel(), world.getLevel().getBiome(pos));
                if (newBee instanceof BeeEntity) {
                    ((BeeEntity) newBee).setHealth(((BeeEntity) newBee).getMaxHealth());
                    newBee.setPos(pos.getX(), pos.getY(), pos.getZ());
                    ((SolitaryNestTileEntity) tileEntity).addOccupantWithPresetTicks(newBee, false, world.getRandom().nextInt(599));
                }
            }
            else if (tileEntity instanceof SugarbagNestTileEntity) {
                ProductiveBees.LOGGER.debug("Spawned sugarbag nest at " + pos + " " + newState);
                ConfigurableBeeEntity newBee = ModEntities.CONFIGURABLE_BEE.get().create(world.getLevel());
                if (newBee != null) {
                    newBee.setBeeType("productivebees:sugarbag");
                    newBee.setAttributes();
                    newBee.setHealth(newBee.getMaxHealth());
                    newBee.setPos(pos.getX(), pos.getY(), pos.getZ());
                    ((SugarbagNestTileEntity) tileEntity).addOccupantWithPresetTicks(newBee, false, world.getRandom().nextInt(599));
                }
            }

            return result;
        }
        return false;
    }
}
