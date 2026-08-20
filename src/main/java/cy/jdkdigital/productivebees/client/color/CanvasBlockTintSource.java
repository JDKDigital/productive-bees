package cy.jdkdigital.productivebees.client.color;

import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasExpansionBoxBlockEntity;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Reads the dye colour from the canvas hive / expansion box BlockEntity at slot 0. In the
 * inventory the block has no BE, so {@link #color(BlockState)} returns {@code -1} — the dye colour
 * is then applied at item-model time via the vanilla {@code minecraft:dye} tint source.
 */
public final class CanvasBlockTintSource implements BlockTintSource
{
    public static final CanvasBlockTintSource INSTANCE = new CanvasBlockTintSource();

    private CanvasBlockTintSource() {}

    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CanvasBeehiveBlockEntity canvas) {
            return canvas.getColor(0);
        }
        if (be instanceof CanvasExpansionBoxBlockEntity canvasBox) {
            return canvasBox.getColor(0);
        }
        return -1;
    }
}
