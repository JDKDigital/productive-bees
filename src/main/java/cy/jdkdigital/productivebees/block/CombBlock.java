package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.item.CombBlockItem;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CombBlock extends Block
{
    protected final int color;

    public CombBlock(Properties properties, String colorCode) {
        super(properties);
        this.color = ColorUtil.hexToInt(colorCode);
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(IBlockDisplayReader world, BlockPos pos) {
        return getColor();
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack) {
        int color = CombBlockItem.getColor(stack);
        return color != 0 ? color : getColor();
    }
}
