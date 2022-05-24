package cy.jdkdigital.productivebees.event.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class SturdyCageModifier extends LootModifier
{
    private final Item addition;

    protected SturdyCageModifier(LootItemCondition[] conditionsIn, Item addition) {
        super(conditionsIn);
        this.addition = addition;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> list, LootContext lootContext) {
        if (lootContext.getRandom().nextBoolean()) {
            list.add(new ItemStack(addition, 1));
        }
        return list;
    }

    public static class Serializer extends GlobalLootModifierSerializer<SturdyCageModifier>
    {
        @Override
        public SturdyCageModifier read(ResourceLocation resourceLocation, JsonObject jsonObject, LootItemCondition[] lootItemConditions) {
            Item addition = ForgeRegistries.ITEMS.getValue(new ResourceLocation((GsonHelper.getAsString(jsonObject, "addition"))));
            return new SturdyCageModifier(lootItemConditions, addition);
        }

        @Override
        public JsonObject write(SturdyCageModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("addition", ForgeRegistries.ITEMS.getKey(instance.addition).toString());
            return json;
        }
    }
}
