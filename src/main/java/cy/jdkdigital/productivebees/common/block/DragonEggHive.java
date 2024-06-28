package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.DragonEggHiveBlockEntity;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DragonEggHive extends AdvancedBeehive
{
    protected static final VoxelShape SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public DragonEggHive(Block.Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DragonEggHiveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntityTypes.DRACONIC_BEEHIVE.get(), DragonEggHiveBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(BeehiveBlock.HONEY_LEVEL) >= getMaxHoneyLevel()) {
            for (int i = 0; i < 22; ++i) {
                double rnd = world.random.nextDouble();
                float xSpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                float ySpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                float zSpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                double x = Mth.lerp(rnd, pos.getX(), pos.getX()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                double y = Mth.lerp(rnd, pos.getY(), pos.getY()) + world.random.nextDouble() - 0.5D;
                double z = Mth.lerp(rnd, pos.getZ(), pos.getZ()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                world.addParticle(ParticleTypes.PORTAL, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }
    }
}
