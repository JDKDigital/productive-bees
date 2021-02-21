package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;

import java.awt.*;

public class CombBlock extends Block
{
    protected final int color;

    public CombBlock(Properties properties, String colorCode) {
        super(properties);
        this.color = Color.decode(colorCode).getRGB();
    }

    public int getColor() {
        return color;
    }

    public int getColor(ILightReader world, BlockPos pos) {
        return getColor();
    }

    public int getColor(ItemStack stack) {
        int color = CombBlockItem.getColor(stack);
        return color != 0 ? color : getColor();
    }
}
