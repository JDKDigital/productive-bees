package cy.jdkdigital.productivebees.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

import java.util.Set;

public class ProductivityBonus extends LootFunction {
   private final RandomValueRange count;
   private final int limit;

   private ProductivityBonus(ILootCondition[] conditions, int limitIn) {
      super(conditions);
      this.limit = limitIn;
      this.count = new RandomValueRange(0, 1);
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.THIS_ENTITY);
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      Entity killer = context.get(LootParameters.KILLER_ENTITY);
      if (killer != null) {
         return stack;
      }

      Entity entity = context.get(LootParameters.THIS_ENTITY);
      if (entity instanceof ProductiveBeeEntity) {
         int productivity = ((ProductiveBeeEntity)entity).getAttributeValue(BeeAttributes.PRODUCTIVITY);
         if (productivity == 0) {
            return stack;
         }

         float f = (float)productivity * stack.getCount() * this.count.generateFloat(context.getRandom());
         stack.grow(Math.round(f));

         if (this.hasLimit() && stack.getCount() > this.limit) {
            stack.setCount(this.limit);
         }
      }

      return stack;
   }

   public static ProductivityBonus.Builder builder(RandomValueRange range) {
      return new ProductivityBonus.Builder(range);
   }

   public static class Builder extends LootFunction.Builder<ProductivityBonus.Builder> {
      private final RandomValueRange valueRange;
      private int limit = 0;

      public Builder(RandomValueRange valueRange) {
         this.valueRange = valueRange;
      }

      protected ProductivityBonus.Builder doCast() {
         return this;
      }

      public ProductivityBonus.Builder func_216072_a(int p_216072_1_) {
         this.limit = p_216072_1_;
         return this;
      }

      public ILootFunction build() {
         return new ProductivityBonus(this.getConditions(), this.limit);
      }
   }

   public static class Serializer extends LootFunction.Serializer<ProductivityBonus> {
      public Serializer() {
         super(new ResourceLocation(ProductiveBees.MODID, "productivity"), ProductivityBonus.class);
      }

      public void serialize(JsonObject object, ProductivityBonus functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         if (functionClazz.hasLimit()) {
            object.add("limit", serializationContext.serialize(functionClazz.limit));
         }
      }

      public ProductivityBonus deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         int i = JSONUtils.getInt(object, "limit", 0);
         return new ProductivityBonus(conditionsIn, i);
      }
   }
}