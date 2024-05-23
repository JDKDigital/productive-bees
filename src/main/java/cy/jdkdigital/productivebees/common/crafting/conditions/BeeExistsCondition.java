package cy.jdkdigital.productivebees.common.crafting.conditions;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

public record BeeExistsCondition(String beeName) implements ICondition
{
    public static MapCodec<BeeExistsCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(Codec.STRING.fieldOf("bee").forGetter(BeeExistsCondition::beeName))
                    .apply(builder, BeeExistsCondition::new));

    public BeeExistsCondition(String location) {
        this(new ResourceLocation(location));
    }

    public BeeExistsCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public BeeExistsCondition(ResourceLocation tag) {
        this.beeName = tag;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        JsonObject beeData = BeeReloadListener.INSTANCE.getCondition(beeName.toString());
        return beeData != null && CraftingHelper.processConditions(beeData, "conditions", context);
    }

    @Override
    public String toString() {
        return "bee_exists(\"" + beeName + "\")";
    }
}