package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class FakeIngredient extends Ingredient
{
    private final String blockName;

    public FakeIngredient(String blockName) {
        super(Stream.of(new Ingredient.ItemValue(ItemStack.EMPTY)));
        this.blockName = blockName;
    }

    @Override
    public JsonElement toJson() {
        JsonArray jsonarray = new JsonArray();

        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", blockName);
        jsonarray.add(jsonobject);

        return jsonarray;
    }
}