package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.tileentity.DragonEggHiveTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DragonEggHive extends AdvancedBeehiveAbstract {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public DragonEggHive(Block.Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public int getMaxHoneyLevel() {
        return 0;
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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote()) {
            DragonEggHiveTileEntity tileEntity = (DragonEggHiveTileEntity) world.getTileEntity(pos);
            ProductiveBees.LOGGER.info("Nest tilentity: " + tileEntity);
            ProductiveBees.LOGGER.info("Bee count: " + tileEntity.getBees().size());
            ProductiveBees.LOGGER.info("Occupants: " + tileEntity.getBees());

            return ActionResultType.PASS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, blockRayTraceResult);
    }
}
