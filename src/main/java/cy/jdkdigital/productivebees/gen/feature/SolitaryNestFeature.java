package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.tileentity.SolitaryNestTileEntity;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

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
    public boolean func_230362_a_(@Nonnull ISeedReader world, @Nonnull StructureManager structureManager, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos blockPos, @Nonnull ReplaceBlockConfig featureConfig) {
        if (nestShouldNotGenerate(featureConfig) || rand.nextFloat() > this.probability) {
            return false;
        }

        // Get random block in chunk
        blockPos = blockPos.south(rand.nextInt(14)).east(rand.nextInt(14));

        // Go to surface
        while (blockPos.getY() < 50 || !world.isAirBlock(blockPos)) {
            blockPos = blockPos.up();
        }

        if (!placeOntop) {
            blockPos = blockPos.down();
        }

        return placeNest(world, blockPos, featureConfig);
    }

    protected boolean nestShouldNotGenerate(ReplaceBlockConfig featureConfig) {
        return !ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("enable_" + featureConfig.state.getBlock().getRegistryName()).get();
    }

    protected boolean placeNest(ISeedReader world, BlockPos pos, ReplaceBlockConfig featureConfig) {
        // Check if we are at target block
        BlockStateMatcher matcher = BlockStateMatcher.forBlock(featureConfig.target.getBlock());
        boolean match = placeOntop ? matcher.test(world.getBlockState(pos.down())) : matcher.test(world.getBlockState(pos));
        if (match) {
            // Check if there's air around and face that way, default to UP
            Direction direction = Direction.UP;
            for (Direction dir : BlockStateProperties.FACING.getAllowedValues()) {
                BlockPos blockPos = pos.offset(dir, 1);
                if (world.isAirBlock(blockPos)) {
                    direction = dir;
                    break;
                }
            }

            // Replace target block with nest
            boolean result = world.setBlockState(pos, featureConfig.state.with(BlockStateProperties.FACING, direction), 1);

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof SolitaryNestTileEntity) {
                SolitaryNestTileEntity nestTileEntity = (SolitaryNestTileEntity) tileEntity;
//                ProductiveBees.LOGGER.debug("Placed nest at " + pos + " " + featureConfig.state);
                EntityType<BeeEntity> beeType = SolitaryNestTileEntity.getProducibleBeeType(world.getWorld(), pos, (SolitaryNest) world.getBlockState(pos).getBlock());
                if (beeType != null) {
                    BeeEntity newBee = beeType.create(world.getWorld());
                    newBee.setHealth(newBee.getMaxHealth());
                    newBee.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    nestTileEntity.tryEnterHive(newBee, false, world.getRandom().nextInt(599));
                }
            }

            return result;
        }
        return false;
    }
}
