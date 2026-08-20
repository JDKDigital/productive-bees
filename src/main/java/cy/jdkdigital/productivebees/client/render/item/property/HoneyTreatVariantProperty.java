package cy.jdkdigital.productivebees.client.render.item.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record HoneyTreatVariantProperty() implements SelectItemModelProperty<String>
{
    public static final MapCodec<HoneyTreatVariantProperty> MAP_CODEC = MapCodec.unit(HoneyTreatVariantProperty::new);
    public static final SelectItemModelProperty.Type<HoneyTreatVariantProperty, String> TYPE =
            SelectItemModelProperty.Type.create(MAP_CODEC, Codec.STRING);

    @Nullable
    @Override
    public String get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        if (!HoneyTreat.hasGene(stack)) {
            return null;
        }
        for (GeneGroup group : HoneyTreat.getGenes(stack)) {
            if (!group.attribute().equals(GeneAttribute.TYPE)) {
                return "genetic";
            }
        }
        return "type";
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
