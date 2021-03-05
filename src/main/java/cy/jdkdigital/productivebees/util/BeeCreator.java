package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.item.ItemStack;
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

        data.putString("name", json.has("name") ? json.get("name").getAsString() : idToName(id.getPath()) + " Bee");

        if (json.has("description")) {
            data.putString("description", json.get("description").getAsString());
        }
        if (json.has("flowerTag") && !json.get("flowerTag").getAsString().isEmpty()) {
            data.putString("flowerTag", json.get("flowerTag").getAsString());
        }
        else if (json.has("flowerBlock") && !json.get("flowerBlock").getAsString().isEmpty()) {
            data.putString("flowerBlock", json.get("flowerBlock").getAsString());
        }
        if (json.has("nestingPreference")) {
            data.putString("nestingPreference", json.get("nestingPreference").getAsString());
        }
        if (json.has("beeTexture")) {
            data.putString("beeTexture", json.get("beeTexture").getAsString());
        }
        if (json.has("particleColor")) {
            data.putInt("particleColor", Color.decode(json.get("particleColor").getAsString()).getRGB());
        }
        if (json.has("tertiaryColor")) {
            data.putInt("tertiaryColor", Color.decode(json.get("tertiaryColor").getAsString()).getRGB());
        }
        if (json.has("attackResponse")) {
            data.putString("attackResponse", json.get("attackResponse").getAsString());
        }

        data.putString("flowerType", json.has("flowerType") ? json.get("flowerType").getAsString() : "block");
        data.putString("renderer", json.has("renderer") ? json.get("renderer").getAsString() : "default");
        data.putString("particleType", json.has("particleType") ? json.get("particleType").getAsString() : "drip");
        data.putFloat("size", json.has("size") ? json.get("size").getAsFloat() : 1.0f);

        data.putBoolean("translucent", (json.has("translucent") && json.get("translucent").getAsBoolean()) || data.getString("renderer").equals("translucent_with_center"));
        data.putBoolean("glowingInnards", json.has("glowingInnards") && json.get("glowingInnards").getAsBoolean());
        data.putBoolean("fireproof", json.has("fireproof") && json.get("fireproof").getAsBoolean());
        data.putBoolean("withered", json.has("withered") && json.get("withered").getAsBoolean());
        data.putBoolean("blinding", json.has("blinding") && json.get("blinding").getAsBoolean());
        data.putBoolean("draconic", json.has("draconic") && json.get("draconic").getAsBoolean());
        data.putBoolean("slimy", json.has("slimy") && json.get("slimy").getAsBoolean());
        data.putBoolean("teleporting", json.has("teleporting") && json.get("teleporting").getAsBoolean());
        data.putBoolean("munchies", json.has("munchies") && json.get("munchies").getAsBoolean());
        data.putBoolean("redstoned", json.has("redstoned") && json.get("redstoned").getAsBoolean());
        data.putBoolean("stringy", json.has("stringy") && json.get("stringy").getAsBoolean());
        data.putBoolean("stingless", json.has("stingless") && json.get("stingless").getAsBoolean());
        data.putBoolean("selfbreed", !json.has("selfbreed") || json.get("selfbreed").getAsBoolean());

        if (json.has("attributes")) {
            for (Map.Entry<String, JsonElement> entry : json.get("attributes").getAsJsonObject().entrySet()) {
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
            for (JsonElement el : json.get("passiveEffects").getAsJsonArray()) {
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

    public static void setTag(String type, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateChildTag("EntityTag");
        tag.putString("type", type);
    }

    public static ItemStack getSpawnEgg(String beeType) {
        ItemStack egg;
        if (BeeReloadListener.INSTANCE.getData(beeType) != null) {
            egg = new ItemStack(ModItems.CONFIGURABLE_SPAWN_EGG.get());
            setTag(beeType, egg);
        } else {
            ResourceLocation name = new ResourceLocation(beeType);
            if (name.getNamespace().equals(ProductiveBees.MODID)) {
                egg = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name.getNamespace(), "spawn_egg_" + name.getPath())));
            } else {
                egg = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name.getNamespace(), name.getPath() + "_spawn_egg")));
            }
        }
        return egg;
    }
}
