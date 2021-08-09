package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.animal.Bee;

import javax.annotation.Nonnull;

public class CalmBeeTrigger extends SimpleCriterionTrigger<CalmBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "calm_bee");

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
    protected Instance createInstance(JsonObject jsonObject, EntityPredicate.Composite andPredicate, DeserializationContext conditionArrayParser) {
        return new CalmBeeTrigger.Instance(GsonHelper.getAsString(jsonObject, "beeName"));
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        private final String beeName;

        public Instance(String beeName) {
            super(CalmBeeTrigger.ID, EntityPredicate.Composite.ANY);
            this.beeName = beeName;
        }

        public static CalmBeeTrigger.Instance any() {
            return new CalmBeeTrigger.Instance("any");
        }

        public static CalmBeeTrigger.Instance create(String beeName) {
            return new CalmBeeTrigger.Instance(beeName);
        }

        public boolean test(Bee bee) {
            String type = bee instanceof ConfigurableBee ? ((ConfigurableBee) bee).getBeeType() : bee.getEncodeId();

            return this.beeName.equals("any") || (type != null && type.equals(this.beeName));
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(SerializationContext serializer) {
            JsonObject jsonobject = super.serializeToJson(serializer);
            jsonobject.addProperty("beeName", this.beeName);
            return jsonobject;
        }
    }
}
