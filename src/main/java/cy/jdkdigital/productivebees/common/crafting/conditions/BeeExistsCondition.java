package cy.jdkdigital.productivebees.common.crafting.conditions;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

public record BeeExistsCondition(ResourceLocation beeName) implements ICondition
{
    public static MapCodec<BeeExistsCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(ResourceLocation.CODEC.fieldOf("bee").forGetter(BeeExistsCondition::beeName))
                    .apply(builder, BeeExistsCondition::new));

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        JsonObject beeData = BeeReloadListener.INSTANCE.getCondition(beeName.toString());
        if (beeData != null) {
            var enabled = !beeData.has("conditions");
            if (!enabled) {
                var conditions = ICondition.LIST_CODEC.decode(JsonOps.INSTANCE, beeData.getAsJsonArray("conditions"));
                if (conditions.isSuccess()) {
                    enabled = true;
                    for (ICondition condition : conditions.result().get().getFirst()) {
                        if (!condition.test(context)) {
                            enabled = false;
                        }
                    }
                }
            }
            return enabled;
        }
        return false;
    }

    @Override
    public String toString() {
        return "bee_exists(\"" + beeName + "\")";
    }
}