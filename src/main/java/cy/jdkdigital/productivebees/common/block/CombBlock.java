package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CombBlock extends Block
{
    protected final int color;

    public CombBlock(Properties properties, String colorCode) {
        super(properties);
        this.color = TextColor.parseColor(colorCode).result().get().getValue();
    }

    public int getColor() {
        return color;
    }

    public int getColor(BlockAndTintGetter world, BlockPos pos) {
        return getColor();
    }

    public int getColor(ItemStack stack) {
        int color = CombBlockItem.getColor(stack);
        return color != 0 ? color : getColor();
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }
}
