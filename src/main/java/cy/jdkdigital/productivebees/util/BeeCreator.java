package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BeeCreator
{
    public static CompoundNBT create(ResourceLocation id, JsonObject json) {
        CompoundNBT data = new CompoundNBT();

        data.putString("id", id.toString());

        Color primary = Color.decode(json.has("primaryColor") ? json.get("primaryColor").getAsString() : "#edc343");
        Color secondary = Color.decode(json.has("secondaryColor") ? json.get("secondaryColor").getAsString() : "#804f40");
        data.putInt("primaryColor", primary.getRGB());
        data.putInt("secondaryColor", secondary.getRGB());

        data.putString("name", json.has("name") ? json.get("name").getAsString() : idToName(id.getPath()));

        if (json.has("description")) {
            data.putString("description", json.get("description").getAsString());
        }
        if (json.has("flowerTag")) {
            data.putString("flowerTag", json.get("flowerTag").getAsString());
        }
        if (json.has("beeTexture")) {
            data.putString("beeTexture", json.get("beeTexture").getAsString());
        }
        if (json.has("attackResponse")) {
            data.putString("attackResponse", json.get("attackResponse").getAsString());
        }
        if (json.has("attributes")) {
            for(Map.Entry<String, JsonElement> entry: json.get("attributes").getAsJsonObject().entrySet()) {
                switch (entry.getKey()) {
                    case "productivity":
                    case "endurance":
                    case "temper":
                    case "behavior":
                    case "weather_tolerance":
                        data.putInt(entry.getKey(), entry.getValue().getAsInt());
                }
            }
        }
        if (json.has("passiveEffects")) {
            Map<Effect, Integer> effects = new HashMap<>();
            for(JsonElement el: json.get("attributes").getAsJsonArray()) {
                JsonObject effect = el.getAsJsonObject();
                effects.put(ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect.get("effect").getAsString())), effect.get("duration").getAsInt());
            }
            data.put("effect", new BeeEffect(effects).serializeNBT());
        }

        data.putBoolean("createComb", !json.has("createComb") || json.get("createComb").getAsBoolean());

        return data;
    }

    public static String idToName(String givenString) {
        String[] arr = givenString.replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
