package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class BeeCreator
{
    public static CompoundTag create(ResourceLocation id, JsonObject json) {
        CompoundTag data = new CompoundTag();

        data.putString("id", id.toString());

        TextColor primary = TextColor.parseColor(json.has("primaryColor") ? json.get("primaryColor").getAsString() : "#edc343");
        TextColor secondary = TextColor.parseColor(json.has("secondaryColor") ? json.get("secondaryColor").getAsString() : "#804f40");
        data.putInt("primaryColor", primary.getValue());
        data.putInt("secondaryColor", secondary.getValue());

        if (json.has("description")) {
            data.putString("description", json.get("description").getAsString());
        }
        if (json.has("flowerTag") && !json.get("flowerTag").getAsString().isEmpty()) {
            data.putString("flowerTag", json.get("flowerTag").getAsString());
        } else if (json.has("flowerBlock") && !json.get("flowerBlock").getAsString().isEmpty()) {
            data.putString("flowerBlock", json.get("flowerBlock").getAsString());
        }
        if (json.has("nestingPreference")) {
            data.putString("nestingPreference", json.get("nestingPreference").getAsString());
        }
        if (json.has("beeTexture")) {
            data.putString("beeTexture", json.get("beeTexture").getAsString());
        }
        if (json.has("combTexture")) {
            data.putString("combTexture", json.get("combTexture").getAsString());
        }
        if (json.has("particleColor")) {
            data.putInt("particleColor", TextColor.parseColor(json.get("particleColor").getAsString()).getValue());
        }

        data.putInt("tertiaryColor", json.has("tertiaryColor") ? TextColor.parseColor(json.get("tertiaryColor").getAsString()).getValue() : data.getInt("primaryColor"));

        if (json.has("attackResponse")) {
            data.putString("attackResponse", json.get("attackResponse").getAsString());
        }

        data.putString("flowerType", json.has("flowerType") ? json.get("flowerType").getAsString() : "block");
        data.putString("renderer", json.has("renderer") ? json.get("renderer").getAsString() : "default");
        data.putString("renderTransform", json.has("renderTransform") ? json.get("renderTransform").getAsString() : "none");
        data.putString("particleType", json.has("particleType") ? json.get("particleType").getAsString() : "drip");
        data.putFloat("size", json.has("size") ? json.get("size").getAsFloat() : 1.0f);

        data.putBoolean("translucent", (json.has("translucent") && json.get("translucent").getAsBoolean()) || data.getString("renderer").equals("translucent_with_center"));
        data.putBoolean("useGlowLayer", (!json.has("useGlowLayer") || json.get("useGlowLayer").getAsBoolean()));
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
        data.putBoolean("waterproof", json.has("waterproof") && json.get("waterproof").getAsBoolean());
        data.putBoolean("coldResistant", json.has("coldResistant") && json.get("coldResistant").getAsBoolean());
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
            Map<MobEffect, Integer> effects = new HashMap<>();
            for (JsonElement el : json.get("passiveEffects").getAsJsonArray()) {
                JsonObject effect = el.getAsJsonObject();
                effects.put(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effect.get("effect").getAsString())), effect.get("duration").getAsInt());
            }
            data.put("effect", new BeeEffect(effects).serializeNBT());
        }

        data.putBoolean("createComb", !json.has("createComb") || json.get("createComb").getAsBoolean());

        return data;
    }

    public static void setTag(String type, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("EntityTag");
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
