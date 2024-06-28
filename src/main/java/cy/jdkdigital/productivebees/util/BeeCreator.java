package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Map;

public class BeeCreator
{
//    public static MapCodec<BeeObject> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
//            Codec.STRING.fieldOf("primaryColor").forGetter(o -> o.primaryColor)
//    ).apply(instance, BeeObject::new));

    public static CompoundTag create(ResourceLocation id, JsonObject json) {
        CompoundTag data = new CompoundTag();

        data.putString("id", id.toString());

        data.putInt("primaryColor", getColor(json, "primaryColor"));
        data.putInt("secondaryColor", json.has("secondaryColor") ? getColor(json, "secondaryColor") : data.getInt("primaryColor"));

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
        } else if (json.has("flowerItem") && !json.get("flowerItem").getAsString().isEmpty()) {
            data.putString("flowerItem", json.get("flowerItem").getAsString());
        } else {
            // default to flowers
            data.putString("flowerTag", "minecraft:flowers");
        }
        if (json.has("nestingPreference")) {
            data.putString("nestingPreference", json.get("nestingPreference").getAsString());
        }
        if (json.has("beeTexture")) {
            data.putString("beeTexture", json.get("beeTexture").getAsString());
        }
        if (json.has("particleColor")) {
            data.putInt("particleColor", getColor(json, "particleColor"));
        }

        data.putInt("tertiaryColor", json.has("tertiaryColor") ? getColor(json, "tertiaryColor") : data.getInt("primaryColor"));

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

        // TODO 1.21 reimplement or make this whole thing into a codec
//        if (json.has("passiveEffects")) {
//            Map<Holder<MobEffect>, Integer> effects = new HashMap<>();
//            for (JsonElement el : json.get("passiveEffects").getAsJsonArray()) {
//                JsonObject effect = el.getAsJsonObject();
//                effects.put(Holder.direct(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effect.get("effect").getAsString()))), effect.get("duration").getAsInt());
//            }
//            data.put("effect", new BeeEffect(effects).serializeNBT());
//        }

        data.putBoolean("createComb", !json.has("createComb") || json.get("createComb").getAsBoolean());

        return data;
    }

    public static void setType(ResourceLocation type, ItemStack stack) {
        stack.set(ModDataComponents.BEE_TYPE, type);
    }

    public static ItemStack getSpawnEgg(ResourceLocation beeType) {
        ItemStack egg;
        if (BeeReloadListener.INSTANCE.getData(beeType) != null) {
            egg = new ItemStack(ModItems.CONFIGURABLE_SPAWN_EGG.get());
            var tag = new CompoundTag();
            tag.putString("type", beeType.toString());
            tag.putString("id", ModEntities.CONFIGURABLE_BEE.getId().toString());
            egg.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
        } else {
            if (beeType.getNamespace().equals(ProductiveBees.MODID)) {
                egg = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(beeType.getNamespace(), "spawn_egg_" + beeType.getPath())));
            } else {
                egg = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(beeType.getNamespace(), beeType.getPath() + "_spawn_egg")));
            }
        }
        return egg;
    }

    private static Integer getColor(JsonObject json, String el) {
        var c = json.get(el).getAsString().replace("#", "").split("(?<=\\G.{2})");
        return FastColor.ARGB32.color(Integer.parseInt(c[0], 16), Integer.parseInt(c[1], 16), Integer.parseInt(c[2], 16));
    }

//    public record BeeObject(String primaryColor) {
//
//    }
}
