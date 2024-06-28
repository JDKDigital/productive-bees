package cy.jdkdigital.productivebees.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.IntFunction;

public enum GeneValue implements StringRepresentable
{
    EMPTY(0, "empty", 0),
    PRODUCTIVITY_NORMAL(1, "productivity.normal", 0),
    PRODUCTIVITY_MEDIUM(2, "productivity.medium", 1),
    PRODUCTIVITY_HIGH(3, "productivity.high", 2),
    PRODUCTIVITY_VERY_HIGH(4, "productivity.very_high", 3),
    ENDURANCE_WEAK(5, "endurance.weak", 0),
    ENDURANCE_NORMAL(6, "endurance.normal", 1),
    ENDURANCE_MEDIUM(7, "endurance.medium", 2),
    ENDURANCE_STRONG(8, "endurance.strong", 3),
    TEMPER_PASSIVE(9, "temper.passive", 0),
    TEMPER_NORMAL(10, "temper.normal", 1),
    TEMPER_AGGRESSIVE(11, "temper.aggressive", 2),
    TEMPER_HOSTILE(12, "temper.hostile", 3),
    BEHAVIOR_DIURNAL(13, "behavior.diurnal", 0),
    BEHAVIOR_NOCTURNAL(14, "behavior.nocturnal", 1),
    BEHAVIOR_METATURNAL(15, "behavior.metaturnal", 2),
    WEATHER_TOLERANCE_NONE(16, "weather_tolerance.none", 0),
    WEATHER_TOLERANCE_RAIN(17,  "weather_tolerance.rain", 1),
    WEATHER_TOLERANCE_ANY(18, "weather_tolerance.any", 2);

    private static final IntFunction<GeneValue> BY_ID = ByIdMap.continuous(GeneValue::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    private static final IntFunction<GeneValue> BY_NAME = ByIdMap.continuous(GeneValue::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StringRepresentable.EnumCodec<GeneValue> CODEC = StringRepresentable.fromEnum(GeneValue::values);
    public static final StreamCodec<ByteBuf, GeneValue> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GeneValue::getId);

    private final Integer id;
    private final String name;
    private final int value;

    GeneValue(Integer id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    @Nullable
    public static GeneValue byName(String name) {
        return CODEC.byName(name);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static GeneValue getRandomProductivity(Random random) {
        return BY_ID.apply(random.nextInt(1, 4));
    }

    public static GeneValue getRandomEndurance(Random random) {
        return BY_ID.apply(random.nextInt(6, 9));
    }

    public static GeneValue nextTemper(GeneValue temper) {
        return switch (temper) {
            case TEMPER_PASSIVE, TEMPER_NORMAL -> TEMPER_PASSIVE;
            case TEMPER_HOSTILE -> TEMPER_AGGRESSIVE;
            default -> TEMPER_NORMAL;
        };
    }

    public static GeneValue productivity(int value) {
        return switch (value) {
            case 1 -> PRODUCTIVITY_MEDIUM;
            case 2 -> PRODUCTIVITY_HIGH;
            case 3 -> PRODUCTIVITY_VERY_HIGH;
            default -> PRODUCTIVITY_NORMAL;
        };
    }

    public static GeneValue endurance(int value) {
        return switch (value) {
            case 1 -> ENDURANCE_NORMAL;
            case 2 -> ENDURANCE_MEDIUM;
            case 3 -> ENDURANCE_STRONG;
            default -> ENDURANCE_WEAK;
        };
    }

    public static GeneValue temper(int value) {
        return switch (value) {
            case 1 -> TEMPER_NORMAL;
            case 2 -> TEMPER_AGGRESSIVE;
            case 3 -> TEMPER_HOSTILE;
            default -> TEMPER_PASSIVE;
        };
    }

    public static GeneValue behavior(int value) {
        return switch (value) {
            case 1 -> BEHAVIOR_NOCTURNAL;
            case 2 -> BEHAVIOR_METATURNAL;
            default -> BEHAVIOR_DIURNAL;
        };
    }

    public static GeneValue weatherTolerance(int value) {
        return switch (value) {
            case 1 -> WEATHER_TOLERANCE_RAIN;
            case 2 -> WEATHER_TOLERANCE_ANY;
            default -> WEATHER_TOLERANCE_NONE;
        };
    }
}
