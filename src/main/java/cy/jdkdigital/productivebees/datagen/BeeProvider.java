package cy.jdkdigital.productivebees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BeeProvider implements DataProvider
{
    private final PackOutput output;
    public BeeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        PackOutput.PathProvider beePath = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "productivebees");

        List<CompletableFuture<?>> output = new ArrayList<>();

        Map<ResourceLocation, Supplier<JsonElement>> bees = Maps.newHashMap();
        // Iterate bees and create json files

        getBeeConfigs().forEach(beeConfig -> {
            bees.put(new ResourceLocation(ProductiveBees.MODID, beeConfig.name), getBee(beeConfig));
        });

        bees.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cachedOutput, supplier.get(), beePath.json(rLoc)));
        });
        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ProductiveBees bee data provider";
    }

    private List<BeeConfig> getBeeConfigs() {
        return new ArrayList<>() {{
//            add(new BeeConfig("test").slimy().primaryColor("#000000").secondaryColor("#000000"));
//            add(new BeeConfig("sub/sub_test").slimy().primaryColor("#000000").secondaryColor("#000000"));
        }};
    }

    private Supplier<JsonElement> getBee(BeeConfig bee) {
        return () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("primaryColor", bee.primaryColor);
            jsonObject.addProperty("secondaryColor", bee.secondaryColor);
            if (bee.tertiaryColor != null) {
                jsonObject.addProperty("tertiaryColor", bee.tertiaryColor);
            }
            if (bee.particleColor != null) {
                jsonObject.addProperty("particleColor", bee.particleColor);
            }
            if (bee.particleType != null) {
                jsonObject.addProperty("particleType", bee.particleType);
            }
            if (bee.flowerType != null) {
                jsonObject.addProperty("flowerType", bee.flowerType);
            }
            if (bee.flowerTag != null) {
                jsonObject.addProperty("flowerTag", bee.flowerTag);
            }
            if (bee.flowerBlock != null) {
                jsonObject.addProperty("flowerBlock", bee.flowerBlock);
            }
            if (bee.flowerItem != null) {
                jsonObject.addProperty("flowerItem", bee.flowerItem);
            }
            if (bee.flowerFluid != null) {
                jsonObject.addProperty("flowerFluid", bee.flowerFluid);
            }
            if (bee.nestingPreference != null) {
                jsonObject.addProperty("nestingPreference", bee.nestingPreference);
            }
            if (bee.postPollination != null) {
                jsonObject.addProperty("postPollination", bee.postPollination);
            }
            if (bee.description != null) {
                jsonObject.addProperty("description", bee.description);
            }
            if (bee.beeTexture != null) {
                jsonObject.addProperty("beeTexture", bee.beeTexture);
            }
            if (bee.attackResponse != null) {
                jsonObject.addProperty("attackResponse", bee.attackResponse);
            }
            if (!bee.createComb) {
                jsonObject.addProperty("createComb", false);
            }
            if (bee.size != 1.0f) {
                jsonObject.addProperty("size", bee.size);
            }
            if (bee.speed != 1.0f) {
                jsonObject.addProperty("speed", bee.speed);
            }
            if (!bee.selfBreed) {
                jsonObject.addProperty("selfbreed", false);
            }
            if (bee.selfHeal) {
                jsonObject.addProperty("selfheal", true);
            }
            if (bee.inverseFlower) {
                jsonObject.addProperty("inverseFlower", true);
            }
            if (bee.teleporting) {
                jsonObject.addProperty("teleporting", true);
            }
            if (bee.translucent) {
                jsonObject.addProperty("translucent", true);
            }
            if (bee.useGlowLayer) {
                jsonObject.addProperty("useGlowLayer", true);
            }
            if (bee.redstoned) {
                jsonObject.addProperty("redstoned", true);
            }
            if (bee.irradiated) {
                jsonObject.addProperty("irradiated", true);
            }
            if (bee.slimy) {
                jsonObject.addProperty("slimy", true);
            }
            if (bee.fireproof) {
                jsonObject.addProperty("fireproof", true);
            }
            if (bee.draconic) {
                jsonObject.addProperty("draconic", true);
            }
            if (bee.withered) {
                jsonObject.addProperty("withered", true);
            }
            if (bee.blinding) {
                jsonObject.addProperty("blinding", true);
            }
            if (bee.stringy) {
                jsonObject.addProperty("stringy", true);
            }
            if (bee.waterproof) {
                jsonObject.addProperty("waterproof", true);
            }
            if (bee.coldResistant) {
                jsonObject.addProperty("coldResistant", true);
            }
            if (bee.munchies) {
                jsonObject.addProperty("munchies", true);
            }
            if (bee.stingless) {
                jsonObject.addProperty("stingless", true);
            }
            if (bee.renderer != null) {
                jsonObject.addProperty("renderer", bee.renderer);
            }
            if (bee.renderTransform != null) {
                jsonObject.addProperty("renderTransform", bee.renderTransform);
            }
            if (bee.breedingItem != null) {
                jsonObject.addProperty("breedingItem", bee.breedingItem);
            }
            if (bee.breedingItemCount != null) {
                jsonObject.addProperty("breedingItemCount", bee.breedingItemCount);
            }
            if (!bee.invulnerability.isEmpty()) {
                JsonArray invul = new JsonArray();
                bee.invulnerability.forEach(invul::add);
                jsonObject.add("invulnerability", invul);
            }
            if (!bee.attributes.isEmpty()) {
                JsonArray attributes = new JsonArray();
                bee.attributes.entrySet().forEach(attribute -> {
                    JsonObject o = new JsonObject();
                    o.addProperty(attribute.getKey(), attribute.getValue());
                    attributes.add(o);
                });
                jsonObject.add("attributes", attributes);
            }
            if (!bee.passiveEffects.isEmpty()) {
                JsonArray effects = new JsonArray();
                bee.passiveEffects.forEach(passiveEffect -> {
                    JsonObject o = new JsonObject();
                    o.addProperty("name", passiveEffect.name);
                    o.addProperty("duration", passiveEffect.duration);
                    effects.add(o);
                });
                jsonObject.add("passiveEffects", effects);
            }
            if (!bee.conditions.isEmpty()) {
                JsonArray conditions = new JsonArray();
                bee.conditions.forEach(condition -> {
                    conditions.add(CraftingHelper.serialize(condition));
                });
                jsonObject.add("conditions", conditions);
            }
            return jsonObject;
        };
    }

    static class BeeConfig {
        String name;
        String primaryColor = null;
        String secondaryColor = null;
        String tertiaryColor = null;
        String particleColor = null;
        String particleType = null;
        String description = null;
        String beeTexture = null;
        String attackResponse = null;
        String flowerType = null;
        String flowerTag = null;
        String flowerBlock = null;
        String flowerItem = null;
        String flowerFluid = null;
        String nestingPreference = null;
        String postPollination = null;
        float size = 1.0f;
        float pollinatedSize = 1.0f;
        float speed = 1.0f;
        double attack = 1.0f;
        boolean createComb = false;
        boolean selfBreed = false;
        boolean selfHeal = false;
        boolean inverseFlower = false;
        boolean teleporting = false;
        boolean translucent = false;
        boolean useGlowLayer = false;
        boolean redstoned = false;
        boolean irradiated = false;
        boolean slimy = false;
        boolean fireproof = false;
        boolean draconic = false;
        boolean withered = false;
        boolean blinding = false;
        boolean stringy = false;
        boolean waterproof = false;
        boolean coldResistant = false;
        boolean munchies = false;
        boolean stingless = false;
        String renderer = null;
        String renderTransform = null;
        String breedingItem = null;
        Integer breedingItemCount = null;
        List<String> invulnerability = new ArrayList<>();
        Map<String, Integer> attributes = new HashMap<>();
        List<ICondition> conditions = new ArrayList<>();
        List<PassiveEffect> passiveEffects = new ArrayList<>();

        BeeConfig(String name) {
            this.name = name;
        }

        static BeeConfig simple(String name) {
            return new BeeConfig(name);
        }

        BeeConfig primaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
            return this;
        }
        BeeConfig secondaryColor(String secondaryColor) {
            this.secondaryColor = secondaryColor;
            return this;
        }
        BeeConfig tertiaryColor(String tertiaryColor) {
            this.tertiaryColor = tertiaryColor;
            return this;
        }
        BeeConfig particleColor(String particleColor) {
            this.particleColor = particleColor;
            return this;
        }
        BeeConfig particleType(String particleType) {
            this.particleType = particleType;
            return this;
        }
        BeeConfig beeTexture(String beeTexture) {
            this.beeTexture = beeTexture;
            return this;
        }
        BeeConfig description(String description) {
            this.description = description;
            return this;
        }
        BeeConfig flowerTag(String flowerTag) {
            this.flowerTag = flowerTag;
            return this;
        }
        BeeConfig flowerBlock(String flowerBlock) {
            this.flowerBlock = flowerBlock;
            return this;
        }
        BeeConfig flowerItem(String flowerItem) {
            this.flowerItem = flowerItem;
            return this;
        }
        BeeConfig flowerFluid(String flowerFluid) {
            this.flowerFluid = flowerFluid;
            return this;
        }
        BeeConfig flowerType(String flowerType) {
            this.flowerType = flowerType;
            return this;
        }
        BeeConfig renderer(String renderer) {
            this.renderer = renderer;
            return this;
        }
        BeeConfig renderTransform(String renderTransform) {
            this.renderTransform = renderTransform;
            return this;
        }
        BeeConfig breedingItem(String breedingItem) {
            this.breedingItem = breedingItem;
            return this;
        }
        BeeConfig breedingItemCount(Integer breedingItemCount) {
            this.breedingItemCount = breedingItemCount;
            return this;
        }
        BeeConfig size(float size) {
            this.size = size;
            return this;
        }
        BeeConfig pollinatedSize(float pollinatedSize) {
            this.pollinatedSize = pollinatedSize;
            return this;
        }
        BeeConfig speed(float speed) {
            this.speed = speed;
            return this;
        }
        BeeConfig attack(double attack) {
            this.attack = attack;
            return this;
        }
        BeeConfig attackResponse(String attackResponse) {
            this.attackResponse = attackResponse;
            return this;
        }
        BeeConfig nestingPreference(String nestingPreference) {
            this.nestingPreference = nestingPreference;
            return this;
        }
        BeeConfig postPollination(String postPollination) {
            this.postPollination = postPollination;
            return this;
        }
        BeeConfig createComb() {
            this.createComb = true;
            return this;
        }
        BeeConfig selfBreed() {
            this.selfBreed = true;
            return this;
        }
        BeeConfig selfheal() {
            this.selfHeal = true;
            return this;
        }
        BeeConfig inverseFlower() {
            this.inverseFlower = true;
            return this;
        }
        BeeConfig translucent() {
            this.translucent = true;
            return this;
        }
        BeeConfig useGlowLayer() {
            this.useGlowLayer = true;
            return this;
        }
        BeeConfig teleporting() {
            this.teleporting = true;
            return this;
        }
        BeeConfig redstoned() {
            this.redstoned = true;
            return this;
        }
        BeeConfig irradiated() {
            this.irradiated = true;
            return this;
        }
        BeeConfig slimy() {
            this.slimy = true;
            return this;
        }
        BeeConfig fireproof() {
            this.fireproof = true;
            return this;
        }
        BeeConfig draconic() {
            this.draconic = true;
            return this;
        }
        BeeConfig withered() {
            this.withered = true;
            return this;
        }
        BeeConfig blinding() {
            this.blinding = true;
            return this;
        }
        BeeConfig stringy() {
            this.stringy = true;
            return this;
        }
        BeeConfig waterproof() {
            this.waterproof = true;
            return this;
        }
        BeeConfig coldResistant() {
            this.coldResistant = true;
            return this;
        }
        BeeConfig munchies() {
            this.munchies = true;
            return this;
        }
        BeeConfig stingless() {
            this.stingless = true;
            return this;
        }
    }

    record PassiveEffect(String name, Integer duration){}
}
