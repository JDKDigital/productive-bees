package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class SaddleBeeTrigger extends AbstractCriterionTrigger<SaddleBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "saddle_bee");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, BeeEntity bee) {
        this.trigger(player, trigger -> trigger.test(bee));
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject jsonObject, EntityPredicate.AndPredicate andPredicate, ConditionArrayParser conditionArrayParser) {
        return new SaddleBeeTrigger.Instance();
    }

    public static class Instance extends CriterionInstance
    {
        public Instance() {
            super(SaddleBeeTrigger.ID, EntityPredicate.AndPredicate.ANY);
        }

        public static SaddleBeeTrigger.Instance any() {
            return new SaddleBeeTrigger.Instance();
        }

        public static SaddleBeeTrigger.Instance create() {
            return new SaddleBeeTrigger.Instance();
        }

        public boolean test(BeeEntity bee) {
            return bee instanceof IEquipable && ((IEquipable) bee).isSaddled();
        }
    }
}
