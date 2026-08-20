package cy.jdkdigital.productivebees.client.color;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block tint source for wood-nest opening texture. Returns the fixed colour for tintIndex 1
 * (matching the original {@code WoodNest.getColor(tintIndex)} behaviour) and {@code -1} for any
 * other tint slot so the base wood texture keeps its native colour. The tint value lives in this
 * record so it can be serialised per-block from the {@link cy.jdkdigital.productivebees.datagen.BlockstateProvider}.
 */
public record WoodNestTintSource(int color) implements BlockTintSource
{
    public static final MapCodec<WoodNestTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(WoodNestTintSource::color)
    ).apply(i, WoodNestTintSource::new));

    @Override
    public int color(BlockState state) {
        return 0xFF000000 | color;
    }
}
