package cy.jdkdigital.productivebees.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class OptionalLootItem extends LootPoolSingletonContainer {
    final Item item;

    protected OptionalLootItem(Item pItem, int pWeight, int pQuality, LootItemCondition[] pConditions, LootItemFunction[] pFunctions) {
        super(pWeight, pQuality, pConditions, pFunctions);
        this.item = pItem;
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike item) {
        return simpleBuilder((weight, quality, conditions, functions) -> {
            return new OptionalLootItem(item.asItem(), weight, quality, conditions, functions);
        });
    }

    public LootPoolEntryType getType() {
        return ProductiveBees.OPTIONAL_LOOT_ITEM.get();
    }

    public void createItemStack(Consumer<ItemStack> pStackConsumer, LootContext pLootContext) {
        pStackConsumer.accept(new ItemStack(this.item));
    }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<OptionalLootItem> {
        public void serializeCustom(JsonObject pObject, OptionalLootItem pContext, JsonSerializationContext pConditions) {
            super.serializeCustom(pObject, pContext, pConditions);
            ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(pContext.item);
            if (resourcelocation == null) {
                pObject.addProperty("name", "minecraft:air");
            } else {
                pObject.addProperty("name", resourcelocation.toString());
            }
        }

        protected OptionalLootItem deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, LootItemFunction[] pFunctions) {
            Item item;
            try {
                item = GsonHelper.getAsItem(pObject, "name");
            } catch (Exception e) {
                item = Items.AIR;
            }
            return new OptionalLootItem(item, pWeight, pQuality, pConditions, pFunctions);
        }
    }
}
