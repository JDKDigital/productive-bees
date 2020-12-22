package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CatchBeeTrigger extends AbstractCriterionTrigger<CatchBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "catch_bee");

    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack cage) {
        this.triggerListeners(player, (trigger) -> trigger.test(cage));
    }

    @Nonnull
    @Override
    protected Instance deserializeTrigger(JsonObject jsonObject, EntityPredicate.AndPredicate andPredicate, ConditionArrayParser conditionArrayParser) {
        return new CatchBeeTrigger.Instance(JSONUtils.getString(jsonObject, "beeName"));
    }

    public static class Instance extends CriterionInstance
    {
        private final String beeName;

        public Instance(String beeName) {
            super(CatchBeeTrigger.ID, EntityPredicate.AndPredicate.ANY_AND);
            this.beeName = beeName;
        }

        public static CatchBeeTrigger.Instance any() {
            return new CatchBeeTrigger.Instance("any");
        }

        public static CatchBeeTrigger.Instance create(String beeName) {
            return new CatchBeeTrigger.Instance(beeName);
        }

        public boolean test(ItemStack cage) {
            CompoundNBT tag = cage.getOrCreateTag();

            if (tag.contains("type")) {
                String type = tag.getString("type");
                // /advancement revoke @p only productivebees:husbandry/bee_cage/quartz_nest/catch_crystalline_bee
                return this.beeName.equals("any") || type.equals(this.beeName);
            }

            return false;
        }

        @Nonnull
        public JsonObject serialize(ConditionArraySerializer serializer) {
            JsonObject jsonobject = super.serialize(serializer);
            jsonobject.addProperty("beeName", this.beeName);
            return jsonobject;
        }
    }
}
