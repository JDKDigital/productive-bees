package cy.jdkdigital.productivebees.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class BeeEffect implements INBTSerializable<CompoundNBT>
{
    private Map<Effect, Integer> effects = new HashMap<>();

    public BeeEffect() {
    }

    public BeeEffect(Map<Effect, Integer> effects) {
        this.effects = effects;
    }

    public BeeEffect(CompoundNBT tag) {
        deserializeNBT(tag);
    }

    public Map<Effect, Integer> getEffects() {
        return effects;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        tag.putInt("i", effects.size());
        getEffects().forEach((effect, duration) -> {
            CompoundNBT effectTag = new CompoundNBT();
            effectTag.putString("effect","" + effect.getRegistryName());
            effectTag.putInt("duration", duration);

            tag.put("effect_" + (tag.size() - 1), effectTag);
        });

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        this.effects = new HashMap<>();
        IntStream.range(0, tag.getInt("i")).forEach(
            i -> {
                CompoundNBT effectTag = (CompoundNBT) tag.get("effect_" + i);
                String effectName = effectTag.getString("effect");

                Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectName));

                this.effects.put(effect, effectTag.getInt("duration"));
            }
        );
    }
}
