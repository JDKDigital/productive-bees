package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public class FakeIngredient implements ICustomIngredient
{
    public final String blockName;

    public FakeIngredient(String blockName) {
        this.blockName = blockName;
    }

    @Override
    public boolean test(ItemStack stack) {
        return false;
    }

    @Override
    public Stream<ItemStack> getItems() {
        return null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return null;
    }

    public JsonElement toJson() {
        JsonArray jsonarray = new JsonArray();

        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", blockName);
        jsonarray.add(jsonobject);

        return jsonarray;
    }
}