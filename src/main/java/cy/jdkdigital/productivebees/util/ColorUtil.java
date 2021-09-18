package cy.jdkdigital.productivebees.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil
{
    // Color calc cache
    private static final Map<Integer, float[]> colorCache = new HashMap<>();

    public static float[] getCacheColor(Integer color) {
        if (!colorCache.containsKey(color)) {
            colorCache.put(color, ColorUtil.getComponents(color));
        }
        return colorCache.get(color);
    }

    public static float[] getComponents(int color) {
        float[] f = new float[4];
        f[0] = (float) ((color >> 16) & 0xFF)/255f;
        f[1] = (float) ((color >> 8) & 0xFF)/255f;
        f[2] = (float) (color & 0xFF)/255f;
        f[3] = (float) ((color >> 24) & 0xff)/255f;

        return f;
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
