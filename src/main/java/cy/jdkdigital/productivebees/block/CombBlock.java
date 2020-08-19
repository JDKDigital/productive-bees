package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CombBlock extends Block
{
    private final int color;

    public CombBlock(Properties properties, String colorCode) {
        super(properties);
        this.color = ColorUtil.hexToInt(colorCode);
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }
}
