package cy.jdkdigital.productivebees.common.crafting.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;

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
        if (lookup.get(ResourceKey.create(BeeRegistries.BEE_DATA, beeName)).isPresent()) {
            return true;
        }
        // Fall back to matching the path's last segment
        String targetPath = beeName.getPath();
        String targetNs = beeName.getNamespace();
        return lookup.listElements().anyMatch(holder -> {
            Identifier id = holder.key().identifier();
            if (!id.getNamespace().equals(targetNs)) return false;
            String path = id.getPath();
            int slash = path.lastIndexOf('/');
            return slash >= 0 && path.substring(slash + 1).equals(targetPath);
        });
    }

    @Override
    public String toString() {
        return "bee_exists(\"" + beeName + "\")";
    }
}
