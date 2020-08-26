package cy.jdkdigital.productivebees.util;

import java.awt.*;

public class ColorUtil
{
    public static int hexToInt(String hex) {
        Color color = Color.decode(hex);

        return 256 * 256 * color.getRed() + 256 * color.getGreen() + color.getBlue();
    }
}
