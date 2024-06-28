package cy.jdkdigital.productivebees.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum GeneAttribute implements StringRepresentable
{
    TYPE(0, "type"),
    PRODUCTIVITY(1, "productivity"),
    ENDURANCE(2, "endurance"),
    TEMPER(3, "temper"),
    BEHAVIOR(4, "behavior"),
    WEATHER_TOLERANCE(5, "weather_tolerance");

    private static final IntFunction<GeneAttribute> BY_ID = ByIdMap.continuous(GeneAttribute::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final EnumCodec<GeneAttribute> CODEC = StringRepresentable.fromEnum(GeneAttribute::values);
    public static final StreamCodec<ByteBuf, GeneAttribute> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GeneAttribute::getId);

    final Integer id;
    final String name;

    GeneAttribute(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}