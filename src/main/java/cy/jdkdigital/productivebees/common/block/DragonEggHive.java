package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.DragonEggHiveTileEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class DragonEggHive extends AdvancedBeehive
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public DragonEggHive(Block.Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return ModTileEntityTypes.DRACONIC_BEEHIVE.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new DragonEggHiveTileEntity();
    }

    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(BeehiveBlock.HONEY_LEVEL) >= MAX_HONEY_LEVEL) {
            for (int i = 0; i < 22; ++i) {
                double rnd = world.rand.nextDouble();
                float xSpeed = (world.rand.nextFloat() - 0.5F) * 0.2F;
                float ySpeed = (world.rand.nextFloat() - 0.5F) * 0.2F;
                float zSpeed = (world.rand.nextFloat() - 0.5F) * 0.2F;
                double x = MathHelper.lerp(rnd, pos.getX(), pos.getX()) + (world.rand.nextDouble() - 0.5D) + 0.5D;
                double y = MathHelper.lerp(rnd, pos.getY(), pos.getY()) + world.rand.nextDouble() - 0.5D;
                double z = MathHelper.lerp(rnd, pos.getZ(), pos.getZ()) + (world.rand.nextDouble() - 0.5D) + 0.5D;
                world.addParticle(ParticleTypes.PORTAL, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }
    }
}
