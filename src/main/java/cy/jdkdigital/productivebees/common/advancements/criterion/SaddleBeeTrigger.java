package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Bee;

import java.util.Optional;

public class SaddleBeeTrigger extends SimpleCriterionTrigger<SaddleBeeTrigger.TriggerInstance>
{
    @Override
    public Codec<SaddleBeeTrigger.TriggerInstance> codec() {
        return SaddleBeeTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Bee bee) {
        this.trigger(player, trigger -> trigger.test(bee));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String beeName) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<SaddleBeeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SaddleBeeTrigger.TriggerInstance::player),
                        Codec.STRING.fieldOf("bee").forGetter(SaddleBeeTrigger.TriggerInstance::beeName)
                )
                .apply(instance, SaddleBeeTrigger.TriggerInstance::new)
        );

        public static SaddleBeeTrigger.TriggerInstance any() {
            return new SaddleBeeTrigger.TriggerInstance(Optional.empty(), "any");
        }

        public static SaddleBeeTrigger.TriggerInstance create(String beeName) {
            return new SaddleBeeTrigger.TriggerInstance(Optional.empty(), beeName);
        }

        public boolean test(Bee bee) {
            return bee instanceof Saddleable && ((Saddleable) bee).isSaddled();
        }
    }
}
