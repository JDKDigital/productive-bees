package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CalmBeeTrigger extends AbstractCriterionTrigger<CalmBeeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "calm_bee");

    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    public CalmBeeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new CalmBeeTrigger.Instance(JSONUtils.getString(json, "beeName"));
    }

    public void trigger(ServerPlayerEntity player, BeeEntity bee) {
        this.func_227070_a_(player.getAdvancements(), (trigger) -> trigger.test(bee));
    }

    public static class Instance extends CriterionInstance
    {
        private final String beeName;

        public Instance(String beeName) {
            super(CalmBeeTrigger.ID);
            this.beeName = beeName;
        }

        public static CalmBeeTrigger.Instance any() {
            return new CalmBeeTrigger.Instance("any");
        }

        public static CalmBeeTrigger.Instance create(String beeName) {
            return new CalmBeeTrigger.Instance(beeName);
        }

        public boolean test(BeeEntity bee) {
            String type = bee instanceof ConfigurableBeeEntity ? ((ConfigurableBeeEntity) bee).getBeeType() : bee.getEntityString();

            ProductiveBees.LOGGER.info("test: " + this.beeName + " type: " + type);

            return this.beeName.equals("any") || type.equals(this.beeName);
        }

        @Nonnull
        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("beeName", this.beeName);
            return jsonobject;
        }
    }
}
