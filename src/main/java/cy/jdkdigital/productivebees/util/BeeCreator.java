package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
        data.putBoolean("inverseFlower", false);
        if (json.has("flowerTag") && !json.get("flowerTag").getAsString().isEmpty()) {
            String tagName = json.get("flowerTag").getAsString();
            if (tagName.startsWith("!")) {
                tagName = tagName.substring(1);
                data.putBoolean("inverseFlower", true);
            }
            data.putString("flowerTag", tagName);
        } else if (json.has("flowerBlock") && !json.get("flowerBlock").getAsString().isEmpty()) {
            data.putString("flowerBlock", json.get("flowerBlock").getAsString());
        } else if (json.has("flowerFluid") && !json.get("flowerFluid").getAsString().isEmpty()) {
            data.putString("flowerFluid", json.get("flowerFluid").getAsString());
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

        if (json.has("postPollination")) {
            data.putString("postPollination", json.get("postPollination").getAsString());
        }

        data.putBoolean("colorCycle", json.has("colorCycle") && json.get("colorCycle").getAsBoolean());

        data.putString("breedingItem", json.has("breedingItem") ? json.get("breedingItem").getAsString() : "");
        data.putInt("breedingItemCount", json.has("breedingItemCount") ? json.get("breedingItemCount").getAsInt() : 1);
        data.putString("flowerType", json.has("flowerType") ? json.get("flowerType").getAsString() : "blocks");
        data.putString("renderer", json.has("renderer") ? json.get("renderer").getAsString() : "default");
        data.putString("renderTransform", json.has("renderTransform") ? json.get("renderTransform").getAsString() : "none");
        data.putString("particleType", json.has("particleType") ? json.get("particleType").getAsString() : "drip");
        data.putFloat("size", json.has("size") ? json.get("size").getAsFloat() : 1.0f);
        data.putFloat("pollinatedSize", json.has("pollinatedSize") ? json.get("pollinatedSize").getAsFloat() : data.getFloat("size"));
        data.putFloat("speed", json.has("speed") ? json.get("speed").getAsFloat() : 1.0f);
        data.putDouble("attack", json.has("attack") ? json.get("attack").getAsFloat() : 2.0D);

        data.putBoolean("translucent", (json.has("translucent") && json.get("translucent").getAsBoolean()) || data.getString("renderer").equals("translucent_with_center"));
        data.putBoolean("useGlowLayer", !json.has("useGlowLayer") || json.get("useGlowLayer").getAsBoolean());
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
        data.putBoolean("selfheal", json.has("selfheal") && json.get("selfheal").getAsBoolean());
        data.putBoolean("irradiated", json.has("irradiated") && json.get("irradiated").getAsBoolean());

        ListTag invulnerability = new ListTag();
        if (json.has("invulnerability")) {
            for (JsonElement damageSource: json.get("invulnerability").getAsJsonArray()) {
                invulnerability.add(StringTag.valueOf(damageSource.getAsString()));
            }
        }
        data.put("invulnerability", invulnerability);

        if (json.has("attributes")) {
            for (Map.Entry<String, JsonElement> entry : json.get("attributes").getAsJsonObject().entrySet()) {
                switch (entry.getKey()) {
                    case "productivity", "endurance", "temper", "behavior", "weather_tolerance" -> data.putInt(entry.getKey(), entry.getValue().getAsInt());
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
