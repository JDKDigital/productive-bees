package cy.jdkdigital.productivebees.client.color;

import cy.jdkdigital.productivebees.common.block.CombBlock;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mirrors {@code CombBlock.getColor(world, pos)}. Inventory falls back to the constructor colour
 * (CombBlock) or the BE colour (ConfigurableCombBlock has no inventory fallback because the held
 * stack tint is handled at the item-model level).
 */
public record CombBlockTintSource(CombBlock block) implements BlockTintSource
{
    @Override
    public int color(BlockState state) {
        return block.getColor();
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return block.getColor(level, pos);
    }
}
