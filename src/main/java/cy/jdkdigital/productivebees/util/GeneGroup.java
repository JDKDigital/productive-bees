package cy.jdkdigital.productivebees.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GeneGroup(GeneAttribute attribute, String value, Integer purity)
{
    public static final Codec<GeneGroup> CODEC = RecordCodecBuilder.create(
            group -> group.group(
                            GeneAttribute.CODEC.fieldOf("attribute").forGetter(GeneGroup::attribute),
                            Codec.STRING.fieldOf("value").forGetter(GeneGroup::value),
                            Codec.INT.fieldOf("purity").forGetter(GeneGroup::purity)
                    )
                    .apply(group, GeneGroup::new)
    );
    public static final StreamCodec<ByteBuf, GeneGroup> STREAM_CODEC = StreamCodec.composite(
            GeneAttribute.STREAM_CODEC,
            GeneGroup::attribute,
            ByteBufCodecs.STRING_UTF8,
            GeneGroup::value,
            ByteBufCodecs.VAR_INT,
            GeneGroup::purity,
            GeneGroup::new
    );

    public static GeneGroup increasePurity(GeneGroup geneGroup, int purity) {
        return new GeneGroup(geneGroup.attribute, geneGroup.value, purity);
    }
}
