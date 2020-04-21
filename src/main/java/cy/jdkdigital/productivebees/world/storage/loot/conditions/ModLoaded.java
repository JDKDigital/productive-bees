package cy.jdkdigital.productivebees.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.fml.ModList;

public class ModLoaded implements ILootCondition {
    private final String modId;
    private boolean inverted;

    private ModLoaded(String modId, boolean inverted) {
        this.modId = modId;
        this.inverted = inverted;
    }

    public boolean test(LootContext ctx) {
        return inverted != ModList.get().isLoaded(modId);
    }

    public static IBuilder builder(String modId, boolean inverted) {
        return () -> new ModLoaded(modId, inverted);
    }

    public static class Serializer extends AbstractSerializer<ModLoaded> {
        public Serializer() {
            super(new ResourceLocation(ProductiveBees.MODID, "mod_loaded"), ModLoaded.class);
        }

        public void serialize(JsonObject json, ModLoaded condition, JsonSerializationContext ctx) {
            json.addProperty("modId", condition.modId);
            json.addProperty("inverted", condition.inverted);
        }

        public ModLoaded deserialize(JsonObject json, JsonDeserializationContext ctx) {
            return new ModLoaded(JSONUtils.getString(json, "modId"), JSONUtils.getBoolean(json, "inverted", false));
        }
    }
}
