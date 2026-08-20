package cy.jdkdigital.productivebees.client.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import org.jspecify.annotations.Nullable;

/**
 * Reads a colour from a {@link cy.jdkdigital.productivebees.setup.BeeReloadListener} entry and
 * returns it as a tint colour. 26.1 replaced the old function-style
 * {@code RegisterColorHandlersEvent.Item} registration with codec-based
 * {@link ItemTintSource} records referenced from per-item-model JSON.
 *
 * <p>If {@code beeType} is non-empty, the source treats it as a fixed bee identifier (used for
 * the hand-rolled spawn eggs and honeycombs — e.g. {@code productivebees:bumble_bee}). If it's
 * empty, the source reads the bee type at render time from the stack — first via
 * {@link ModDataComponents#BEE_TYPE} (configurable honeycomb / comb-block), then via
 * {@link DataComponents#ENTITY_DATA} (configurable spawn egg). Falls back to white.
 *
 * <p>{@code colorKey} is the key inside the bee's reload-listener data — typically
 * {@code primaryColor}, {@code secondaryColor}, or {@code tertiaryColor}.
 */
public record BeeTintSource(String beeType, String colorKey) implements ItemTintSource
{
    public static final MapCodec<BeeTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("beeType", "").forGetter(BeeTintSource::beeType),
            Codec.STRING.fieldOf("colorKey").forGetter(BeeTintSource::colorKey)
    ).apply(i, BeeTintSource::new));

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        Identifier beeId = resolveBeeType(stack);
        if (beeId == null) {
            return 0xFFFFFFFF;
        }
        BeeData beeData = BeeRegistries.lookup(beeId);
        if (beeData == null) {
            return 0xFFFFFFFF;
        }
        int color = switch (colorKey) {
            case "primaryColor" -> beeData.primaryColor();
            case "secondaryColor" -> beeData.secondaryColor();
            case "tertiaryColor" -> beeData.tertiaryColor();
            case "particleColor" -> beeData.particleColor().orElse(0xFFFFFF);
            default -> 0xFFFFFF;
        };
        return 0xFF000000 | color;
    }

    private @Nullable Identifier resolveBeeType(ItemStack stack) {
        if (!beeType.isEmpty()) {
            return Identifier.tryParse(beeType);
        }
        Identifier viaComponent = stack.get(ModDataComponents.BEE_TYPE.get());
        if (viaComponent != null) {
            return viaComponent;
        }
        TypedEntityData<?> entityData = stack.get(DataComponents.ENTITY_DATA);
        if (entityData != null) {
            String type = entityData.copyTagWithoutId().getString("type").orElse("");
            if (!type.isEmpty()) {
                return Identifier.tryParse(type);
            }
        }
        return null;
    }

    @Override
    public MapCodec<BeeTintSource> type() {
        return MAP_CODEC;
    }
}
