package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;

public class CombBlock extends Block
{
    protected final int color;

    public CombBlock(Properties properties, String colorCode) {
        super(properties);
        this.color = ColorUtil.hexToInt(colorCode);
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
}
