package cy.jdkdigital.productivebees.block;

import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class CombBlock extends Block
{
    private final int color;

    public CombBlock(Properties properties, String colorCode) {
        super(properties);
        this.color = Color.decode(colorCode).getRGB();
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }
}
