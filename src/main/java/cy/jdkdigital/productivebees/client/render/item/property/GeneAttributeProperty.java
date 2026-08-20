package cy.jdkdigital.productivebees.client.render.item.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves an {@link ItemStack}'s {@link GeneAttribute} to its serialized name so per-attribute
 * gene icons can be picked via the 26.1 {@code minecraft:select} item-model property. Replaces the
 * pre-26.1 {@code overrides[].predicate.genetic} mechanism on the gene item model.
 */
public record GeneAttributeProperty() implements SelectItemModelProperty<String>
{
    public static final MapCodec<GeneAttributeProperty> MAP_CODEC = MapCodec.unit(GeneAttributeProperty::new);
    public static final SelectItemModelProperty.Type<GeneAttributeProperty, String> TYPE =
            SelectItemModelProperty.Type.create(MAP_CODEC, Codec.STRING);

    @Nullable
    @Override
    public String get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        GeneAttribute attribute = Gene.getAttribute(stack);
        return attribute == null ? null : attribute.getSerializedName();
    }

    @Override
    public Codec<String> valueCodec() {
        return Codec.STRING;
    }

    @Override
    public SelectItemModelProperty.Type<? extends SelectItemModelProperty<String>, String> type() {
        return TYPE;
    }
}
