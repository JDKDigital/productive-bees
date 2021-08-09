package cy.jdkdigital.productivebees.util;

import net.minecraft.ChatFormatting;

import java.awt.*;

public class ColorUtil
{
    public static int hexToInt(String hex) {
        Color color = Color.decode(hex);

        return 256 * 256 * color.getRed() + 256 * color.getGreen() + color.getBlue();
    }

    public static ChatFormatting getColor(String type) {
        switch (type) {
            case "hive":
                return ChatFormatting.YELLOW;
            case "solitary":
                return ChatFormatting.GRAY;
        }
        return ChatFormatting.WHITE;
    }

    public static ChatFormatting getColor(int level) {
        switch (level) {
            case -3:
                return ChatFormatting.RED;
            case -2:
                return ChatFormatting.DARK_RED;
            case -1:
                return ChatFormatting.YELLOW;
            case 1:
                return ChatFormatting.GREEN;
            case 2:
                return ChatFormatting.BLUE;
            case 3:
                return ChatFormatting.GOLD;
        }
        return ChatFormatting.LIGHT_PURPLE;
    }
}
