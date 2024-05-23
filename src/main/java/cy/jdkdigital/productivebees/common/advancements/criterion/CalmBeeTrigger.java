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

public class CalmBeeTrigger extends SimpleCriterionTrigger<CalmBeeTrigger.TriggerInstance>
{
    @Override
    public Codec<CalmBeeTrigger.TriggerInstance> codec() {
        return CalmBeeTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Bee bee) {
        this.trigger(player, trigger -> trigger.test(bee));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String beeName) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<CalmBeeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(CalmBeeTrigger.TriggerInstance::player),
                Codec.STRING.fieldOf("bee").forGetter(CalmBeeTrigger.TriggerInstance::beeName)
            )
            .apply(instance, CalmBeeTrigger.TriggerInstance::new)
        );

        public static CalmBeeTrigger.TriggerInstance any() {
            return new CalmBeeTrigger.TriggerInstance(Optional.empty(), "any");
        }

        public static CalmBeeTrigger.TriggerInstance create(String beeName) {
            return new CalmBeeTrigger.TriggerInstance(Optional.empty(), beeName);
        }

        public boolean test(Bee bee) {
            String type = bee instanceof ConfigurableBee ? ((ConfigurableBee) bee).getBeeType() : bee.getEncodeId();

            return this.beeName.equals("any") || (type != null && type.equals(this.beeName));
        }
    }
}
