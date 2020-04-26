package cy.jdkdigital.productivebees.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraftforge.registries.ForgeRegistries;

public class ConvertToComb extends LootFunction {

   private String itemName;

   private ConvertToComb(ILootCondition[] condition, String itemName) {
      super(condition);
      this.itemName = itemName;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      ProductiveBees.LOGGER.info("ConverToComb doApply: " + itemName + " " + stack + " " + stack.getItem().getName());
      switch (itemName) {
         case "minecraft:emerald":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:emerald_honeycomb")));
            break;
         case "minecraft:diamond":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:diamond_honeycomb")));
            break;
         case "minecraft:redstone":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:redstone_honeycomb")));
            break;
         case "minecraft:lapis_lazuli":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:lapis_honeycomb")));
            break;
         case "minecraft:quartz":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:quartz_honeycomb")));
            break;
         case "minecraft:ender_pearl":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:ender_honeycomb")));
            break;
         case "minecraft:iron_ingot":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:iron_honeycomb")));
            break;
         case "minecraft:gold_ingot":
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("beesourceful:gold_honeycomb")));
            break;
      }
      return stack;
   }

   public static ConvertToComb.Builder builder() {
      return new ConvertToComb.Builder();
   }

   public static class Builder extends LootFunction.Builder<ConvertToComb.Builder> {
      private String itemName;

      public Builder() {}

      protected ConvertToComb.Builder doCast() {
         return this;
      }

      public ConvertToComb.Builder func_216072_a(String itemName) {
         this.itemName = itemName;
         return this;
      }

      public ILootFunction build() {
         return new ConvertToComb(this.getConditions(), this.itemName);
      }
   }

   public static class Serializer extends LootFunction.Serializer<ConvertToComb> {
      public Serializer() {
         super(new ResourceLocation(ProductiveBees.MODID, "convert_to_comb"), ConvertToComb.class);
      }

      public void serialize(JsonObject object, ConvertToComb functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.add("itemName", serializationContext.serialize(functionClazz.itemName));
      }

      public ConvertToComb deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         return new ConvertToComb(conditionsIn, JSONUtils.getString(object, "itemName"));
      }
   }
}