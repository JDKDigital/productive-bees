package cy.jdkdigital.productivebees.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil
{
    // Color calc cache
    private static final Map<Integer, float[]> colorCache = new HashMap<>();
    private static final Map<String, Integer> stringColorCache = new HashMap<>();

    public static Integer getCacheColor(String color) {
        if (!stringColorCache.containsKey(color)) {
            stringColorCache.put(color, TextColor.parseColor(color).result().get().getValue());
        }
        return stringColorCache.get(color);
    }

    public static float[] getCacheColor(int color) {
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

    public static int getCycleColor(int color, int color2, int tickCount, float partialTicks) {
        float f3 = ((float)(tickCount % 25) + partialTicks) / 25.0F;
        return (int) (color * (1.0F - f3) + color2 * f3);
    }

    public static ChatFormatting getBeeTypeColor(String type) {
        return switch (type) {
            case "hive" -> ChatFormatting.YELLOW;
            case "solitary" -> ChatFormatting.GRAY;
            default -> ChatFormatting.WHITE;
        };
    }

    public static ChatFormatting getAttributeColor(GeneValue level) {
        if (level == null) {
            return ChatFormatting.GREEN;
        }
        return switch (level) {
            case TEMPER_NORMAL, PRODUCTIVITY_MEDIUM, ENDURANCE_NORMAL -> ChatFormatting.BLUE;
            case TEMPER_AGGRESSIVE, PRODUCTIVITY_HIGH, WEATHER_TOLERANCE_RAIN, ENDURANCE_MEDIUM, BEHAVIOR_NOCTURNAL -> ChatFormatting.LIGHT_PURPLE;
            case TEMPER_HOSTILE, BEHAVIOR_METATURNAL, ENDURANCE_STRONG, WEATHER_TOLERANCE_ANY, PRODUCTIVITY_VERY_HIGH -> ChatFormatting.RED;
            default -> ChatFormatting.GREEN;
        };
    }
}
