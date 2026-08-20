package cy.jdkdigital.productivebees.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class BeeEffect
{
    private Map<Holder<MobEffect>, Integer> effects = new HashMap<>();

    public BeeEffect(Map<Holder<MobEffect>, Integer> effects) {
        this.effects = effects;
    }

    public BeeEffect(HolderLookup.Provider provider, CompoundTag tag) {
        deserializeNBT(provider, tag);
    }

    public Map<Holder<MobEffect>, Integer> getEffects() {
        return effects;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("i", effects.size());
        getEffects().forEach((effect, duration) -> {
            CompoundTag effectTag = new CompoundTag();
            effectTag.putString("effect", "" + BuiltInRegistries.MOB_EFFECT.getKey(effect.value()));
            effectTag.putInt("duration", duration);

            tag.put("effect_" + (tag.size() - 1), effectTag);
        });

        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.effects = new HashMap<>();
        IntStream.range(0, tag.getInt("i").orElse(0)).forEach(
            i -> {
                CompoundTag effectTag = tag.getCompound("effect_" + i).orElse(new CompoundTag());
                String effectName = effectTag.getString("effect").orElse("");

                BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(effectName)).ifPresent(holder -> {
                    this.effects.put(holder, effectTag.getInt("duration").orElse(0));
                });
            }
        );
    }
}
