package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HoneyComb extends Item
{
    private final int color;

    public HoneyComb(Properties properties, String colorCode) {
        super(properties);
        this.color = ColorUtil.hexToInt(colorCode);
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }
}
