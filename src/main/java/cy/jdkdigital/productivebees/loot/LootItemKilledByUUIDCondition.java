package cy.jdkdigital.productivebees.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;
import java.util.UUID;

public class LootItemKilledByUUIDCondition implements LootItemCondition
{
   private final UUID uuid;

   private LootItemKilledByUUIDCondition(UUID uuid) {
      this.uuid = uuid;
   }

   @Override
   public LootItemConditionType getType() {
      return ProductiveBees.KILLED_BY_UUID.get();
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   @Override
   public boolean test(LootContext context) {
      if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
         return context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER) && context.getParam(LootContextParams.LAST_DAMAGE_PLAYER).getUUID().equals(uuid);
      }
      return false;
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemKilledByUUIDCondition>
   {
      @Override
      public void serialize(JsonObject json, LootItemKilledByUUIDCondition condition, JsonSerializationContext context) {
         json.addProperty("uuid", condition.uuid.toString());
      }

      @Override
      public LootItemKilledByUUIDCondition deserialize(JsonObject json, JsonDeserializationContext context) {
         return new LootItemKilledByUUIDCondition(UUID.fromString(json.get("uuid").getAsString()));
      }
   }
}