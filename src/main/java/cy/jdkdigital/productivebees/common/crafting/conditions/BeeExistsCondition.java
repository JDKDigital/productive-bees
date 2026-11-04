package cy.jdkdigital.productivebees.common.crafting.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Optional;

/** Recipe / advancement condition that succeeds if a bee with the given id is loaded. */
public record BeeExistsCondition(Identifier beeName, HolderLookup.RegistryLookup<BeeData> lookup) implements ICondition
{
    public static final MapCodec<BeeExistsCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            Identifier.CODEC.fieldOf("bee").forGetter(BeeExistsCondition::beeName),
                            RegistryOps.retrieveRegistryLookup(BeeRegistries.BEE_DATA).forGetter(BeeExistsCondition::lookup)
                    )
                    .apply(builder, BeeExistsCondition::new));

    /** Datagen-only */
    public BeeExistsCondition(Identifier beeName) {
        this(beeName, null);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        Optional<Holder.Reference<BeeData>> direct = lookup.get(ResourceKey.create(BeeRegistries.BEE_DATA, beeName));
        if (direct.isPresent()) {
            return gatesPass(context, direct.get().value());
        }
        // Fall back to matching the path's last segment
        String targetPath = beeName.getPath();
        String targetNs = beeName.getNamespace();
        return lookup.listElements().anyMatch(holder -> {
            Identifier id = holder.key().identifier();
            if (!id.getNamespace().equals(targetNs)) return false;
            String path = id.getPath();
            int slash = path.lastIndexOf('/');
            return slash >= 0 && path.substring(slash + 1).equals(targetPath) && gatesPass(context, holder.value());
        });
    }

    /** Mirrors {@link BeeRegistries#gatesPass} against the reload's condition context. */
    private static boolean gatesPass(ICondition.IContext context, BeeData data) {
        for (String tag : data.requiredTags()) {
            if (itemOrBlockTagEmpty(context, tag)) return false;
        }
        for (String tag : data.forbiddenTags()) {
            if (!itemOrBlockTagEmpty(context, tag)) return false;
        }
        for (String tag : data.requiredFluidTags()) {
            if (context.getTag(TagKey.create(Registries.FLUID, Identifier.parse(tag))).isEmpty()) return false;
        }
        return true;
    }

    private static boolean itemOrBlockTagEmpty(ICondition.IContext context, String tag) {
        Identifier id = Identifier.parse(tag);
        return context.getTag(TagKey.create(Registries.ITEM, id)).isEmpty()
                && context.getTag(TagKey.create(Registries.BLOCK, id)).isEmpty();
    }

    @Override
    public String toString() {
        return "bee_exists(\"" + beeName + "\")";
    }
}
