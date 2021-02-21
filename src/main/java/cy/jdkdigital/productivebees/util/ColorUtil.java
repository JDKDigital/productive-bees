package cy.jdkdigital.productivebees.util;

import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class ColorUtil
{
    public static int hexToInt(String hex) {
        Color color = Color.decode(hex);

        return 256 * 256 * color.getRed() + 256 * color.getGreen() + color.getBlue();
    }

    public static TextFormatting getColor(String type) {
        switch (type) {
            case "hive":
                return TextFormatting.YELLOW;
            case "solitary":
                return TextFormatting.GRAY;
        }
        return TextFormatting.WHITE;
    }

    public static TextFormatting getColor(int level) {
        switch (level) {
            case -3:
                return TextFormatting.RED;
            case -2:
                return TextFormatting.DARK_RED;
            case -1:
                return TextFormatting.YELLOW;
            case 1:
                return TextFormatting.GREEN;
            case 2:
                return TextFormatting.BLUE;
            case 3:
                return TextFormatting.GOLD;
        }
        return TextFormatting.LIGHT_PURPLE;
    }
}
