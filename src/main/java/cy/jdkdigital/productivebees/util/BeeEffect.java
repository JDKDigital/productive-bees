package cy.jdkdigital.productivebees.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class BeeEffect implements INBTSerializable<CompoundTag>
{
    private Map<MobEffect, Integer> effects = new HashMap<>();

    public BeeEffect(Map<MobEffect, Integer> effects) {
        this.effects = effects;
    }

    public BeeEffect(HolderLookup.Provider provider, CompoundTag tag) {
        deserializeNBT(provider, tag);
    }

    public Map<MobEffect, Integer> getEffects() {
        return effects;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("i", effects.size());
        getEffects().forEach((effect, duration) -> {
            CompoundTag effectTag = new CompoundTag();
            effectTag.putString("effect", "" + BuiltInRegistries.MOB_EFFECT.getKey(effect));
            effectTag.putInt("duration", duration);

            tag.put("effect_" + (tag.size() - 1), effectTag);
        });

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.effects = new HashMap<>();
        IntStream.range(0, tag.getInt("i")).forEach(
            i -> {
                CompoundTag effectTag = tag.getCompound("effect_" + i);
                String effectName = effectTag.getString("effect");

                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(effectName));

                this.effects.put(effect, effectTag.getInt("duration"));
            }
        );
    }
}
