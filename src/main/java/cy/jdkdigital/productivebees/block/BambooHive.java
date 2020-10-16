package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.tileentity.SolitaryHiveTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BambooHive extends AdvancedBeehiveAbstract
{
    public static final EnumProperty<Direction.Axis> AXIS;

    public BambooHive(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    public int getMaxHoneyLevel() {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.SOLITARY_HIVE.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new SolitaryHiveTileEntity();
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.get(AXIS)) {
                    case X:
                        return state.with(AXIS, Direction.Axis.Z);
                    case Z:
                        return state.with(AXIS, Direction.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, AXIS);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(AXIS, context.getFace().getAxis());
    }

    static {
        AXIS = BlockStateProperties.AXIS;
    }
}
