package cy.jdkdigital.productivebees.item;

import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class HoneyComb extends Item
{
    private final int color;

    public HoneyComb(Properties properties, String colorCode) {
        super(properties);
        this.color = Color.decode(colorCode).getRGB();
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }
}
