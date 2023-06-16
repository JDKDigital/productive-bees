package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.advancements.critereon.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class CatchBeeTrigger extends SimpleCriterionTrigger<CatchBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "catch_bee");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, ItemStack cage) {
        this.trigger(player, trigger -> trigger.test(cage));
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject jsonObject, ContextAwarePredicate andPredicate, DeserializationContext conditionArrayParser) {
        return new CatchBeeTrigger.Instance(GsonHelper.getAsString(jsonObject, "beeName"));
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        private final String beeName;

        public Instance(String beeName) {
            super(CatchBeeTrigger.ID, ContextAwarePredicate.ANY);
            this.beeName = beeName;
        }

        public static CatchBeeTrigger.Instance any() {
            return new CatchBeeTrigger.Instance("any");
        }

        public static CatchBeeTrigger.Instance create(String beeName) {
            return new CatchBeeTrigger.Instance(beeName);
        }

        public boolean test(ItemStack cage) {
            CompoundTag tag = cage.getTag();

            if (tag != null && tag.contains("type")) {
                String type = tag.getString("type");
                // /advancement revoke @p only productivebees:husbandry/bee_cage/quartz_nest/catch_crystalline_bee
                return this.beeName.equals("any") || type.equals(this.beeName);
            }

            return false;
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
