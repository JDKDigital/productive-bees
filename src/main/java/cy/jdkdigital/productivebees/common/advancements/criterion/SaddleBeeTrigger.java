package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BumbleBeeEntity;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class SaddleBeeTrigger extends AbstractCriterionTrigger<SaddleBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "saddle_bee");

    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    public SaddleBeeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new SaddleBeeTrigger.Instance();
    }

    public void trigger(ServerPlayerEntity player, BeeEntity bee) {
        this.func_227070_a_(player.getAdvancements(), (trigger) -> trigger.test(bee));
    }

    public static class Instance extends CriterionInstance
    {
        public Instance() {
            super(SaddleBeeTrigger.ID);
        }

        public static SaddleBeeTrigger.Instance any() {
            return new SaddleBeeTrigger.Instance();
        }

        public static SaddleBeeTrigger.Instance create() {
            return new SaddleBeeTrigger.Instance();
        }

        public boolean test(BeeEntity bee) {
            return bee instanceof BumbleBeeEntity && ((BumbleBeeEntity) bee).getSaddled();
        }

        @Nonnull
        public JsonElement serialize() {
            return new JsonObject();
        }
    }
}
