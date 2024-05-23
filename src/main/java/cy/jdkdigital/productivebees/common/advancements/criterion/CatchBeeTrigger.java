package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CatchBeeTrigger extends SimpleCriterionTrigger<CatchBeeTrigger.TriggerInstance>
{
    @Override
    public Codec<CatchBeeTrigger.TriggerInstance> codec() {
        return CatchBeeTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack cage) {
        this.trigger(player, trigger -> trigger.test(cage));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String beeName) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<CatchBeeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(CatchBeeTrigger.TriggerInstance::player),
                                Codec.STRING.fieldOf("bee").forGetter(CatchBeeTrigger.TriggerInstance::beeName)
                        )
                        .apply(instance, CatchBeeTrigger.TriggerInstance::new)
        );

        public static CatchBeeTrigger.TriggerInstance any() {
            return new CatchBeeTrigger.TriggerInstance(Optional.empty(), "any");
        }

        public static CatchBeeTrigger.TriggerInstance create(String beeName) {
            return new CatchBeeTrigger.TriggerInstance(Optional.empty(), beeName);
        }

        public boolean test(ItemStack cage) {
            CompoundTag tag = cage.getTag();

            if (tag != null && tag.contains("type")) {
                String type = tag.getString("type");
                return this.beeName.equals("any") || type.equals(this.beeName);
            }

            return false;
        }
    }
}
