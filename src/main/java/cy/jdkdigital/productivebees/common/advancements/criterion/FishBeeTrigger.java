package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Bee;

import java.util.Optional;

public class FishBeeTrigger extends SimpleCriterionTrigger<FishBeeTrigger.TriggerInstance>
{
    @Override
    public Codec<FishBeeTrigger.TriggerInstance> codec() {
        return FishBeeTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Bee bee) {
        this.trigger(player, trigger -> trigger.test(bee));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String beeName) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<FishBeeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(FishBeeTrigger.TriggerInstance::player),
                                Codec.STRING.fieldOf("bee").forGetter(FishBeeTrigger.TriggerInstance::beeName)
                        )
                        .apply(instance, FishBeeTrigger.TriggerInstance::new)
        );

        public static TriggerInstance any() {
            return new TriggerInstance(Optional.empty(), "any");
        }

        public static TriggerInstance create(String beeName) {
            return new TriggerInstance(Optional.empty(), beeName);
        }

        public boolean test(Bee bee) {
            String type = bee instanceof ConfigurableBee ? ((ConfigurableBee) bee).getBeeType() : bee.getEncodeId();

            return this.beeName.equals("any") || (type != null && type.equals(this.beeName));
        }
    }
}
