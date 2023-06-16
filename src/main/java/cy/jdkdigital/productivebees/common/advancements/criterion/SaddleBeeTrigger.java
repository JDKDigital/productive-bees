package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Bee;

import javax.annotation.Nonnull;

public class SaddleBeeTrigger extends SimpleCriterionTrigger<SaddleBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "saddle_bee");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, Bee bee) {
        this.trigger(player, trigger -> trigger.test(bee));
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject jsonObject, ContextAwarePredicate andPredicate, DeserializationContext conditionArrayParser) {
        return new SaddleBeeTrigger.Instance();
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        public Instance() {
            super(SaddleBeeTrigger.ID, ContextAwarePredicate.ANY);
        }

        public static SaddleBeeTrigger.Instance any() {
            return new SaddleBeeTrigger.Instance();
        }

        public static SaddleBeeTrigger.Instance create() {
            return new SaddleBeeTrigger.Instance();
        }

        public boolean test(Bee bee) {
            return bee instanceof Saddleable && ((Saddleable) bee).isSaddled();
        }
    }
}
