package cy.jdkdigital.productivebees.datagen;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.crafting.condition.FluidTagEmptyCondition;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.*;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BeeProvider implements DataProvider
{
    private final PackOutput output;
    public BeeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        PackOutput.PathProvider beePath = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "productivebees");

        List<CompletableFuture<?>> output = new ArrayList<>();

        Map<ResourceLocation, Supplier<JsonElement>> bees = Maps.newHashMap();
        // Iterate bees and create json files

        Map<ResourceLocation, CompoundTag> BEE_DATA = new HashMap<>();
        getBeeConfigs().forEach(beeConfig -> {
            var id = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, beeConfig.name);
            bees.put(id, getBee(beeConfig));
            BEE_DATA.put(id, BeeCreator.create(id, bees.get(id).get().getAsJsonObject()));
        });
        // Make data available for later providers
        BeeReloadListener.INSTANCE.setData(BEE_DATA);

        bees.forEach((rLoc, supplier) -> {
            output.add(saveStable(cachedOutput, supplier.get(), beePath.json(rLoc)));
        });
        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    static CompletableFuture<?> saveStable(CachedOutput output, JsonElement json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");
                    GsonHelper.writeValue(jsonwriter, json, null);
                }

                output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", path, ioexception);
            }

        }, Util.backgroundExecutor());
    }

    @Override
    public @NotNull String getName() {
        return "ProductiveBees bee data provider";
    }

    protected List<BeeConfig> getBeeConfigs() {
        return new ArrayList<>() {{
            add(new BeeConfig("amber").primaryColor("#fa9310").secondaryColor("#064f2c").tertiaryColor("#d4700e").particleColor("#fa9310").renderer("default_crystal").flowerTag("!productivebees:bee_encase_blacklist").flowerType("entity_types").noComb().size(0.7).postPollination("amber_encase"));
            add(new BeeConfig("coal").primaryColor("#222525").secondaryColor("#804f40").particleColor("#222525").flowerTag(Tags.Items.STORAGE_BLOCKS_COAL.location().toString()).size(0.5));
            add(new BeeConfig("draconic").primaryColor("#1c1c1c").secondaryColor("#5f2525").particleColor("#cc00fa").beeTexture("draconic").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("temper", GeneValue.TEMPER_AGGRESSIVE.getSerializedName()); }}).breedingItem("productivebees:draconic_dust").breedingItemCount(2).draconic().flowerTag("productivebees:flowers/draconic").nestingPreference("productivebees:nests/draconic_nests"));
            add(new BeeConfig("ender").primaryColor("#161616").secondaryColor("#623875").particleColor("#cc00fa").particleType("portal").size(0.8).beeTexture("ender").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_NORMAL.getSerializedName()); }}).teleporting().flowerTag("productivebees:flowers/ender").nestingPreference("productivebees:nests/end_nests"));
            add(new BeeConfig("experience").primaryColor("#00fc1a").secondaryColor("#884739").particleColor("#00fc1a").flowerTag(Tags.Items.BOOKSHELVES.location().toString()));
            add(new BeeConfig("frosty").primaryColor("#ffffff").secondaryColor("#228B22").particleColor("#cccccc").flowerTag("productivebees:flowers/frozen").waterproof().coldResistant());
            add(new BeeConfig("ghostly").primaryColor("#eeeeee").beeTexture("ghostly").translucent().noComb().attributes(new HashMap<>(){{ put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}).flowerTag("productivebees:flowers/souled").nestingPreference("productivebees:nests/soul_sand_nests"));
            add(new BeeConfig("kamikaz").primaryColor("#FFFF00").secondaryColor("#000001").particleType("pop").attributes(new HashMap<>(){{ put("productivity", GeneValue.PRODUCTIVITY_VERY_HIGH.getSerializedName()); put("endurance", GeneValue.ENDURANCE_WEAK.getSerializedName()); put("temper", GeneValue.TEMPER_HOSTILE.getSerializedName()); }}).noComb().noSelfBreed().speed(5).attack(6.0).size(0.2));
            add(new BeeConfig("magmatic").primaryColor("#d06a1b").secondaryColor("#100000").particleColor("#d06a1b").beeTexture("magmatic").renderer("translucent_with_center").flowerTag("productivebees:flowers/magmatic").nestingPreference("productivebees:nests/nether_brick_nests").attackResponse("lava").particleType("lava").fireproof().passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:fire_resistance", 400)); }}));
            add(new BeeConfig("obsidian").primaryColor("#3b2754").secondaryColor("#06030b").particleColor("#3b2754").renderer("thicc").size(0.85).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).flowerTag(Tags.Items.OBSIDIANS.location().toString()));
            add(new BeeConfig("pepto_bismol").primaryColor("#FFC0CB").secondaryColor("#FFFF00").particleColor("#FFC0CB").size(0.6).renderer("thicc").noComb().invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:regeneration", 400)); add(new PassiveEffect("minecraft:absorption", 400)); add(new PassiveEffect("minecraft:haste", 400)); add(new PassiveEffect("minecraft:strength", 400)); add(new PassiveEffect("minecraft:instant_health", 400)); add(new PassiveEffect("minecraft:saturation", 400)); add(new PassiveEffect("minecraft:luck", 400)); }}));
            add(new BeeConfig("prismarine").primaryColor("#79b7ab").secondaryColor("#315041").tertiaryColor("#5e85a4").particleColor("#267a4b").renderer("default_crystal").size(0.6).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("productivity", GeneValue.PRODUCTIVITY_MEDIUM.getSerializedName()); put("temper", GeneValue.TEMPER_HOSTILE.getSerializedName()); put("weather_tolerance", GeneValue.WEATHER_TOLERANCE_ANY.getSerializedName()); }}).waterproof().flowerTag("productivebees:flowers/prismarine"));
            add(new BeeConfig("sculk").primaryColor("#141414").secondaryColor("#141414").particleColor("#141414").beeTexture("sculk").noSelfBreed().flowerBlock("minecraft:sculk_shrieker").attributes(new HashMap<>(){{ put("temper", GeneValue.TEMPER_AGGRESSIVE.getSerializedName()); put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}).passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:darkness", 60)); }}));
            add(new BeeConfig("silky").primaryColor("#ffffff").secondaryColor("#228B22").particleColor("#cccccc").flowerBlock("minecraft:cobweb").stringy());
            add(new BeeConfig("skeletal").primaryColor("#c1c1c1").secondaryColor("#c1c1c1").beeTexture("skeletal").noSelfBreed().flowerBlock("minecraft:bone_block").attributes(new HashMap<>(){{ put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}));
            add(new BeeConfig("slimy").primaryColor("#2ff757").secondaryColor("#623875").particleColor("#2ff757").beeTexture("slimy").renderer("translucent_with_center").attributes(new HashMap<>(){{ put("temper", GeneValue.TEMPER_PASSIVE.getSerializedName()); }}).slimy().flowerTag("productivebees:flowers/swamp").nestingPreference("productivebees:nests/slimy_nests"));
            add(new BeeConfig("sponge").primaryColor("#cccc4c").secondaryColor("#9c8c3c").particleColor("#3c4c9c").size(0.7).pollinatedSize(1.1).beeTexture("sponge").noComb().waterproof().attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_NORMAL.getSerializedName()); }}).flowerBlock("minecraft:sponge"));
            add(new BeeConfig("sugarbag").primaryColor("#000000").secondaryColor("#623875").particleColor("#99bf02").beeTexture("sugarbag").flowerBlock("minecraft:sugar_cane").noComb().renderer("tiny").attributes(new HashMap<>(){{ put("temper", GeneValue.TEMPER_PASSIVE.getSerializedName()); }}).stingless().nestingPreference("productivebees:nests/sugarbag_nests"));
            add(new BeeConfig("sussy").primaryColor("#610bff").secondaryColor("#b70000").particleColor("#4200ba").size(0.3).beeTexture("sussy").flowerBlock("minecraft:air").noComb().postPollination("sus").onlySpawnegg());
            add(new BeeConfig("wanna").primaryColor("#3b2754").secondaryColor("#06030b").particleColor("#3b2754").beeTexture("wanna").renderer("thicc").noComb().attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).flowerBlock("productivebees:amber").breedingItem("minecraft:disc_fragment_5"));
            add(new BeeConfig("withered").primaryColor("#141414").secondaryColor("#141414").particleColor("#141414").beeTexture("wither").noSelfBreed().flowerTag("productivebees:flowers/wither").attributes(new HashMap<>(){{ put("temper", GeneValue.TEMPER_AGGRESSIVE.getSerializedName()); put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}).withered().passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:wither", 150)); }}));
            add(new BeeConfig("zombie").primaryColor("#796565").beeTexture("zombie").munchies().noSelfBreed().attributes(new HashMap<>(){{ put("temper", GeneValue.TEMPER_AGGRESSIVE.getSerializedName()); put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}).passiveEffects(new ArrayList<>() {{  add(new PassiveEffect("minecraft:hunger", 150)); }}).speed(0.8));
            add(new BeeConfig("breeze").primaryColor("#2b425e").secondaryColor("#686394").tertiaryColor("#bbabd1").particleColor("#f3f5ff").beeTexture("breeze").flowerItem("minecraft:heavy_core").speed(0.8));
            add(new BeeConfig("ribbeet").primaryColor("#3a5a19").secondaryColor("#73964b").particleColor("#f3f5ff").flowerTag("c:magma_cubes").beeTexture("ribbeet").flowerType("entity_types").renderer("thicc").size(0.5f).pollinatedSize(1.0f).noComb().noSelfBreed());

            add(new BeeConfig("ad_astra/calorite").primaryColor("#c44249").secondaryColor("#470d2f").particleColor("#df6d5c").flowerTag("c:storage_blocks/calorite").requireTag("c:storage_blocks/calorite"));
            add(new BeeConfig("ad_astra/cheese").primaryColor("#d99c0d").particleColor("#edc76d").beeTexture("cheese").onlySpawnegg().flowerBlock("ad_astra:cheese_block").size(0.8).requireMod("ad_astra"));
            add(new BeeConfig("ad_astra/desh").primaryColor("#e9ba5d").secondaryColor("#9e4539").particleColor("#e9ba5d").flowerTag("c:storage_blocks/desh").requireTag("c:storage_blocks/desh"));
            add(new BeeConfig("ad_astra/ostrum").primaryColor("#966062").secondaryColor("#2c1f2d").particleColor("#564151").flowerTag("c:storage_blocks/ostrum").requireTag("c:storage_blocks/ostrum"));

            add(new BeeConfig("ae2/fluix").primaryColor("#3d3270").secondaryColor("#2e0b17").tertiaryColor("#6d4fa8").particleColor("#6d4fa8").renderer("default_crystal").size(0.7).flowerBlock("ae2:fluix_block").requireMod("ae2"));
            add(new BeeConfig("ae2/silicon").primaryColor("#918d96").secondaryColor("#6b5873").size(0.7).flowerTag("productivebees:flowers/crystalline").requireTag("c:silicon"));
            add(new BeeConfig("ae2/sky_steel").primaryColor("#424546").secondaryColor("#87dfff").tertiaryColor("#3b5146").particleColor("#99ffcd").beeTexture("sky_steel").size(0.5).flowerBlock("megacells:sky_steel_block").noSelfBreed().requireMod("megacells").onlySpawnegg());
            add(new BeeConfig("ae2/spacial").primaryColor("#dfe5f6").secondaryColor("#93c7ff").tertiaryColor("#93c7ff").particleColor("#66aefc").renderer("default_crystal").size(0.7).flowerBlock("ae2:quartz_block").requireMod("ae2"));
            add(new BeeConfig("ae2/entro").primaryColor("#035256").secondaryColor("#03b99a").tertiaryColor("#65e883").particleColor("#f4ffb5").renderer("default_crystal").size(0.8).beeTexture("entro").flowerBlock("extendedae:entro_block").requireMod("extendedae").noSelfBreed().onlySpawnegg());
            add(new BeeConfig("ae2/redstone_crystal").primaryColor("#5c0404").secondaryColor("#b51d1d").tertiaryColor("#e34848").particleColor("#ffa58c").renderer("default_crystal").size(0.7).beeTexture("redstone_crystal").flowerBlock("appflux:charged_redstone_block").requireMod("appflux").noSelfBreed().onlySpawnegg());
            add(new BeeConfig("ae2/sky_bronze").primaryColor("#2e0b05").secondaryColor("#5c2513").tertiaryColor("#804a2b").particleColor("#bfb57c").renderer("default_crystal").size(0.8).beeTexture("sky_bronze").flowerBlock("megacells:sky_bronze_block").requireMod("megacells").noSelfBreed().onlySpawnegg());
            add(new BeeConfig("ae2/sky_osmium").primaryColor("#222030").secondaryColor("#353149").tertiaryColor("#635089").particleColor("#d4a3c0").size(0.6).beeTexture("sky_osmium").flowerBlock("megacells:sky_osmium_block").requireMod("megacells").noSelfBreed().onlySpawnegg());

            add(new BeeConfig("alloys/brass").primaryColor("#DAAA4C").secondaryColor("#804f40").flowerTag("c:storage_blocks/brass").requireTag("c:storage_blocks/brass"));
            add(new BeeConfig("alloys/bronze").primaryColor("#C98C52").secondaryColor("#804f40").flowerTag("c:storage_blocks/bronze").requireTag("c:storage_blocks/bronze"));
            add(new BeeConfig("alloys/constantan").primaryColor("#fc8669").secondaryColor("#884739").flowerTag("c:storage_blocks/constantan").requireTag("c:storage_blocks/constantan"));
            add(new BeeConfig("alloys/electrum").primaryColor("#D5BB4F").secondaryColor("#804f40").flowerTag("c:storage_blocks/electrum").requireTag("c:storage_blocks/electrum"));
            add(new BeeConfig("alloys/enderium").primaryColor("#58a28b").secondaryColor("#804f40").particleColor("#437f6c").flowerTag("c:storage_blocks/enderium").requireTag("c:storage_blocks/enderium"));
            add(new BeeConfig("alloys/invar").primaryColor("#ADB7B2").secondaryColor("#804f40").flowerTag("c:storage_blocks/invar").requireTag("c:storage_blocks/invar"));
            add(new BeeConfig("alloys/lumium").primaryColor("#f4ffc3").secondaryColor("#804f40").particleColor("#dde8ae").blinding().flowerTag("c:storage_blocks/lumium").requireTag("c:storage_blocks/lumium"));
            add(new BeeConfig("alloys/signalum").primaryColor("#e7917d").secondaryColor("#804f40").particleColor("#b56f60").flowerTag("c:storage_blocks/signalum").requireTag("c:storage_blocks/signalum"));
            add(new BeeConfig("alloys/steel").primaryColor("#737373").secondaryColor("#804f40").flowerTag("c:storage_blocks/steel").requireTag("c:storage_blocks/steel"));

            add(new BeeConfig("ars_nouveau/arcane").primaryColor("#c203fc").secondaryColor("#6c2482").tertiaryColor("#c203fc").particleColor("#c203fc").onlySpawnegg().renderer("default_crystal").size(0.8).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_MEDIUM.getSerializedName()); }}).flowerBlock("ars_nouveau:source_gem_block").requireMod("ars_nouveau"));

            add(new BeeConfig("aquaculture/neptunium").primaryColor("#6bf9d2").secondaryColor("#bcfcea").onlySpawnegg().beeTexture("neptunium").size(0.5).attributes(new HashMap<>(){{ put("weather_telorance", GeneValue.WEATHER_TOLERANCE_ANY.getSerializedName()); }}).flowerBlock("aquaculture:neptunium_block").requireMod("aquaculture"));

            add(new BeeConfig("astralsorcery/rock_crystal").primaryColor("#ffffff").secondaryColor("#5c5350").particleColor("#dbdbdb").onlySpawnegg().renderer("thicc").size(0.8).noComb().flowerBlock("minecraft:stone").noSelfBreed().requireMod("astralsorcery"));
            add(new BeeConfig("astralsorcery/starmetal").primaryColor("#0545b2").secondaryColor("#eceee3").particleColor("#0545b2").onlySpawnegg().flowerBlock("astralsorcery:starmetal").noSelfBreed().requireMod("astralsorcery"));

            add(new BeeConfig("atm/allthemodium").primaryColor("#f2f24f").secondaryColor("#d0581f").flowerTag("c:storage_blocks/allthemodium").breedingItem("allthemodium:vibranium_ingot").breedingItemCount(4).noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("allthemodium"));
            add(new BeeConfig("atm/gregstar").primaryColor("#282e6f").secondaryColor("#21bce6").tertiaryColor("#576cb5").particleColor("#36dfff").renderer("thicc").beeTexture("gregstar").size(1.2).onlySpawnegg().flowerBlock("allthetweaks:greg_star_block").noSelfBreed().selfHeal().fireproof().particleType("pop").invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("allthetweaks").requireMod("gtceu"));
            add(new BeeConfig("atm/patrick").primaryColor("#ffffff").secondaryColor("#ffa500").beeTexture("patrick").flowerBlock("allthetweaks:atm_star_block").renderer("thicc").size(1.25).noSelfBreed().selfHeal().fireproof().attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).requireMod("allthemodium").requireMod("allthetweaks"));
            add(new BeeConfig("atm/soul_lava").primaryColor("#323e8f").secondaryColor("#5a8eb7").particleColor("#323e8f").flowerFluid("allthemodium:soul_lava").breedingItem("allthemodium:soul_lava_bucket").noSelfBreed().particleType("lava").fireproof().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("allthemodium"));
            add(new BeeConfig("atm/starry").primaryColor("#d0581f").secondaryColor("#f2c01a").tertiaryColor("#fbfb5d").flowerBlock("allthetweaks:atm_star_block").renderer("default_shell").beeTexture("starry").size(1.5).noSelfBreed().selfHeal().fireproof().particleType("pop").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("allthemodium").requireMod("allthetweaks"));
            add(new BeeConfig("atm/unobtainium").primaryColor("#bc2feb").secondaryColor("#2e237b").flowerTag("c:storage_blocks/unobtainium").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).noSelfBreed().invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("allthemodium"));
            add(new BeeConfig("atm/vibranium").primaryColor("#73ffb9").secondaryColor("#0f5c7a").flowerTag("c:storage_blocks/vibranium").breedingItem("allthemodium:unobtainium_ingot").breedingItemCount(4).noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("allthemodium"));

            add(new BeeConfig("bloodmagic/hellfire").primaryColor("#b9f3e9").secondaryColor("#386058").tertiaryColor("#5fa295").particleColor("#e1f9f9").beeTexture("hellfire").size(0.4).flowerBlock("bloodmagic:dungeon_metal").noSelfBreed().requireMod("bloodmagic"));
            add(new BeeConfig("bloodmagic/hematophagous").primaryColor("#7a0300").secondaryColor("#0f0f66").particleColor("#7a0300").flowerTag("productivebees:animals").flowerType("entity_types").requireMod("bloodmagic"));
            add(new BeeConfig("bloodmagic/regenerative").primaryColor("#940e00").secondaryColor("#0f0f66").particleColor("#940e00").onlySpawnegg().renderer("thicc").noSelfBreed().selfHeal().noComb().attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("temper", GeneValue.TEMPER_AGGRESSIVE.getSerializedName()); put("behavior", GeneValue.BEHAVIOR_METATURNAL.getSerializedName()); }}).requireMod("bloodmagic"));

            add(new BeeConfig("botania/elementium").primaryColor("#dc5af8").secondaryColor("#804f40").onlySpawnegg().flowerTag("c:storage_blocks/elementium").noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("botania"));
            add(new BeeConfig("botania/mana").primaryColor("#316ff5").secondaryColor("#08080f").tertiaryColor("#400438").particleColor("#316ff5").onlySpawnegg().flowerBlock("botania:gaia_pylon").renderer("default_foliage").size(0.5).noComb().breedingItem("botania:blacker_lotus").requireMod("botania"));
            add(new BeeConfig("botania/manasteel").primaryColor("#4aa7ef").secondaryColor("#804f40").onlySpawnegg().flowerTag("c:storage_blocks/manasteel").noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("botania"));
            add(new BeeConfig("botania/pure").primaryColor("#fff8e3").secondaryColor("#bfb595").onlySpawnegg().renderer("default_foliage").size(0.5).noSelfBreed().noComb().requireMod("botania"));
            add(new BeeConfig("botania/terrasteel").primaryColor("#49cc1d").secondaryColor("#804f40").onlySpawnegg().flowerTag("c:storage_blocks/terrasteel").noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("botania"));

            add(new BeeConfig("botanicadds/gaiasteel").primaryColor("#862025").secondaryColor("#3e0b0c").onlySpawnegg().flowerBlock("botanicadds:gaiasteel_block").invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("botanicadds"));

            add(new BeeConfig("byg/emeraldite").primaryColor("#26ac43").secondaryColor("#804f40").tertiaryColor("#1b752f").particleColor("#26ac43").renderer("default_crystal").flowerBlock("byg:emeraldite_ore").size(0.8).requireMod("byg"));
            add(new BeeConfig("byg/pendorite").primaryColor("#482e76").secondaryColor("#30174d").particleColor("#8984d3").flowerBlock("byg:pendorite_block").size(1.2).requireMod("byg"));

            add(new BeeConfig("chemlib/actinium").primaryColor("#51cce8").particleColor("#67dbf5").beeTexture("actinium").onlySpawnegg().flowerBlock("chemlib:actinium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/americium").primaryColor("#ed220c").particleColor("#0c0ced").onlySpawnegg().beeTexture("americium").size(0.5).flowerItem("chemlib:americium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/antimony").primaryColor("#382b18").particleColor("#57452c").beeTexture("antimony").flowerItem("chemlib:antimony").requireMod("chemlib"));
            add(new BeeConfig("chemlib/argon").primaryColor("#be36d6").particleColor("#d54eed").size(0.5).beeTexture("argon").onlySpawnegg().flowerFluid("chemlib:argon_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/arsenic").primaryColor("#ccbecf").particleColor("#cdc6cf").beeTexture("arsenic").onlySpawnegg().flowerItem("chemlib:arsenic").requireMod("chemlib"));
            add(new BeeConfig("chemlib/astatine").primaryColor("#edd574").particleColor("#f2db7e").size(0.4).beeTexture("astatine").onlySpawnegg().flowerItem("chemlib:astatine").requireMod("chemlib"));
            add(new BeeConfig("chemlib/barium").primaryColor("#bfbdb2").particleColor("#c4c3be").beeTexture("barium").onlySpawnegg().flowerBlock("chemlib:barium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/berkelium").primaryColor("#c4bec4").particleColor("#d9d4d9").onlySpawnegg().beeTexture("berkelium").size(0.5).flowerItem("chemlib:berkelium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/bohrium").primaryColor("#67f0bb").particleColor("#6ff2c0").onlySpawnegg().beeTexture("bohrium").size(0.5).flowerItem("chemlib:bohrium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/boron").primaryColor("#4a4340").particleColor("#f2f24f").beeTexture("boron").flowerItem("chemlib:boron").requireMod("chemlib"));
            add(new BeeConfig("chemlib/bromine").primaryColor("#e0a394").particleColor("#edb6a8").beeTexture("bromine").onlySpawnegg().flowerFluid("chemlib:bromine_fluid").requireMod("chemlib"));
            add(new BeeConfig("chemlib/cadmium").primaryColor("#b0adac").particleColor("#ccc9c8").beeTexture("cadmium").flowerBlock("chemlib:cadmium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/calcium").primaryColor("#bdbbac").particleColor("#cfcdc5").beeTexture("calcium").onlySpawnegg().flowerBlock("chemlib:calcium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/californium").primaryColor("#dcb197").particleColor("#d7baa6").onlySpawnegg().beeTexture("californium").size(0.5).flowerItem("chemlib:californium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/cerium").primaryColor("#b5c8c0").particleColor("#c1d2c9").beeTexture("cerium").flowerBlock("chemlib:cerium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/cesium").primaryColor("#e3cb7b").particleColor("#e8d9a5").beeTexture("cesium").flowerBlock("chemlib:cesium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/chlorine").primaryColor("#cbf747").particleColor("#d0ef76").size(0.6).beeTexture("chlorine").flowerFluid("chemlib:chlorine_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/copernicium").primaryColor("#eb9c67").particleColor("#e6a780").onlySpawnegg().beeTexture("copernicium").size(0.4).flowerItem("chemlib:copernicium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/curium").primaryColor("#e8e8f1").particleColor("#e8d3d9").onlySpawnegg().beeTexture("curium").size(0.6).flowerItem("chemlib:curium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/darmstadtium").primaryColor("#4394e5").particleColor("#5ea6dd").onlySpawnegg().beeTexture("darmstadtium").size(0.4).flowerItem("chemlib:darmstadtium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/dubnium").primaryColor("#f1b89c").particleColor("#f4bda1").onlySpawnegg().beeTexture("dubnium").size(0.4).flowerItem("chemlib:dubnium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/dysprosium").primaryColor("#8eb997").particleColor("#9fcba4").beeTexture("dysprosium").flowerBlock("chemlib:dysprosium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/einsteinium").primaryColor("#35b2df").particleColor("#baebfa").onlySpawnegg().beeTexture("einsteinium").size(0.4).flowerItem("chemlib:einsteinium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/erbium").primaryColor("#ddb6c8").particleColor("#ddbec8").beeTexture("erbium").flowerBlock("chemlib:erbium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/europium").primaryColor("#6067d3").particleColor("#e2d91e").size(0.8).beeTexture("europium").flowerBlock("chemlib:europium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/fermium").primaryColor("#dece8a").particleColor("#e1d6a2").onlySpawnegg().beeTexture("fermium").size(0.4).flowerItem("chemlib:fermium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/flerovium").primaryColor("#7e6782").particleColor("#8b7791").onlySpawnegg().beeTexture("flerovium").size(0.4).flowerItem("chemlib:flerovium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/fluorine").primaryColor("#cac12c").particleColor("#f3ee80").size(0.4).beeTexture("fluorine").onlySpawnegg().flowerFluid("chemlib:fluorine_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/francium").primaryColor("#78787d").particleColor("#7f7f86").size(0.4).beeTexture("francium").onlySpawnegg().flowerBlock("chemlib:francium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/gadolinium").primaryColor("#d5bdc7").particleColor("#e1ccd3").beeTexture("gadolinium").onlySpawnegg().flowerBlock("chemlib:gadolinium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/gallium").primaryColor("#c6ccd3").particleColor("#c1cdd4").beeTexture("gallium").flowerBlock("chemlib:gallium_metal_block").size(1.0).requireMod("chemlib"));
            add(new BeeConfig("chemlib/germanium").primaryColor("#dfdfe0").particleColor("#e9e9eb").size(0.7).beeTexture("germanium").flowerItem("chemlib:germanium").requireMod("chemlib"));
            add(new BeeConfig("chemlib/hafnium").primaryColor("#444784").particleColor("#5b5e9a").size(0.7).beeTexture("hafnium").onlySpawnegg().flowerBlock("chemlib:hafnium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/hassium").primaryColor("#cfc73e").particleColor("#dfd643").onlySpawnegg().beeTexture("hassium").size(0.4).flowerItem("chemlib:hassium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/helium").primaryColor("#e5dec7").particleColor("#efe8d4").size(0.7).beeTexture("helium").onlySpawnegg().flowerFluid("chemlib:helium_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/holmium").primaryColor("#8acdcd").particleColor("#94d2ce").beeTexture("holmium").flowerBlock("chemlib:holmium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/hydrogen").primaryColor("#adafe4").particleColor("#bcbcd7").size(0.7).beeTexture("hydrogen").onlySpawnegg().flowerFluid("chemlib:hydrogen_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/indium").primaryColor("#c4cad0").particleColor("#c4d4d6").beeTexture("indium").onlySpawnegg().flowerBlock("chemlib:indium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/iodine").primaryColor("#be9dc8").particleColor("#c9aacb").beeTexture("iodine").flowerItem("chemlib:iodine").requireMod("chemlib"));
            add(new BeeConfig("chemlib/krypton").primaryColor("#bcadca").particleColor("#d0c1d0").size(0.7).beeTexture("krypton").onlySpawnegg().flowerFluid("chemlib:krypton_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/lanthanum").primaryColor("#d1bf87").particleColor("#dccb90").beeTexture("lanthanum").flowerBlock("chemlib:lanthanum_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/lawrencium").primaryColor("#c7c7c8").particleColor("#cececf").onlySpawnegg().beeTexture("lawrencium").size(0.5).flowerItem("chemlib:lawrencium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/lithium").primaryColor("#ccccda").particleColor("#ffffff").beeTexture("lithium").onlySpawnegg().flowerBlock("chemlib:lithium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/livermorium").primaryColor("#759781").particleColor("#6c978e").onlySpawnegg().beeTexture("livermorium").size(0.5).flowerItem("chemlib:livermorium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/lutetium").primaryColor("#d88087").particleColor("#d49a9e").beeTexture("lutetium").onlySpawnegg().flowerBlock("chemlib:lutetium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/magnesium").primaryColor("#aaaaad").particleColor("#ccccd1").beeTexture("magnesium").flowerBlock("chemlib:magnesium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/meitnerium").primaryColor("#9c8984").particleColor("#b4a19d").onlySpawnegg().beeTexture("meitnerium").size(0.5).flowerItem("chemlib:meitnerium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/mendelevium").primaryColor("#2b349d").particleColor("#4148a5").onlySpawnegg().beeTexture("mendelevium").size(0.5).flowerItem("chemlib:mendelevium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/mercury").primaryColor("#a5a4aa").particleColor("#bfbec5").beeTexture("mercury").flowerFluid("chemlib:mercury_fluid").requireMod("chemlib"));
            add(new BeeConfig("chemlib/molybdenum").primaryColor("#7e80a5").particleColor("#a7a8c3").beeTexture("molybdenum").flowerBlock("chemlib:molybdenum_metal_block").requireMod("chemlib").missingTag("c:raw_materials/molybdenum").missingMod("gtceu"));
            add(new BeeConfig("chemlib/moscovium").primaryColor("#e93f49").particleColor("#9f7a6b").onlySpawnegg().beeTexture("moscovium").size(0.5).flowerItem("chemlib:moscovium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/neodymium").primaryColor("#998784").particleColor("#ab9d9a").beeTexture("neodymium").onlySpawnegg().flowerBlock("chemlib:neodymium_metal_block").requireMod("chemlib").missingTag("c:raw_materials/neodymium").missingMod("gtceu"));
            add(new BeeConfig("chemlib/neon").primaryColor("#e1a4aa").particleColor("#ebafa0").size(0.7).beeTexture("neon").onlySpawnegg().flowerFluid("chemlib:neon_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/neptunium").primaryColor("#cbcbd1").particleColor("#c0d1d3").onlySpawnegg().beeTexture("neptunium").size(0.6).flowerItem("chemlib:neptunium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/nihonium").primaryColor("#f3f4f9").particleColor("#f3b9bf").onlySpawnegg().beeTexture("nihonium").size(0.5).flowerItem("chemlib:nihonium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/niobium").primaryColor("#bfb1e6").particleColor("#c3beea").beeTexture("niobium").flowerBlock("chemlib:niobium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/nitrogen").primaryColor("#f0c6cc").particleColor("#f2c9cf").size(0.7).beeTexture("nitrogen").onlySpawnegg().flowerFluid("chemlib:nitrogen_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/nobelium").primaryColor("#9c32b4").particleColor("#ae36c8").onlySpawnegg().beeTexture("nobelium").size(0.5).flowerItem("chemlib:nobelium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/oganesson").primaryColor("#7f3190").particleColor("#9636a1").onlySpawnegg().beeTexture("oganesson").size(0.2).flowerItem("chemlib:oganesson").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/oxygen").primaryColor("#edede1").particleColor("#f5f5ed").size(0.7).beeTexture("oxygen").onlySpawnegg().flowerFluid("chemlib:oxygen_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/palladium").primaryColor("#b78187").particleColor("#c3989a").beeTexture("palladium").flowerBlock("chemlib:palladium_metal_block").requireMod("chemlib").missingTag("c:raw_materials/palladium").missingMod("gtceu"));
            add(new BeeConfig("chemlib/phosphorus").primaryColor("#904456").particleColor("#c595a2").beeTexture("phosphorus").onlySpawnegg().flowerItem("chemlib:phosphorus").requireMod("chemlib"));
            add(new BeeConfig("chemlib/plutonium").primaryColor("#cdccd1").particleColor("#c9c6a8").onlySpawnegg().beeTexture("plutonium").size(0.6).flowerItem("chemlib:plutonium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/polonium").primaryColor("#b3c9c8").particleColor("#bccfcc").beeTexture("polonium").flowerBlock("chemlib:polonium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/potassium").primaryColor("#b6a577").particleColor("#eed587").beeTexture("potassium").onlySpawnegg().flowerBlock("chemlib:potassium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/praseodymium").primaryColor("#86a245").particleColor("#a2bc6e").beeTexture("praseodymium").flowerBlock("chemlib:praseodymium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/promethium").primaryColor("#afd7d4").particleColor("#cfece9").beeTexture("promethium").flowerItem("chemlib:promethium").requireMod("chemlib"));
            add(new BeeConfig("chemlib/protactinium").primaryColor("#aaa9b0").particleColor("#b2b7ae").beeTexture("protactinium").onlySpawnegg().size(0.8).flowerBlock("chemlib:protactinium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/radium").primaryColor("#cde9ee").particleColor("#e4f7fa").beeTexture("radium").onlySpawnegg().flowerBlock("chemlib:radium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/radon").primaryColor("#df494b").particleColor("#ea613d").size(0.7).beeTexture("radon").onlySpawnegg().flowerFluid("chemlib:radon_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/rhenium").primaryColor("#56565b").particleColor("#acacad").beeTexture("rhenium").onlySpawnegg().flowerBlock("chemlib:rhenium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/rhodium").primaryColor("#cccac9").particleColor("#cccac1").beeTexture("rhodium").onlySpawnegg().flowerBlock("chemlib:rhodium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/roentgenium").primaryColor("#aacb46").particleColor("#b0d248").onlySpawnegg().beeTexture("roentgenium").size(0.5).flowerItem("chemlib:roentgenium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/rubidium").primaryColor("#d89abe").particleColor("#e2afcb").beeTexture("rubidium").flowerBlock("chemlib:rubidium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/ruthenium").primaryColor("#a68994").particleColor("#ba9ca6").beeTexture("ruthenium").onlySpawnegg().flowerBlock("chemlib:ruthenium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/rutherfordium").primaryColor("#717174").particleColor("#f2f24f").onlySpawnegg().beeTexture("rutherfordium").size(0.5).flowerItem("chemlib:rutherfordium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/samarium").primaryColor("#d5db8c").particleColor("#e3f696").beeTexture("samarium").onlySpawnegg().flowerBlock("chemlib:samarium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/scandium").primaryColor("#d5d3ca").particleColor("#dedbd0").beeTexture("scandium").flowerBlock("chemlib:scandium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/seaborgium").primaryColor("#4d54bb").particleColor("#7d659e").onlySpawnegg().beeTexture("seaborgium").size(0.5).flowerItem("chemlib:seaborgium").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/selenium").primaryColor("#99bab8").particleColor("#b6e3de").beeTexture("selenium").onlySpawnegg().flowerItem("chemlib:selenium").requireMod("chemlib"));
            add(new BeeConfig("chemlib/silicium").primaryColor("#9a9aa2").particleColor("#d6d5dc").beeTexture("silicium").flowerItem("chemlib:silicon").requireMod("chemlib"));
            add(new BeeConfig("chemlib/sodium").primaryColor("#e8d7a6").particleColor("#f6e5b2").beeTexture("sodium").flowerBlock("chemlib:sodium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/strontium").primaryColor("#c6bdcf").particleColor("#d4c5d2").beeTexture("strontium").onlySpawnegg().flowerBlock("chemlib:strontium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/tantalum").primaryColor("#b9bbde").particleColor("#bfc2e7").beeTexture("tantalum").flowerBlock("chemlib:tantalum_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/technetium").primaryColor("#848486").particleColor("#969699").size(0.7).beeTexture("technetium").onlySpawnegg().flowerItem("chemlib:technetium").requireMod("chemlib"));
            add(new BeeConfig("chemlib/tellurium").primaryColor("#957c74").particleColor("#a29085").size(0.7).beeTexture("tellurium").onlySpawnegg().flowerItem("chemlib:tellurium").requireMod("chemlib"));
            add(new BeeConfig("chemlib/tennessine").primaryColor("#dd995e").particleColor("#eeb149").onlySpawnegg().beeTexture("tennessine").size(0.5).flowerItem("chemlib:tennessine").noSelfBreed().requireMod("chemlib"));
            add(new BeeConfig("chemlib/terbium").primaryColor("#c4c3c2").particleColor("#d1cfca").beeTexture("terbium").onlySpawnegg().flowerBlock("chemlib:terbium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/thallium").primaryColor("#9292a0").particleColor("#9e9ea5").beeTexture("thallium").onlySpawnegg().flowerBlock("chemlib:thallium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/thorium").primaryColor("#e6e6ea").particleColor("#ffffff").beeTexture("thorium").flowerBlock("chemlib:thorium_metal_block").breedingItem("chemlib:thorium_ingot").requireMod("chemlib"));
            add(new BeeConfig("chemlib/thulium").primaryColor("#a8d5bb").particleColor("#bbd5c9").beeTexture("thulium").onlySpawnegg().flowerBlock("chemlib:thulium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/vanadium").primaryColor("#9da1d6").particleColor("#adafd5").beeTexture("vanadium").flowerBlock("chemlib:vanadium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/xenon").primaryColor("#a0ade2").particleColor("#accded").size(0.7).beeTexture("xenon").onlySpawnegg().flowerFluid("chemlib:xenon_fluid").translucent().requireMod("chemlib"));
            add(new BeeConfig("chemlib/ytterbium").primaryColor("#d6d3c9").particleColor("#dfd9c5").beeTexture("ytterbium").flowerBlock("chemlib:ytterbium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/yttrium").primaryColor("#dbe2b1").particleColor("#d0e6b6").beeTexture("yttrium").flowerBlock("chemlib:yttrium_metal_block").requireMod("chemlib"));
            add(new BeeConfig("chemlib/zirconium").primaryColor("#e1e15f").particleColor("#e6e56d").beeTexture("zirconium").onlySpawnegg().flowerBlock("chemlib:zirconium_metal_block").requireMod("chemlib"));

            add(new BeeConfig("create_enchantment_industry/hyper_experience").primaryColor("#4550e7").secondaryColor("#5698db").tertiaryColor("#b0cdeb").particleColor("#a1d0ff").beeTexture("hyper_experience").size(0.6).flowerItem("create_enchantment_industry:hyper_experience_bottle").noSelfBreed().requireMod("create_enchantment_industry"));

            add(new BeeConfig("draconicevolution/awakened").primaryColor("#bc3500").secondaryColor("#5f2525").particleColor("#faa420").tertiaryColor("#bc3500").renderer("default_crystal").onlySpawnegg().flowerTag("c:storage_blocks/draconium_awakened").fireproof().draconic().noSelfBreed().nestingPreference("productivebees:nests/draconic_nests").invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("draconicevolution"));
            add(new BeeConfig("draconicevolution/chaos").primaryColor("#090909").secondaryColor("#090909").particleColor("#303030").tertiaryColor("#303030").renderer("default_crystal").onlySpawnegg().flowerItem("draconicevolution:chaos_shard").fireproof().draconic().noSelfBreed().nestingPreference("productivebees:nests/draconic_nests").invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("draconicevolution"));
            add(new BeeConfig("draconicevolution/draconium").primaryColor("#1c1c1c").secondaryColor("#5f2525").particleColor("#cc00fa").tertiaryColor("#cc00fa").renderer("default_crystal").onlySpawnegg().flowerTag("c:storage_blocks/draconium").fireproof().draconic().noSelfBreed().nestingPreference("productivebees:nests/draconic_nests").invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("draconicevolution"));

            add(new BeeConfig("dusts/blazing").primaryColor("#fcd979").particleColor("#fcd979").beeTexture("blazing").flowerTag("productivebees:flowers/fiery").attackResponse("fire").particleType("lava").fireproof());
            add(new BeeConfig("dusts/glowing").primaryColor("#fad87d").secondaryColor("#5f2525").particleColor("#fad87d").size(0.9).beeTexture("glowing").blinding().flowerTag("productivebees:flowers/glowing").nestingPreference("productivebees:nests/glowstone_nests").passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:glowing", 400)); }}));
            add(new BeeConfig("dusts/niter").primaryColor("#e9edf3").secondaryColor("#836e73").particleColor("#e9edf3").flowerTag("c:storage_blocks/niter").renderer("default_crystal").requireTag("c:storage_blocks/niter"));
            add(new BeeConfig("dusts/redstone").primaryColor("#d03621").secondaryColor("#804f40").tertiaryColor("#730c00").particleColor("#ff0000").particleType("lava").renderer("default_crystal").noGlow().redstoned().flowerTag("productivebees:flowers/redstone"));
            add(new BeeConfig("dusts/salty").primaryColor("#fa9f98").secondaryColor("#8a8a8a").particleColor("#fa7d73").flowerFluid("minecraft:water").waterproof().requireTag("c:dusts/salt"));
            add(new BeeConfig("dusts/sulfur").primaryColor("#e4ff95").secondaryColor("#c9ab4b").particleColor("#f1f372").flowerTag("productivebees:flowers/sulfur").renderer("default_crystal").requireTag("c:dusts/sulfur"));

            add(new BeeConfig("eidolon/arcane_gold").primaryColor("#f2da7d").secondaryColor("#a14f38").tertiaryColor("#7a3030").particleColor("#f9e597").beeTexture("arcane_gold").size(0.8).flowerBlock("eidolon:arcane_gold_block").requireMod("eidolon"));
            add(new BeeConfig("eidolon/pewter").primaryColor("#63635a").secondaryColor("#a1a097").tertiaryColor("#b8b8b2").particleColor("#f0f0f0").size(0.9).flowerBlock("eidolon:pewter_block").requireMod("eidolon"));
            add(new BeeConfig("eidolon/soul_shard").primaryColor("#6650b5").secondaryColor("#e388dd").tertiaryColor("#57368f").particleColor("#ffbaba").onlySpawnegg().beeTexture("soul_shard").size(0.5).flowerBlock("eidolon:shadow_gem_block").noSelfBreed().requireMod("eidolon"));

            add(new BeeConfig("elementalcraft/air_crystal").primaryColor("#a4a634").secondaryColor("#484849").tertiaryColor("#cccccc").particleColor("#a4a634").renderer("default_crystal").flowerBlock("elementalcraft:aircrystal_block").size(0.6).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/drenched_iron").primaryColor("#cbddee").secondaryColor("#5590ce").particleColor("#f8f8fa").onlySpawnegg().flowerTag("c:storage_blocks/drenched_iron").size(0.6).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/earth_crystal").primaryColor("#299652").secondaryColor("#484849").tertiaryColor("#cccccc").particleColor("#299652").renderer("default_crystal").flowerBlock("elementalcraft:earthcrystal_block").size(0.6).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/fire_crystal").primaryColor("#aa4242").secondaryColor("#484849").tertiaryColor("#cccccc").particleColor("#aa4242").renderer("default_crystal").flowerBlock("elementalcraft:firecrystal_block").size(0.6).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/fireite").primaryColor("#db750e").secondaryColor("#2c0d0d").particleColor("#db750e").onlySpawnegg().flowerTag("c:storage_blocks/fireite").size(0.6).fireproof().noSelfBreed().selfHeal().attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/inert_crystal").primaryColor("#cccccc").secondaryColor("#484849").tertiaryColor("#cccccc").particleColor("#cccccc").renderer("default_crystal").flowerBlock("elementalcraft:inertcrystal_block").size(0.6).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/pure_crystal").primaryColor("#fffffe").secondaryColor("#beb0dd").tertiaryColor("#beb0dd").particleColor("#fffffe").renderer("default_crystal").onlySpawnegg().flowerBlock("elementalcraft:purerock").size(0.6).noSelfBreed().selfHeal().attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); }}).invulnerability(new ArrayList<>() {{ add("mekanism.radiation"); }}).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/springaline").primaryColor("#b4ddfc").secondaryColor("#517391").tertiaryColor("#fcfdfd").particleColor("#fcfdfd").renderer("default_crystal").onlySpawnegg().flowerBlock("elementalcraft:springaline_block").size(0.6).noSelfBreed().requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/swift_alloy").primaryColor("#ebb760").secondaryColor("#c58114").particleColor("#ebb760").onlySpawnegg().flowerTag("c:storage_blocks/swift_alloy").noSelfBreed().size(0.6).requireMod("elementalcraft"));
            add(new BeeConfig("elementalcraft/water_crystal").primaryColor("#293c76").secondaryColor("#484849").tertiaryColor("#cccccc").particleColor("#293c76").renderer("default_crystal").flowerBlock("elementalcraft:watercrystal_block").size(0.6).requireMod("elementalcraft"));

            add(new BeeConfig("enderio/conductive_alloy").primaryColor("#ecccc8").secondaryColor("#987e77").particleColor("#d3aa9e").flowerBlock("enderio:conductive_alloy_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/copper_alloy").primaryColor("#b67c07").secondaryColor("#7a5305").particleColor("#b67c07").flowerBlock("enderio:copper_alloy_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/dark_steel").primaryColor("#3b3b3b").secondaryColor("#171717").particleColor("#7a7a7a").size(0.7).flowerBlock("enderio:dark_steel_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/end_steel").primaryColor("#fcf29b").secondaryColor("#b0a654").particleColor("#fcf29b").size(0.7).flowerBlock("enderio:end_steel_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/energetic_alloy").primaryColor("#fcebc5").secondaryColor("#e6a100").particleColor("#fcd989").flowerBlock("enderio:energetic_alloy_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/infinity").primaryColor("#191919").secondaryColor("#717171").particleColor("#717171").particleType("pop").size(0.5).flowerBlock("enderio:reinforced_obsidian_block").breedingItem("minecraft:flint_and_steel").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/pulsating_alloy").primaryColor("#6fd184").secondaryColor("#3f7d4d").particleColor("#b2ebbf").size(0.8).flowerBlock("enderio:pulsating_alloy_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/redstone_alloy").primaryColor("#f65b5b").secondaryColor("#621919").particleColor("#f65b5b").flowerBlock("enderio:redstone_alloy_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/soularium").primaryColor("#5b4223").secondaryColor("#2a1d0a").particleColor("#5b4223").flowerBlock("enderio:soularium_block").onlySpawnegg().requireMod("enderio"));
            add(new BeeConfig("enderio/vibrant_alloy").primaryColor("#e8f178").secondaryColor("#d0da4b").particleColor("#f0fcb2").size(0.9).flowerBlock("enderio:vibrant_alloy_block").onlySpawnegg().requireMod("enderio"));

            add(new BeeConfig("enigmaticlegacy/astral").primaryColor("#4d88ed").secondaryColor("#d85cd8").tertiaryColor("#dc502d").particleColor("#fee645").beeTexture("astral").size(0.6).onlySpawnegg().flowerBlock("enigmaticlegacy:astral_block").noSelfBreed().requireMod("enigmaticlegacy"));
            add(new BeeConfig("enigmaticlegacy/etherium_ore").primaryColor("#9b9252").secondaryColor("#dae1a1").tertiaryColor("#25bfab").particleColor("#cfffff").beeTexture("etherium_ore").size(0.7).flowerBlock("enigmaticlegacy:etherium_block").noSelfBreed().requireMod("enigmaticlegacy"));

            add(new BeeConfig("evilcraft/bloody").primaryColor("#ba3d34").secondaryColor("#8a0303").particleColor("#ba3d34").onlySpawnegg().renderer("thicc").size(0.9).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("productivity", GeneValue.PRODUCTIVITY_MEDIUM.getSerializedName()); }}).flowerBlock("evilcraft:hardened_blood").requireMod("evilcraft"));
            add(new BeeConfig("evilcraft/dark_gem").primaryColor("#636363").secondaryColor("#2e0b17").tertiaryColor("#141414").particleColor("#636363").renderer("default_crystal").size(0.7).attributes(new HashMap<>(){{ put("temper", GeneValue.TEMPER_AGGRESSIVE.getSerializedName()); }}).flowerBlock("evilcraft:dark_block").requireMod("evilcraft"));

            add(new BeeConfig("feywild/fey").primaryColor("#66ccff").secondaryColor("#666699").particleColor("#18abf5").onlySpawnegg().beeTexture("fey").renderer("default_crystal").particleType("pop").size(0.6).flowerTag("c:ores/fey_gem").noSelfBreed().noComb().requireMod("feywild"));

            add(new BeeConfig("fluids/chocolate").primaryColor("#914139").secondaryColor("#804f40").particleColor("#914139").flowerBlock("minecraft:cocoa").requireFluidTag("c", "chocolate"));
            add(new BeeConfig("fluids/oily").primaryColor("#010000").secondaryColor("#804f40").particleColor("#3b2754").flowerFluid("#c:crude_oil").waterproof().renderer("thicc").requireFluidTag("c", "crude_oil"));
            add(new BeeConfig("fluids/tea").primaryColor("#ca7157").secondaryColor("#804f40").particleColor("#ca7157").flowerTag("minecraft:leaves").requireMod("create"));
            add(new BeeConfig("fluids/water").primaryColor("#4977f5").secondaryColor("#232b3d").tertiaryColor("#5e94e0").particleColor("#0b1e42").size(0.8).attributes(new HashMap<>(){{put("weather_tolerance", GeneValue.WEATHER_TOLERANCE_ANY.getSerializedName()); }}).waterproof().noComb().flowerFluid("minecraft:water"));

            add(new BeeConfig("fluxnetworks/flux").primaryColor("#141715").particleColor("#222924").beeTexture("flux").onlySpawnegg().flowerBlock("fluxnetworks:flux_block").size(0.5).requireMod("fluxnetworks"));

            add(new BeeConfig("forbidden_arcanus/arcane_crystal").primaryColor("#4550e7").secondaryColor("#a7bbfa").tertiaryColor("#c1ecfd").particleColor("#c4dee8").beeTexture("arcane_crystal").renderer("default_crystal").size(0.7).flowerBlock("forbidden_arcanus:arcane_crystal_block").requireMod("forbidden_arcanus"));
            add(new BeeConfig("forbidden_arcanus/deorum").primaryColor("#ca831a").secondaryColor("#ebb02f").particleColor("#f4d167").onlySpawnegg().size(0.8).flowerBlock("forbidden_arcanus:deorum_block").requireMod("forbidden_arcanus"));
            add(new BeeConfig("forbidden_arcanus/rune").primaryColor("#620d5f").secondaryColor("#8d2171").tertiaryColor("#c12383").particleColor("#eebcbe").beeTexture("rune").renderer("default_crystal").size(0.7).flowerBlock("forbidden_arcanus:rune_block").requireMod("forbidden_arcanus"));
            add(new BeeConfig("forbidden_arcanus/stellarite").primaryColor("#58594d").secondaryColor("#7a7e6d").particleColor("#909885").onlySpawnegg().size(0.8).noSelfBreed().flowerBlock("forbidden_arcanus:stellarite_block").requireMod("forbidden_arcanus"));

            add(new BeeConfig("gems/agate").primaryColor("#9f01b8").secondaryColor("#064f2c").tertiaryColor("#c0138a").particleColor("#9f01b8").renderer("default_crystal").flowerTag("productivebees:flowers/agate").size(0.6).requireTag("c:gems/agate").requireTag("productivebees:flowers/agate"));
            add(new BeeConfig("gems/alexandrite").primaryColor("#8d009a").secondaryColor("#2e0b17").tertiaryColor("#250028").particleColor("#8d009a").renderer("default_crystal").flowerTag("productivebees:flowers/alexandrite").size(0.6).requireTag("c:gems/alexandrite").requireTag("productivebees:flowers/alexandrite"));
            add(new BeeConfig("gems/amber_gem").primaryColor("#fa9310").secondaryColor("#064f2c").tertiaryColor("#d4700e").particleColor("#fa9310").renderer("default_crystal").flowerTag("productivebees:flowers/amber").size(0.7).requireTag("c:gems/amber").requireTag("productivebees:flowers/amber"));
            add(new BeeConfig("gems/amethyst").primaryColor("#7217c4").secondaryColor("#064f2c").tertiaryColor("#32005c").particleColor("#7217c4").renderer("default_crystal").flowerTag("productivebees:flowers/amethyst").size(0.6));
            add(new BeeConfig("gems/ametrine").primaryColor("#98004c").secondaryColor("#2e0b17").tertiaryColor("#3d001f").particleColor("#98004c").renderer("default_crystal").flowerTag("productivebees:flowers/ametrine").size(0.6).requireTag("c:gems/ametrine").requireTag("productivebees:flowers/ametrine"));
            add(new BeeConfig("gems/ammolite").primaryColor("#e4481f").secondaryColor("#3e5b68").tertiaryColor("#0900c9").particleColor("#e4481f").renderer("default_crystal").flowerTag("productivebees:flowers/ammolite").size(0.6).requireTag("c:gems/ammolite").requireTag("productivebees:flowers/ammolite"));
            add(new BeeConfig("gems/apatite").primaryColor("#69ffff").secondaryColor("#3e5b68").tertiaryColor("#20afce").particleColor("#69ffff").renderer("default_crystal").flowerTag("productivebees:flowers/apatite").size(0.6).requireTag("c:gems/apatite").requireTag("productivebees:flowers/apatite"));
            add(new BeeConfig("gems/aquamarine").primaryColor("#17cadd").secondaryColor("#064f2c").tertiaryColor("#007b70").particleColor("#17cadd").renderer("default_crystal").flowerTag("productivebees:flowers/aquamarine").size(0.6).requireTag("c:gems/aquamarine").requireTag("productivebees:flowers/aquamarine"));
            add(new BeeConfig("gems/benitoite").primaryColor("#001bb6").secondaryColor("#2e0b17").tertiaryColor("#000c46").particleColor("#001bb6").renderer("default_crystal").flowerTag("productivebees:flowers/benitoite").size(0.6).requireTag("c:gems/benitoite").requireTag("productivebees:flowers/benitoite"));
            add(new BeeConfig("gems/black_diamond").primaryColor("#636363").secondaryColor("#2e0b17").tertiaryColor("#141414").particleColor("#636363").renderer("default_crystal").flowerTag("productivebees:flowers/black_diamond").size(0.6).requireTag("c:gems/black_diamond").requireTag("productivebees:flowers/black_diamond"));
            add(new BeeConfig("gems/black_opal").primaryColor("#636363").secondaryColor("#064f2c").tertiaryColor("#141414").particleColor("#636363").renderer("default_crystal").flowerTag("productivebees:flowers/black_opal").size(0.6).requireTag("c:gems/black_opal").requireTag("productivebees:flowers/black_opal"));
            add(new BeeConfig("gems/carnelian").primaryColor("#d84e02").secondaryColor("#2e0b17").tertiaryColor("#340400").particleColor("#d84e02").renderer("default_crystal").flowerTag("productivebees:flowers/carnelian").size(0.6).requireTag("c:gems/carnelian").requireTag("productivebees:flowers/carnelian"));
            add(new BeeConfig("gems/cats_eye").primaryColor("#ffe59e").secondaryColor("#3e5b68").tertiaryColor("#d59042").particleColor("#ffe59e").renderer("default_crystal").flowerTag("productivebees:flowers/cats_eye").size(0.6).requireTag("c:gems/cats_eye").requireTag("productivebees:flowers/cats_eye"));
            add(new BeeConfig("gems/chrysoprase").primaryColor("#6cf631").secondaryColor("#3e5b68").tertiaryColor("#54943a").particleColor("#6cf631").renderer("default_crystal").flowerTag("productivebees:flowers/chrysoprase").size(0.6).requireTag("c:gems/chrysoprase").requireTag("productivebees:flowers/chrysoprase"));add(new BeeConfig("gems/cinnabar").primaryColor("#d73e4a").secondaryColor("#710626").particleColor("#ff7883").flowerTag("c:storage_blocks/cinnabar").size(0.6).requireTag("c:gems/cinnabar").requireTag("c:storage_blocks/cinnabar"));
            add(new BeeConfig("gems/citrine").primaryColor("#995500").secondaryColor("#2e0b17").tertiaryColor("#838f00").particleColor("#995500").renderer("default_crystal").flowerTag("productivebees:flowers/citrine").size(0.6).requireTag("c:gems/citrine").requireTag("productivebees:flowers/citrine"));
            add(new BeeConfig("gems/coral").primaryColor("#ff732f").secondaryColor("#3e5b68").tertiaryColor("#dc1e4b").particleColor("#ff732f").renderer("default_crystal").flowerTag("productivebees:flowers/coral").size(0.6).requireTag("c:gems/coral").requireTag("productivebees:flowers/coral"));add(new BeeConfig("gems/crystalline").primaryColor("#ede5dd").secondaryColor("#804f40").particleColor("#ede5dd").size(0.9).beeTexture("quartz").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_MEDIUM.getSerializedName()); }}).flowerTag("productivebees:flowers/crystalline").nestingPreference("productivebees:nests/nether_quartz_nests").passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:resistance", 600)); }}));
            add(new BeeConfig("gems/crystalline").primaryColor("#ede5dd").secondaryColor("#804f40").particleColor("#ede5dd").size(0.9).beeTexture("quartz").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_MEDIUM.getSerializedName()); }}).flowerTag("productivebees:flowers/crystalline").nestingPreference("productivebees:nests/nether_quartz_nests").passiveEffects(new ArrayList<>() {{ add(new PassiveEffect("minecraft:resistance", 600)); }}));
            add(new BeeConfig("gems/diamond").primaryColor("#3ddfe1").secondaryColor("#804f40").tertiaryColor("#0ebabd").particleColor("#3ddfe1").renderer("default_crystal").size(0.6).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_MEDIUM.getSerializedName()); }}).flowerTag("productivebees:flowers/diamond"));
            add(new BeeConfig("gems/emerald").primaryColor("#26ac43").secondaryColor("#804f40").tertiaryColor("#1b752f").particleColor("#26ac43").flowerTag("productivebees:flowers/emerald").renderer("default_crystal").size(0.6));
            add(new BeeConfig("gems/euclase").primaryColor("#005782").secondaryColor("#2e0b17").tertiaryColor("#001b28").particleColor("#005782").renderer("default_crystal").flowerTag("productivebees:flowers/euclase").size(0.6).requireTag("c:gems/euclase").requireTag("productivebees:flowers/euclase"));
            add(new BeeConfig("gems/fluorite").primaryColor("#b9f3fa").secondaryColor("#1b2c33").tertiaryColor("#28a0b1").particleColor("#32e1f6").renderer("default_crystal").flowerTag("productivebees:flowers/fluorite").size(0.6).requireTag("c:gems/fluorite").requireTag("productivebees:flowers/fluorite"));
            add(new BeeConfig("gems/garnet").primaryColor("#cc3516").secondaryColor("#064f2c").tertiaryColor("#dd3b17").particleColor("#cc3516").renderer("default_crystal").flowerTag("productivebees:flowers/garnet").size(0.6).requireTag("c:gems/garnet").requireTag("productivebees:flowers/garnet"));
            add(new BeeConfig("gems/green_sapphire").primaryColor("#8adb18").secondaryColor("#064f2c").tertiaryColor("#019500").particleColor("#8adb18").renderer("default_crystal").flowerTag("productivebees:flowers/green_sapphire").size(0.6).requireTag("c:gems/green_sapphire").requireTag("productivebees:flowers/green_sapphire"));
            add(new BeeConfig("gems/heliodor").primaryColor("#ddcf17").secondaryColor("#064f2c").tertiaryColor("#957b00").particleColor("#ddcf17").renderer("default_crystal").flowerTag("productivebees:flowers/heliodor").size(0.6).requireTag("c:gems/heliodor").requireTag("productivebees:flowers/heliodor"));
            add(new BeeConfig("gems/iolite").primaryColor("#270070").secondaryColor("#2e0b17").tertiaryColor("#7000bd").particleColor("#270070").renderer("default_crystal").flowerTag("productivebees:flowers/iolite").size(0.6).requireTag("c:gems/iolite").requireTag("productivebees:flowers/iolite"));
            add(new BeeConfig("gems/jade").primaryColor("#d6ff75").secondaryColor("#3e5b68").tertiaryColor("#b2e25d").particleColor("#d6ff75").renderer("default_crystal").flowerTag("productivebees:flowers/jade").size(0.6).requireTag("c:gems/jade").requireTag("productivebees:flowers/jade"));
            add(new BeeConfig("gems/jasper").primaryColor("#342b00").secondaryColor("#2e0b17").tertiaryColor("#85794c").particleColor("#342b00").renderer("default_crystal").flowerTag("productivebees:flowers/jasper").size(0.6).requireTag("c:gems/jasper").requireTag("productivebees:flowers/jasper"));
            add(new BeeConfig("gems/kunzite").primaryColor("#fe48ef").secondaryColor("#3e5b68").tertiaryColor("#93239e").particleColor("#fe48ef").renderer("default_crystal").flowerTag("productivebees:flowers/kunzite").size(0.6).requireTag("c:gems/kunzite").requireTag("productivebees:flowers/kunzite"));
            add(new BeeConfig("gems/kyanite").primaryColor("#3b67ec").secondaryColor("#3e5b68").tertiaryColor("#0f4362").particleColor("#3b67ec").renderer("default_crystal").flowerTag("productivebees:flowers/kyanite").size(0.6).requireTag("c:gems/kyanite").requireTag("productivebees:flowers/kyanite"));
            add(new BeeConfig("gems/lapis").primaryColor("#2659ab").secondaryColor("#804f40").tertiaryColor("#1b3588").particleColor("#3537bc").particleType("pop").renderer("default_crystal").noGlow().flowerTag("c:storage_blocks/lapis").size(0.8));
            add(new BeeConfig("gems/lepidolite").primaryColor("#510046").secondaryColor("#2e0b17").tertiaryColor("#c812aa").particleColor("#510046").renderer("default_crystal").flowerTag("productivebees:flowers/lepidolite").size(0.6).requireTag("c:gems/lepidolite").requireTag("productivebees:flowers/lepidolite"));
            add(new BeeConfig("gems/malachite").primaryColor("#22d946").secondaryColor("#2e0b17").tertiaryColor("#127625").particleColor("#22d946").renderer("default_crystal").flowerTag("productivebees:flowers/malachite").size(0.6).requireTag("c:gems/malachite").requireTag("productivebees:flowers/malachite"));
            add(new BeeConfig("gems/moldavite").primaryColor("#b5bb05").secondaryColor("#2e0b17").tertiaryColor("#636e00").particleColor("#b5bb05").renderer("default_crystal").flowerTag("productivebees:flowers/moldavite").size(0.6).requireTag("c:gems/moldavite").requireTag("productivebees:flowers/moldavite"));
            add(new BeeConfig("gems/moonstone").primaryColor("#e4e4e4").secondaryColor("#2e0b17").tertiaryColor("#b6b6b6").particleColor("#e4e4e4").renderer("default_crystal").flowerTag("productivebees:flowers/moonstone").size(0.6).requireTag("c:gems/moonstone").requireTag("productivebees:flowers/moonstone"));
            add(new BeeConfig("gems/morganite").primaryColor("#ffbfc4").secondaryColor("#064f2c").tertiaryColor("#d67a91").particleColor("#ffbfc4").renderer("default_crystal").flowerTag("productivebees:flowers/morganite").size(0.6).requireTag("c:gems/morganite").requireTag("productivebees:flowers/morganite"));
            add(new BeeConfig("gems/onyx").primaryColor("#383838").secondaryColor("#064f2c").tertiaryColor("#1f1f1f").particleColor("#383838").renderer("default_crystal").flowerTag("productivebees:flowers/onyx").size(0.6).requireTag("c:gems/onyx").requireTag("productivebees:flowers/onyx"));
            add(new BeeConfig("gems/opal").primaryColor("#dbdbdb").secondaryColor("#064f2c").tertiaryColor("#8c8c8c").particleColor("#dbdbdb").renderer("default_crystal").flowerTag("productivebees:flowers/opal").size(0.6).requireTag("c:gems/opal").requireTag("productivebees:flowers/opal"));
            add(new BeeConfig("gems/pearl").primaryColor("#a4b6d2").secondaryColor("#3e5b68").tertiaryColor("#7c95be").particleColor("#a4b6d2").renderer("default_crystal").flowerTag("productivebees:flowers/pearl").size(0.6).requireTag("c:gems/pearl").requireTag("productivebees:flowers/pearl"));
            add(new BeeConfig("gems/peridot").primaryColor("#8adb18").secondaryColor("#064f2c").tertiaryColor("#5d7b00").particleColor("#8adb18").renderer("default_crystal").flowerTag("productivebees:flowers/peridot").size(0.6).requireTag("c:gems/peridot").requireTag("productivebees:flowers/peridot"));
            add(new BeeConfig("gems/phosphophyllite").primaryColor("#18db8a").secondaryColor("#064f2c").tertiaryColor("#007b33").particleColor("#18db8a").renderer("default_crystal").flowerTag("productivebees:flowers/phosphophyllite").size(0.6).requireTag("c:gems/phosphophyllite").requireTag("productivebees:flowers/phosphophyllite"));
            add(new BeeConfig("gems/pyrope").primaryColor("#bc1100").secondaryColor("#3e5b68").tertiaryColor("#620000").particleColor("#bc1100").renderer("default_crystal").flowerTag("productivebees:flowers/pyrope").size(0.6).requireTag("c:gems/pyrope").requireTag("productivebees:flowers/pyrope"));
            add(new BeeConfig("gems/rose_quartz").primaryColor("#ffbffb").secondaryColor("#3e5b68").tertiaryColor("#a63367").particleColor("#ffbffb").renderer("default_crystal").flowerTag("productivebees:flowers/rose_quartz").size(0.6).requireTag("productivebees:flowers/rose_quartz"));
            add(new BeeConfig("gems/ruby").primaryColor("#c62415").secondaryColor("#064f2c").tertiaryColor("#7b000b").particleColor("#c62415").renderer("default_crystal").flowerTag("productivebees:flowers/ruby").size(0.6).requireTag("c:gems/ruby").requireTag("productivebees:flowers/ruby"));
            add(new BeeConfig("gems/sapphire").primaryColor("#5241f3").secondaryColor("#064f2c").tertiaryColor("#000b7b").particleColor("#5241f3").renderer("default_crystal").flowerTag("productivebees:flowers/sapphire").size(0.6).requireTag("c:gems/sapphire").requireTag("productivebees:flowers/sapphire"));
            add(new BeeConfig("gems/sodalite").primaryColor("#4c4bff").secondaryColor("#3e5b68").tertiaryColor("#23389e").particleColor("#4c4bff").renderer("default_crystal").flowerTag("productivebees:flowers/sodalite").size(0.6).requireTag("c:gems/sodalite"));
            add(new BeeConfig("gems/spinel").primaryColor("#741200").secondaryColor("#2e0b17").tertiaryColor("#c25500").particleColor("#741200").renderer("default_crystal").flowerTag("productivebees:flowers/spinel").size(0.6).requireTag("c:gems/spinel").requireTag("productivebees:flowers/spinel"));
            add(new BeeConfig("gems/sunstone").primaryColor("#ffcab0").secondaryColor("#3e5b68").tertiaryColor("#d45241").particleColor("#ffcab0").renderer("default_crystal").flowerTag("productivebees:flowers/sunstone").size(0.6).requireTag("c:gems/sunstone").requireTag("productivebees:flowers/sunstone"));
            add(new BeeConfig("gems/tanzanite").primaryColor("#a66ef4").secondaryColor("#064f2c").tertiaryColor("#3500aa").particleColor("#a66ef4").renderer("default_crystal").flowerTag("productivebees:flowers/tanzanite").size(0.6).requireTag("c:gems/tanzanite").requireTag("productivebees:flowers/tanzanite"));
            add(new BeeConfig("gems/tektite").primaryColor("#978574").secondaryColor("#3e5b68").tertiaryColor("#725e4c").particleColor("#978574").renderer("default_crystal").flowerTag("productivebees:flowers/tektite").size(0.6).requireTag("c:gems/tektite").requireTag("productivebees:flowers/tektite"));
            add(new BeeConfig("gems/topaz").primaryColor("#dd7d17").secondaryColor("#064f2c").tertiaryColor("#dd7d17").particleColor("#dd7d17").renderer("default_crystal").flowerTag("productivebees:flowers/topaz").size(0.6).requireTag("c:gems/topaz").requireTag("productivebees:flowers/topaz"));
            add(new BeeConfig("gems/turquoise").primaryColor("#24daac").secondaryColor("#2e0b17").tertiaryColor("#19ac80").particleColor("#24daac").renderer("default_crystal").flowerTag("productivebees:flowers/turquoise").size(0.6).requireTag("c:gems/turquoise").requireTag("productivebees:flowers/turquoise"));
            add(new BeeConfig("gems/tourmaline").primaryColor("#ee2386").secondaryColor("#3e5b68").tertiaryColor("#8a2457").particleColor("#ee2386").renderer("default_crystal").flowerTag("productivebees:flowers/tourmaline").size(0.6).requireTag("c:gems/tourmaline").requireTag("productivebees:flowers/tourmaline"));add(new BeeConfig("gems/turquoise").primaryColor("#24daac").secondaryColor("#2e0b17").tertiaryColor("#19ac80").particleColor("#24daac").renderer("default_crystal").flowerTag("productivebees:flowers/turquoise").size(0.6).requireTag("c:gems/turquoise").requireTag("productivebees:flowers/turquoise"));
            add(new BeeConfig("gems/white_diamond").primaryColor("#fff9e4").secondaryColor("#f7ed91").tertiaryColor("#c5b520").particleColor("#fff9e4").renderer("default_crystal").flowerTag("productivebees:flowers/white_diamond").size(0.6).requireTag("c:gems/white_diamond").requireTag("productivebees:flowers/white_diamond"));
            add(new BeeConfig("gems/zircon").primaryColor("#787800").secondaryColor("#2e0b17").tertiaryColor("#b3b600").particleColor("#787800").renderer("default_crystal").flowerTag("productivebees:flowers/zircon").size(0.6).requireTag("c:gems/zircon").requireTag("productivebees:flowers/zircon"));

            add(new BeeConfig("gobber/end_gobber").primaryColor("#30cc9a").secondaryColor("#a1f6b9").flowerBlock("gobber2:gobber2_block_end").breedingItem("gobber2:gobber2_glob_end").noSelfBreed().size(0.5).requireMod("gobber2"));
            add(new BeeConfig("gobber/gobber").primaryColor("#528dc6").secondaryColor("#66b1e4").flowerBlock("gobber2:gobber2_block").breedingItem("gobber2:gobber2_glob").noSelfBreed().size(0.5).requireMod("gobber2"));
            add(new BeeConfig("gobber/nether_gobber").primaryColor("#8f2c55").secondaryColor("#fda155").flowerBlock("gobber2:gobber2_block_nether").breedingItem("gobber2:gobber2_glob_nether").noSelfBreed().size(0.5).requireMod("gobber2"));

            add(new BeeConfig("gtceu/barite").primaryColor("#836c43").secondaryColor("#d8d2c2").tertiaryColor("#57411a").particleColor("#e5dfcf").beeTexture("barite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_barite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/bastnasite").primaryColor("#743b21").secondaryColor("#ab904e").tertiaryColor("#a77742").particleColor("#c7a85e").beeTexture("bastnasite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_bastnasite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/bauxite").primaryColor("#965425").secondaryColor("#cdaa52").tertiaryColor("#450a04").particleColor("#cdaa52").beeTexture("bauxite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_bauxite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/chromite").primaryColor("#554256").secondaryColor("#b4b09a").tertiaryColor("#8d7c85").particleColor("#c3bfa6").beeTexture("chromite").size(0.8).flowerTag("c:storage_blocks/raw_chromite").onlySpawnegg().noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/cobaltite").primaryColor("#184ddf").secondaryColor("#3d8bff").tertiaryColor("#262556").particleColor("#3d8bff").beeTexture("cobaltite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_cobaltite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/electrotine").primaryColor("#1a4568").secondaryColor("#7dc1ea").tertiaryColor("#001c36").particleColor("#e3f0f8").beeTexture("electrotine").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_electrotine").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/galena").primaryColor("#2c2034").secondaryColor("#ded4e5").tertiaryColor("#5a4f61").particleColor("#f0e5f7").beeTexture("galena").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_galena").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/graphite").primaryColor("#1b2012").secondaryColor("#77796d").tertiaryColor("#4a4e40").particleColor("#a6a69c").beeTexture("graphite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_graphite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/ilmenite").primaryColor("#171006").secondaryColor("#2a2924").tertiaryColor("#251a0d").particleColor("#28251e").beeTexture("ilmenite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_ilmenite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/lepidolite").primaryColor("#573750").secondaryColor("#e1c6d2").tertiaryColor("#774d67").particleColor("#fbd7e0").beeTexture("lepidolite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_lepidolite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/molybdenum").primaryColor("#7e80a5").particleColor("#a7a8c3").beeTexture("molybdenum").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/molybdenum").noSelfBreed().requireMod("gtceu").requireTag("c:raw_materials/molybdenum"));
            add(new BeeConfig("gtceu/naquadah").primaryColor("#522629").secondaryColor("#211722").tertiaryColor("#cc9c59").particleColor("#fcefbd").beeTexture("naquadah").size(0.6).onlySpawnegg().flowerBlock("gtceu:naquadah_block").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/neodymium").primaryColor("#998784").particleColor("#ab9d9a").beeTexture("neodymium").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/neodymium").noSelfBreed().requireMod("gtceu").requireTag("c:raw_materials/neodymium"));
            add(new BeeConfig("gtceu/neutronium").primaryColor("#272727").secondaryColor("#383838").tertiaryColor("#b3b3b3").particleColor("#dcdcdc").beeTexture("neutronium").size(1).flowerTag("c:storage_blocks/neutronium").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/oilsands").primaryColor("#292822").secondaryColor("#a18f66").tertiaryColor("#665d45").particleColor("#e0c588").beeTexture("oilsands").size(0.8).onlySpawnegg().flowerBlock("gtceu:raw_oilsands_block").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/palladium").primaryColor("#b78187").particleColor("#c3989a").beeTexture("palladium").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/palladium").noSelfBreed().requireMod("gtceu").requireTag("c:raw_materials/palladium"));
            add(new BeeConfig("gtceu/pyrochlore").primaryColor("#2b1a0d").secondaryColor("#4a3929").tertiaryColor("#2c1c10").particleColor("#5a4737").beeTexture("pyrochlore").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_pyrochlore").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/pyrolusite").primaryColor("#5b5551").secondaryColor("#b9a89f").tertiaryColor("#353231").particleColor("#c5b3a9").beeTexture("pyrolusite").size(0.8).flowerTag("c:storage_blocks/raw_pyrolusite").onlySpawnegg().noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/realgar").primaryColor("#35080b").secondaryColor("#d5312b").tertiaryColor("#470d0f").particleColor("#fc3c32").beeTexture("realgar").size(0.8).flowerTag("c:storage_blocks/realgar").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/scheelite").primaryColor("#24365d").secondaryColor("#c8d8a6").tertiaryColor("#93a9ad").particleColor("#d4e5b1").beeTexture("scheelite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_scheelite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/sheldonite").primaryColor("#4d4f2e").secondaryColor("#d5e999").tertiaryColor("#8f9961").particleColor("#e6fca5").beeTexture("sheldonite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_cooperite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/sphalerite").primaryColor("#4a4326").secondaryColor("#edcd7e").tertiaryColor("#8d7c4a").particleColor("#fcd986").beeTexture("sphalerite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_sphalerite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/stibnite").primaryColor("#141824").secondaryColor("#646464").tertiaryColor("#363840").particleColor("#7d7c7c").beeTexture("stibnite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_stibnite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/tantalite").primaryColor("#291206").secondaryColor("#476287").tertiaryColor("#514444").particleColor("#4d6a92").beeTexture("tantalite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_tantalite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/tetrahedrite").primaryColor("#273125").secondaryColor("#7d7f77").tertiaryColor("#474f44").particleColor("#a19e99").beeTexture("tetrahedrite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_tetrahedrite").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/tricalcium_phosphate").primaryColor("#94941e").secondaryColor("#dfde97").tertiaryColor("#bebd49").particleColor("#fbf9d8").beeTexture("tricalcium_phosphate").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_tricalcium_phosphate").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/tungstate").primaryColor("#6e4824").secondaryColor("#bfc893").tertiaryColor("#725531").particleColor("#ddfcc2").beeTexture("tungstate").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_tungstate").noSelfBreed().requireMod("gtceu"));
            add(new BeeConfig("gtceu/vanadium_magnetite").primaryColor("#201a27").secondaryColor("#7e8794").tertiaryColor("#5c5d6d").particleColor("#8892a0").beeTexture("vanadium_magnetite").size(0.8).onlySpawnegg().flowerTag("c:storage_blocks/raw_vanadium_magnetite").noSelfBreed().requireMod("gtceu"));

            add(new BeeConfig("iceandfire/fire_dragonsteel").primaryColor("#c08787").secondaryColor("#3d1316").particleColor("#524853").onlySpawnegg().renderer("thicc").size(1.2).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("productivity", GeneValue.PRODUCTIVITY_MEDIUM.getSerializedName()); }}).flowerBlock("iceandfire:dragonsteel_fire_block").requireMod("iceandfire"));
            add(new BeeConfig("iceandfire/ice_dragonsteel").primaryColor("#c0e3f6").secondaryColor("#323234").particleColor("#fcfcfc").onlySpawnegg().renderer("thicc").size(1.2).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("productivity", GeneValue.PRODUCTIVITY_MEDIUM.getSerializedName()); }}).flowerBlock("iceandfire:dragonsteel_ice_block").requireMod("iceandfire"));
            add(new BeeConfig("iceandfire/lightning_dragonsteel").primaryColor("#4d3e7c").secondaryColor("#9177ae").particleColor("#8471a0").onlySpawnegg().renderer("thicc").size(1.2).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_STRONG.getSerializedName()); put("productivity", GeneValue.PRODUCTIVITY_MEDIUM.getSerializedName()); }}).flowerBlock("iceandfire:dragonsteel_lightning_block").requireMod("iceandfire"));

            add(new BeeConfig("immersiveengineering/hop_graphite").primaryColor("#242424").secondaryColor("#202020").tertiaryColor("#0a0a0a").particleColor("#191919").size(0.9).flowerItem("immersiveengineering:graphite_electrode").noSelfBreed().requireMod("immersiveengineering"));

            add(new BeeConfig("industrialforegoing/ether_gas").primaryColor("#9ad7e3").secondaryColor("#70b4c2").particleColor("#b5f3ff").beeTexture("ether_gas").renderer("translucent_with_center").size(0.5).flowerTag("c:withers").flowerType("entity_types").noSelfBreed().requireMod("industrialforegoing"));
            add(new BeeConfig("industrialforegoing/pink_slimy").primaryColor("#b969ba").secondaryColor("#623875").particleColor("#c98bca").beeTexture("pink_slimy").renderer("translucent_with_center").attributes(new HashMap<>(){{ put("productivity", GeneValue.PRODUCTIVITY_NORMAL.getSerializedName()); }}).slimy().flowerTag("productivebees:flowers/swamp").nestingPreference("productivebees:nests/slimy_nests").requireFluidTag("c", "pink_slime"));

            add(new BeeConfig("integrateddynamics/menril").primaryColor("#5a7088").secondaryColor("#804f40").particleColor("#5a7088").flowerBlock("integrateddynamics:crystalized_menril_block").requireMod("integrateddynamics"));

            add(new BeeConfig("irons_spellbooks/arcane_essence").primaryColor("#7cd7ea").secondaryColor("#9938c3").tertiaryColor("#227c8f").particleColor("#7cd7ea").beeTexture("arcane_essence").size(0.8).flowerItem("irons_spellbooks:ruined_book").requireMod("irons_spellbooks"));

            add(new BeeConfig("justdirethings/blazegold").primaryColor("#3e2133").secondaryColor("#ae5653").tertiaryColor("#e99053").particleColor("#f6b06d").beeTexture("blazegold").size(0.8).flowerBlock("justdirethings:blazegold_block").requireMod("justdirethings"));
            add(new BeeConfig("justdirethings/celestigem").primaryColor("#15595f").secondaryColor("#37c5bb").tertiaryColor("#90f8e3").particleColor("#d8fbec").beeTexture("celestigem").size(0.7).renderer("default_crystal").flowerBlock("justdirethings:celestigem_block").noSelfBreed().requireMod("justdirethings"));
            add(new BeeConfig("justdirethings/eclipsealloy").primaryColor("#2c3141").secondaryColor("#475e61").tertiaryColor("#65888c").particleColor("#6d9195").beeTexture("eclipsealloy").size(0.9).renderer("thicc").flowerBlock("justdirethings:eclipsealloy_block").noSelfBreed().requireMod("justdirethings"));
            add(new BeeConfig("justdirethings/ferricore").primaryColor("#32474e").secondaryColor("#53777e").tertiaryColor("#bdf4ea").particleColor("#e4fff9").beeTexture("ferricore").flowerBlock("justdirethings:ferricore_block").requireMod("justdirethings"));
            add(new BeeConfig("justdirethings/time_crystal").primaryColor("#00dd10").secondaryColor("#61f825").particleColor("#94FF95").size(0.3).noSelfBreed().renderer("default_crystal").beeTexture("time_crystal").onlySpawnegg().flowerBlock("justdirethings:time_crystal_block").requireMod("justdirethings"));

            add(new BeeConfig("l2hostility/chaotic").primaryColor("#6400ab").secondaryColor("#f627f3").particleColor("#ffa268").beeTexture("chaotic").size(0.8).noSelfBreed().flowerBlock("l2hostility:chaos_block").requireMod("l2hostility"));
            add(new BeeConfig("l2hostility/miracle").primaryColor("#412fbf").secondaryColor("#56c693").particleColor("#ffffe0").beeTexture("miracle").size(0.6).noSelfBreed().flowerBlock("l2hostility:miracle_block").requireMod("l2hostility"));

            add(new BeeConfig("materials/plastic").primaryColor("#d3d3d3").secondaryColor("#535353").size(0.7).flowerTag("productivebees:flowers/plastic").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_WEAK.getSerializedName()); }}).requireTag("productivebees:flowers/plastic"));
            add(new BeeConfig("materials/sticky_resin").primaryColor("#000001").secondaryColor("#d98b24").size(0.7).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_WEAK.getSerializedName()); }}).requireEitherMod("gtceu", "ic2"));

            add(new BeeConfig("mekanism/refined_glowstone").primaryColor("#feee7c").secondaryColor("#bb8d23").flowerTag("c:storage_blocks/refined_glowstone").requireMod("mekanism"));
            add(new BeeConfig("mekanism/refined_obsidian").primaryColor("#5e5077").secondaryColor("#372856").flowerTag("c:storage_blocks/refined_obsidian").requireMod("mekanism"));
            add(new BeeConfig("mekanism/wasted_radioactive").primaryColor("#80B425").secondaryColor("#bb8d23").particleColor("#80B425").flowerItem("mekanism:pellet_antimatter").irradiated().particleType("pop").noSelfBreed().requireMod("mekanism"));
            add(new BeeConfig("mekanism/lithium").primaryColor("#694d0c").secondaryColor("#b6830c").tertiaryColor("#dd9f11").particleColor("#e2af3a").beeTexture("lithium").flowerFluid("mekanism:lithium").noSelfBreed().requireMod("mekanism"));

            add(new BeeConfig("modern_industrialization/antimony").primaryColor("#83838f").secondaryColor("#9696a3").tertiaryColor("#b7b7cb").particleColor("#c5c5d7").beeTexture("antimony").flowerTag("c:storage_blocks/antimony").requireTag("c:storage_blocks/antimony"));
            add(new BeeConfig("modern_industrialization/beryllium").primaryColor("#a7dbb4").particleColor("#b1e6be").beeTexture("beryllium").flowerTag("c:storage_blocks/beryllium").requireTag("c:storage_blocks/beryllium").onlySpawnegg());
            add(new BeeConfig("modern_industrialization/chromium").primaryColor("#d1d1d3").particleColor("#e9e9e9").beeTexture("chromium").flowerTag("c:storage_blocks/chromium").requireTag("c:storage_blocks/chromium").onlySpawnegg());
            add(new BeeConfig("modern_industrialization/manganese").primaryColor("#bccddb").particleColor("#c6dadf").beeTexture("manganese").flowerItem("modern_industrialization:manganese_dust").requireMod("modern_industrialization").onlySpawnegg());
            add(new BeeConfig("modern_industrialization/monazite").primaryColor("#471143").secondaryColor("#9f3197").tertiaryColor("#d66dce").particleColor("#f17ce7").beeTexture("monazite").flowerTag("c:storage_blocks/monazite").requireTag("c:storage_blocks/monazite"));

            add(new BeeConfig("mysticalagriculture/awakened_supremium").primaryColor("#d08412").secondaryColor("#a60b0a").particleColor("#c86911").size(0.8).onlySpawnegg().flowerBlock("mysticalagriculture:awakened_supremium_block").noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/imperium").primaryColor("#007FDB").secondaryColor("#804f40").particleColor("#007FDB").size(0.8).onlySpawnegg().flowerBlock("mysticalagriculture:imperium_block").noSelfBreed().requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/inferium").primaryColor("#748E00").secondaryColor("#804f40").particleColor("#748E00").size(0.8).onlySpawnegg().flowerBlock("mysticalagriculture:inferium_block").requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/insanium").primaryColor("#4d086d").secondaryColor("#804f40").particleColor("#410062").size(0.8).onlySpawnegg().flowerBlock("mysticalagradditions:insanium_block").noSelfBreed().invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("mysticalagradditions"));
            add(new BeeConfig("mysticalagriculture/prosperity").primaryColor("#ddfbfb").secondaryColor("#587676").particleColor("#aecccc").size(0.6).flowerBlock("mysticalagriculture:prosperity_block").noSelfBreed().requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/prudentium").primaryColor("#008C23").secondaryColor("#804f40").particleColor("#008C23").size(0.8).onlySpawnegg().flowerBlock("mysticalagriculture:prudentium_block").noSelfBreed().requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/soulium").primaryColor("#301b10").secondaryColor("#804f40").particleColor("#301b10").size(0.8).flowerBlock("mysticalagriculture:soulium_block").requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/supremium").primaryColor("#C40000").secondaryColor("#804f40").particleColor("#C40000").size(0.8).onlySpawnegg().flowerBlock("mysticalagriculture:supremium_block").noSelfBreed().requireMod("mysticalagriculture"));
            add(new BeeConfig("mysticalagriculture/tertium").primaryColor("#B74900").secondaryColor("#804f40").particleColor("#B74900").size(0.8).onlySpawnegg().flowerBlock("mysticalagriculture:tertium_block").noSelfBreed().requireMod("mysticalagriculture"));

            add(new BeeConfig("mythicbotany/alfsteel").primaryColor("#ffd238").secondaryColor("#b77d04").onlySpawnegg().flowerBlock("mythicbotany:alfsteel_block").invulnerability(new ArrayList<>() {{  add("mekanism.radiation"); }}).requireMod("mythicbotany"));

            add(new BeeConfig("naturesaura/infused_iron").primaryColor("#51d75a").secondaryColor("#2ea736").tertiaryColor("#29732e").particleColor("#2fc63a").beeTexture("infused_iron").size(0.9).flowerBlock("naturesaura:infused_iron_block").noSelfBreed().requireMod("naturesaura"));
            add(new BeeConfig("naturesaura/sky_ingot").primaryColor("#b7e8ff").secondaryColor("#6ec7f1").tertiaryColor("#4ea3cb").particleColor("#9ce0ff").beeTexture("sky_ingot").size(0.5).flowerBlock("naturesaura:sky_ingot_block").noSelfBreed().requireMod("naturesaura"));
            add(new BeeConfig("naturesaura/tainted_gold").primaryColor("#ca7328").secondaryColor("#a05a1e").tertiaryColor("#734721").particleColor("#cb7225").beeTexture("tainted_gold").size(0.8).flowerBlock("naturesaura:tainted_gold_block").noSelfBreed().requireMod("naturesaura"));

            add(new BeeConfig("occultism/iesnium").primaryColor("#52a0ae").secondaryColor("#8ecbce").particleColor("#3c8794").flowerTag("c:storage_blocks/iesnium").onlySpawnegg().requireTag("c:storage_blocks/iesnium"));

            add(new BeeConfig("pneumaticcraft/compressed_iron").primaryColor("#737373").secondaryColor("#804f40").particleColor("#b6b6b6").size(0.5).onlySpawnegg().flowerBlock("pneumaticcraft:compressed_iron_block").requireMod("pneumaticcraft"));

            add(new BeeConfig("pokecube/cosmic_dust").primaryColor("#2394cc").secondaryColor("#0f0f66").particleColor("#2394cc").flowerTag("c:ores/cosmic").requireTag("c:gems/cosmicdust").requireTag("c:ores/cosmic"));
            add(new BeeConfig("pokecube/spectrum").primaryColor("#ffc9a7").secondaryColor("#ff762c").particleColor("#ffc9a7").flowerTag("c:storage_blocks/spectrum").requireTag("c:gems/spectrum").requireTag("c:storage_blocks/spectrum"));

            add(new BeeConfig("powah/blazing_crystal").primaryColor("#f2c735").secondaryColor("#c9a324").flowerBlock("powah:blazing_crystal_block").onlySpawnegg().requireMod("powah"));
            add(new BeeConfig("powah/energized_steel").primaryColor("#bfb49d").secondaryColor("#7d7565").flowerBlock("powah:energized_steel_block").requireMod("powah").onlySpawnegg());
            add(new BeeConfig("powah/niotic_crystal").primaryColor("#1dc1f2").secondaryColor("#1b86a6").flowerBlock("powah:niotic_crystal_block").requireMod("powah").onlySpawnegg());
            add(new BeeConfig("powah/nitro_crystal").primaryColor("#e33917").secondaryColor("#b3280c").flowerBlock("powah:nitro_crystal_block").noSelfBreed().requireMod("powah").onlySpawnegg());
            add(new BeeConfig("powah/spirited_crystal").primaryColor("#7cff1f").secondaryColor("#61bf1f").flowerBlock("powah:spirited_crystal_block").noSelfBreed().requireMod("powah").onlySpawnegg());
            add(new BeeConfig("powah/uraninite").primaryColor("#00FF00").secondaryColor("#008000").particleColor("#7CFC00").size(0.8).flowerTag("c:storage_blocks/uraninite").requireMod("powah").requireTag("c:storage_blocks/uraninite"));

            add(new BeeConfig("raw_materials/aluminum").primaryColor("#A4A6B1").secondaryColor("#804f40").flowerTag("c:storage_blocks/aluminum").requireTag("c:storage_blocks/aluminum"));
            add(new BeeConfig("raw_materials/bismuth").primaryColor("#ece386").secondaryColor("#586bb7").particleColor("#b598db").flowerTag("c:storage_blocks/bismuth").requireTag("c:storage_blocks/bismuth"));
            add(new BeeConfig("raw_materials/copper").primaryColor("#F48702").secondaryColor("#804f40").flowerTag("productivebees:flowers/cupric"));
            add(new BeeConfig("raw_materials/gold").primaryColor("#FCD979").secondaryColor("#804f40").particleColor("#fffd6e").flowerTag("productivebees:flowers/gilded").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_WEAK.getSerializedName()); }}));
            add(new BeeConfig("raw_materials/iridium").primaryColor("#ffccff").secondaryColor("#d4e4fc").particleColor("#8c9fac").flowerTag("c:storage_blocks/iridium").size(0.8).requireTag("c:storage_blocks/iridium"));
            add(new BeeConfig("raw_materials/iron").primaryColor("#cdcdcd").secondaryColor("#804f40").particleColor("#b6b6b6").flowerTag("productivebees:flowers/ferric"));
            add(new BeeConfig("raw_materials/lead").primaryColor("#677193").secondaryColor("#804f40").flowerTag("c:storage_blocks/lead").requireTag("c:storage_blocks/lead"));
            add(new BeeConfig("raw_materials/netherite").primaryColor("#4d494d").secondaryColor("#804f40").particleColor("#4d494d").flowerTag("c:storage_blocks/netherite").size(0.7).attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_MEDIUM.getSerializedName()); }}).attackResponse("fire").fireproof().withered());
            add(new BeeConfig("raw_materials/nickel").primaryColor("#D8CC93").secondaryColor("#804f40").flowerTag("c:storage_blocks/nickel").requireTag("c:storage_blocks/nickel"));
            add(new BeeConfig("raw_materials/osmium").primaryColor("#4c9db6").secondaryColor("#804f40").flowerTag("c:storage_blocks/osmium").requireTag("c:storage_blocks/osmium"));
            add(new BeeConfig("raw_materials/platinum").primaryColor("#6FEAEF").secondaryColor("#804f40").attributes(new HashMap<>(){{ put("endurance", GeneValue.ENDURANCE_MEDIUM.getSerializedName()); }}).flowerTag("c:storage_blocks/platinum").requireTag("c:storage_blocks/platinum"));
            add(new BeeConfig("raw_materials/radioactive").primaryColor("#60AE11").secondaryColor("#804f40").flowerTag("productivebees:flowers/radioactive").passiveEffects(new ArrayList<>() {{  add(new PassiveEffect("minecraft:nausea", 150));  add(new PassiveEffect("minecraft:weakness", 150)); }}).requireTag("productivebees:flowers/radioactive"));
            add(new BeeConfig("raw_materials/silver").primaryColor("#A9DBE5").secondaryColor("#804f40").flowerTag("c:storage_blocks/silver").requireTag("c:storage_blocks/silver"));
            add(new BeeConfig("raw_materials/tin").primaryColor("#9ABDD6").secondaryColor("#804f40").flowerTag("c:storage_blocks/tin").requireTag("c:storage_blocks/tin"));
            add(new BeeConfig("raw_materials/titanium").primaryColor("#D0D1DA").secondaryColor("#804f40").flowerTag("c:storage_blocks/titanium").requireTag("c:storage_blocks/titanium"));
            add(new BeeConfig("raw_materials/tungsten").primaryColor("#616669").secondaryColor("#804f40").flowerTag("c:storage_blocks/tungsten").requireTag("c:storage_blocks/tungsten"));
            add(new BeeConfig("raw_materials/zinc").primaryColor("#E9EBE7").secondaryColor("#804f40").flowerTag("c:storage_blocks/zinc").requireTag("c:storage_blocks/zinc"));
            add(new BeeConfig("raw_materials/mithril").primaryColor("#0b2638").secondaryColor("#0e6a61").particleColor("#92e7ae").flowerTag("productivebees:flowers/mithril").beeTexture("mithril").requireTag("productivebees:flowers/mithril"));

            add(new BeeConfig("reactors/blutonium").primaryColor("#1929d4").secondaryColor("#0c1899").particleColor("#2b38bd").size(0.8).flowerTag("c:storage_blocks/blutonium").onlySpawnegg().requireTag("c:storage_blocks/blutonium"));
            add(new BeeConfig("reactors/cyanite").primaryColor("#72c2d4").secondaryColor("#60b2c4").particleColor("#92c6d1").size(0.8).flowerTag("c:storage_blocks/cyanite").requireTag("c:storage_blocks/cyanite"));
            add(new BeeConfig("reactors/graphite").primaryColor("#1b2012").secondaryColor("#77796d").tertiaryColor("#4a4e40").particleColor("#a6a69c").beeTexture("graphite").size(0.8).flowerTag("c:storage_blocks/graphite").noSelfBreed().missingMod("gtceu").requireTag("c:storage_blocks/graphite"));
            add(new BeeConfig("reactors/inanite").primaryColor("#bd0d62").secondaryColor("#db046c").particleColor("#ed2b89").size(0.8).flowerTag("c:storage_blocks/inanite").noSelfBreed().onlySpawnegg().requireTag("c:storage_blocks/inanite"));
            add(new BeeConfig("reactors/insanite").primaryColor("#1eeb96").secondaryColor("#16c97f").particleColor("#37de99").size(0.8).flowerTag("c:storage_blocks/insanite").noSelfBreed().onlySpawnegg().requireTag("c:storage_blocks/insanite"));
            add(new BeeConfig("reactors/ludicrite").primaryColor("#7d10b0").secondaryColor("#8f21c2").particleColor("#ad5bd4").size(0.8).flowerTag("c:storage_blocks/ludicrite").noSelfBreed().onlySpawnegg().requireTag("c:storage_blocks/ludicrite"));
            add(new BeeConfig("reactors/magentite").primaryColor("#c418c9").secondaryColor("#970c9c").particleColor("#da6fde").size(0.8).flowerTag("c:storage_blocks/magentite").requireTag("c:storage_blocks/magentite"));
            add(new BeeConfig("reactors/ridiculite").primaryColor("#e2ace8").secondaryColor("#ba8abf").particleColor("#dcb4e0").size(0.8).flowerTag("c:storage_blocks/ridiculite").noSelfBreed().onlySpawnegg().requireTag("c:storage_blocks/ridiculite"));

            add(new BeeConfig("refinedstorage/quartz_enriched_iron").primaryColor("#c9c7c2").secondaryColor("#4f4e4b").particleColor("#c9c7c2").size(1.0).flowerBlock("refinedstorage:quartz_enriched_iron_block").requireMod("refinedstorage"));

            add(new BeeConfig("shroom/brown_shroom").primaryColor("#724a2f").secondaryColor("#3c2617").particleColor("#724a2f").flowerBlock("minecraft:brown_mushroom").renderer("default_foliage").size(0.5).breedingItem("minecraft:brown_mushroom"));
            add(new BeeConfig("shroom/crimson").primaryColor("#6e1917").secondaryColor("#2f0d06").tertiaryColor("#913000").particleColor("#913000").flowerBlock("minecraft:crimson_fungus").renderer("default_foliage").size(0.5).breedingItem("minecraft:crimson_fungus").attributes(new HashMap<>(){{ put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}).nestingPreference("productivebees:nests/nether_nests"));
            add(new BeeConfig("shroom/red_shroom").primaryColor("#8e1410").secondaryColor("#566551").particleColor("#8e1410").flowerBlock("minecraft:red_mushroom").renderer("default_foliage").size(0.5).breedingItem("minecraft:red_mushroom"));
            add(new BeeConfig("shroom/warped").primaryColor("#0c6138").secondaryColor("#0c3326").tertiaryColor("#9a562c").particleColor("#9a3600").flowerBlock("minecraft:warped_fungus").renderer("default_foliage").size(0.5).breedingItem("minecraft:warped_fungus").attributes(new HashMap<>(){{ put("behavior", GeneValue.BEHAVIOR_NOCTURNAL.getSerializedName()); }}).nestingPreference("productivebees:nests/nether_nests"));

            add(new BeeConfig("spirit/spirit").primaryColor("#839cb8").secondaryColor("#0d1119").particleColor("#5c687d").flowerBlock("spirit:soul_steel_block").requireMod("spirit"));

            add(new BeeConfig("tconstruct/amethyst_bronze").primaryColor("#C687BD").secondaryColor("#7a5800").particleColor("#C687BD").onlySpawnegg().flowerTag("c:storage_blocks/amethyst_bronze").requireTag("c:storage_blocks/amethyst_bronze"));
            add(new BeeConfig("tconstruct/cobalt").primaryColor("#1d77eb").secondaryColor("#0c5abe").particleColor("#1d77eb").onlySpawnegg().flowerTag("c:storage_blocks/cobalt").requireTag("c:storage_blocks/cobalt"));
            add(new BeeConfig("tconstruct/ender_slimy").primaryColor("#d17bfc").secondaryColor("#6200ae").particleColor("#d17bfc").beeTexture("ender_slimy").onlySpawnegg().renderer("translucent_with_center").requireTag("c:slimeball/ender"));
            add(new BeeConfig("tconstruct/hepatizon").primaryColor("#675072").secondaryColor("#1b0426").particleColor("#675072").onlySpawnegg().flowerTag("c:storage_blocks/hepatizon").requireTag("c:storage_blocks/hepatizon"));
            add(new BeeConfig("tconstruct/ichor_slimy").primaryColor("#fcb77b").secondaryColor("#ae3f00").particleColor("#fcb77b").beeTexture("ichor_slimy").onlySpawnegg().renderer("translucent_with_center").requireTag("c:slimeball/ichor"));
            add(new BeeConfig("tconstruct/knightslime").primaryColor("#c882f5").secondaryColor("#804f40").particleColor("#c882f5").onlySpawnegg().flowerTag("c:storage_blocks/knightslime").requireTag("c:storage_blocks/knightslime"));
            add(new BeeConfig("tconstruct/manyullyn").primaryColor("#ab6cd7").secondaryColor("#652e87").particleColor("#ab6cd7").onlySpawnegg().flowerTag("c:storage_blocks/manyullyn").requireTag("c:storage_blocks/manyullyn"));
            add(new BeeConfig("tconstruct/pig_iron").primaryColor("#dbaaa9").secondaryColor("#804f40").particleColor("#dbaaa9").onlySpawnegg().flowerTag("c:storage_blocks/pig_iron").requireTag("c:storage_blocks/pig_iron"));
            add(new BeeConfig("tconstruct/queens_slime").primaryColor("#267049").secondaryColor("#204c49").particleColor("#267049").onlySpawnegg().flowerTag("c:storage_blocks/queens_slime").requireTag("c:storage_blocks/queens_slime"));
            add(new BeeConfig("tconstruct/rose_gold").primaryColor("#eeb9a0").secondaryColor("#804f40").particleColor("#eeb9a0").onlySpawnegg().flowerTag("c:storage_blocks/rose_gold").requireTag("c:storage_blocks/rose_gold"));
            add(new BeeConfig("tconstruct/sky_slime").primaryColor("#80d4d2").secondaryColor("#2e5250").particleColor("#80d4d2").beeTexture("sky_slimy").onlySpawnegg().renderer("translucent_with_center").requireTag("c:slimeball/sky"));
            add(new BeeConfig("tconstruct/slimesteel").primaryColor("#7ae7e0").secondaryColor("#73d2dc").particleColor("#7ae7e0").onlySpawnegg().flowerTag("c:storage_blocks/slimesteel").requireTag("c:storage_blocks/slimesteel"));
            add(new BeeConfig("tconstruct/soulsteel").primaryColor("#5c4436").secondaryColor("#1f0700").particleColor("#5c4436").onlySpawnegg().flowerTag("c:storage_blocks/soulsteel").requireTag("c:storage_blocks/soulsteel"));

            add(new BeeConfig("tetra/geode").primaryColor("#747474").secondaryColor("#804f40").particleColor("#747474").renderer("thicc").noComb().flowerBlock("minecraft:deepslate").requireMod("tetra"));
            add(new BeeConfig("tetra/scrapped").primaryColor("#747474").secondaryColor("#804f40").particleColor("#747474").size(1.2).flowerBlock("tetra:forged_workbench").requireMod("tetra"));

            add(new BeeConfig("thermal/basalz").primaryColor("#2b2b2f").secondaryColor("#ff8219").particleColor("#0e080a").onlySpawnegg().flowerTag("productivebees:flowers/burning").beeTexture("basalz").fireproof().requireMod("thermal"));
            add(new BeeConfig("thermal/blitz").primaryColor("#e9edf3").secondaryColor("#bdccd9").particleColor("#ffd86f").onlySpawnegg().beeTexture("blitz").flowerBlock("thermal:niter_block").attributes(new HashMap<>(){{ put("weather_tolerance", GeneValue.WEATHER_TOLERANCE_ANY.getSerializedName()); }}).requireMod("thermal"));
            add(new BeeConfig("thermal/blizz").primaryColor("#1d7cf1").secondaryColor("#ffffff").particleColor("#8cdeff").onlySpawnegg().beeTexture("blizz").renderer("elvis").flowerTag("productivebees:flowers/frozen").requireMod("thermal"));
            add(new BeeConfig("thermal/destabilized_redstone").primaryColor("#d03621").secondaryColor("#804f40").tertiaryColor("#730c00").particleColor("#ff0000").size(0.8).onlySpawnegg().redstoned().flowerTag("productivebees:flowers/redstone").requireMod("thermal"));
            add(new BeeConfig("thermal/energized_glowstone").primaryColor("#fad87d").secondaryColor("#5f2525").particleColor("#fad87d").onlySpawnegg().size(0.8).particleType("rising").flipped().blinding().requireMod("thermal"));
            add(new BeeConfig("thermal/resonant_ender").primaryColor("#161616").secondaryColor("#623875").particleColor("#cc00fa").particleType("portal").size(0.8).onlySpawnegg().flowerTag("productivebees:flowers/ender").teleporting().requireMod("thermal"));

            add(new BeeConfig("thermalendergy/melodium").primaryColor("#523a8b").secondaryColor("#b390f4").particleColor("#fcd9ea").size(0.5).beeTexture("melodium").onlySpawnegg().noSelfBreed().flowerTag("c:storage_blocks/melodium").requireTag("c:storage_blocks/melodium"));
            add(new BeeConfig("thermalendergy/stellarium").primaryColor("#1e2626").secondaryColor("#728e8e").particleColor("#deeaea").size(0.3).beeTexture("stellarium").onlySpawnegg().noSelfBreed().flowerTag("c:storage_blocks/stellarium").requireTag("c:storage_blocks/stellarium"));
            add(new BeeConfig("thermalendergy/prismalium").primaryColor("#43806e").secondaryColor("#9fe1cb").particleColor("#f7fdfd").size(0.7).beeTexture("prismalium").flowerTag("c:storage_blocks/prismalium").requireTag("c:storage_blocks/prismalium"));

            add(new BeeConfig("thermal_extra/dragonsteel").primaryColor("#15174c").secondaryColor("#2b377f").tertiaryColor("#5f7ecc").particleColor("#79a3ea").onlySpawnegg().size(0.7).flowerBlock("thermal_extra:dragonsteel_block").noSelfBreed().requireMod("thermal_extra"));
            add(new BeeConfig("thermal_extra/shellite").primaryColor("#6d2b7f").secondaryColor("#924ab2").tertiaryColor("#a65fcc").particleColor("#c294e8").onlySpawnegg().size(0.7).flowerBlock("thermal_extra:shellite_block").noSelfBreed().requireMod("thermal_extra"));
            add(new BeeConfig("thermal_extra/soul_infused").primaryColor("#b7785a").secondaryColor("#db9e70").tertiaryColor("#ffd68e").particleColor("#ffd68e").onlySpawnegg().size(0.7).flowerBlock("thermal_extra:soul_infused_block").requireMod("thermal_extra"));
            add(new BeeConfig("thermal_extra/twinite").primaryColor("#e44f76").secondaryColor("#e44f76").tertiaryColor("#ec6f86").particleColor("#fee9f0").onlySpawnegg().size(0.7).flowerBlock("thermal_extra:twinite_block").noSelfBreed().requireMod("thermal_extra"));

            add(new BeeConfig("tombstone/grave").primaryColor("#c9c7c2").secondaryColor("#4f4e4b").particleColor("#c9c7c2").size(0.5).flowerTag("productivebees:flowers/graves").requireMod("tombstone"));

            add(new BeeConfig("undergarden/cloggrum").primaryColor("#493933").secondaryColor("#ab8f6f").particleColor("#f4d7b6").size(0.8).flowerBlock("undergarden:cloggrum_block").beeTexture("cloggrum").requireMod("undergarden"));
            add(new BeeConfig("undergarden/forgotten").primaryColor("#2a695f").secondaryColor("#57e3b3").particleColor("#7bffbd").size(0.3).flowerBlock("undergarden:forgotten_block").beeTexture("forgotten").renderer("default_shell").requireMod("undergarden"));
            add(new BeeConfig("undergarden/froststeel").primaryColor("#4f5781").secondaryColor("#9fc8ef").particleColor("#f5f5f5").size(0.6).flowerBlock("undergarden:froststeel_block").beeTexture("froststeel").requireMod("undergarden"));
            add(new BeeConfig("undergarden/regalium").primaryColor("#964b28").secondaryColor("#fcd87d").particleColor("#ffefb2").size(0.5).flowerBlock("undergarden:regalium_block").beeTexture("regalium").renderer("default_crystal").requireMod("undergarden"));
            add(new BeeConfig("undergarden/utheric").primaryColor("#675d42").secondaryColor("#c3434c").particleColor("#ff8d80").size(0.4).flowerBlock("undergarden:utherium_block").beeTexture("utheric").renderer("default_crystal").requireMod("undergarden"));
        }};
    }

    public Supplier<JsonElement> getBee(BeeConfig bee) {
        return () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("primaryColor", bee.primaryColor);
            if (bee.secondaryColor != null) {
                jsonObject.addProperty("secondaryColor", bee.secondaryColor);
            }
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
            if (bee.flowerTag == null && bee.flowerBlock == null && bee.flowerItem == null && bee.flowerFluid == null) {
                jsonObject.addProperty("flowerTag", "minecraft:flowers");
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
            if (bee.pollinatedSize != 0 && bee.pollinatedSize != bee.size) {
                jsonObject.addProperty("pollinatedSize", bee.pollinatedSize);
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
            if (!bee.useGlowLayer) {
                jsonObject.addProperty("useGlowLayer", false);
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
                JsonObject attributes = new JsonObject();
                bee.attributes.entrySet().forEach(attribute -> {
                    attributes.addProperty(attribute.getKey(), attribute.getValue());
                });
                jsonObject.add("attributes", attributes);
            }
            if (!bee.passiveEffects.isEmpty()) {
                JsonArray effects = new JsonArray();
                bee.passiveEffects.forEach(passiveEffect -> {
                    JsonObject o = new JsonObject();
                    o.addProperty("effect", passiveEffect.name);
                    o.addProperty("duration", passiveEffect.duration);
                    effects.add(o);
                });
                jsonObject.add("passiveEffects", effects);
            }
            if (!bee.conditions.isEmpty()) {
                JsonArray conditions = new JsonArray();
                bee.conditions.forEach(condition -> {
                    conditions.add(ICondition.CODEC.encode(condition, JsonOps.INSTANCE, new JsonObject()).getOrThrow());
                });
                jsonObject.add("conditions", conditions);
            }
            return jsonObject;
        };
    }



    public static class BeeConfig {
        public static Codec<BeeConfig> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                            Codec.STRING.fieldOf("name").forGetter(c -> c.name)
                ).apply(builder, BeeConfig::new));
        public static Codec<Optional<WithConditions<BeeConfig>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(CODEC);

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
        float pollinatedSize = 0f;
        float speed = 1.0f;
        double attack = 1.0f;
        boolean createComb = true;
        boolean selfBreed = true;
        boolean selfHeal = false;
        boolean inverseFlower = false;
        boolean teleporting = false;
        boolean translucent = false;
        boolean useGlowLayer = true;
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
        Map<String, String> attributes = new HashMap<>();
        List<PassiveEffect> passiveEffects = new ArrayList<>();
        List<ICondition> conditions = new ArrayList<>();

        public BeeConfig(String name) {
            this.name = name;
        }

        public BeeConfig primaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
            return this;
        }
        public BeeConfig secondaryColor(String secondaryColor) {
            this.secondaryColor = secondaryColor;
            return this;
        }
        public BeeConfig tertiaryColor(String tertiaryColor) {
            this.tertiaryColor = tertiaryColor;
            return this;
        }
        public BeeConfig particleColor(String particleColor) {
            this.particleColor = particleColor;
            return this;
        }
        public BeeConfig particleType(String particleType) {
            this.particleType = particleType;
            return this;
        }
        public BeeConfig beeTexture(String beeTexture) {
            this.beeTexture = "productivebees:textures/entity/bee/" + beeTexture + "/bee";
            return this;
        }
        public BeeConfig description(String description) {
            this.description = description;
            return this;
        }
        public BeeConfig onlySpawnegg() {
            this.description = "productivebees.ingredient.description.only_spawnegg";
            return this;
        }
        public BeeConfig flowerTag(String flowerTag) {
            this.flowerTag = flowerTag;
            return this;
        }
        public BeeConfig flowerBlock(String flowerBlock) {
            this.flowerBlock = flowerBlock;
            return this;
        }
        public BeeConfig flowerItem(String flowerItem) {
            this.flowerItem = flowerItem;
            return this;
        }
        public BeeConfig flowerFluid(String flowerFluid) {
            this.flowerFluid = flowerFluid;
            return this;
        }
        public BeeConfig flowerType(String flowerType) {
            this.flowerType = flowerType;
            return this;
        }
        public BeeConfig renderer(String renderer) {
            this.renderer = renderer;
            return this;
        }
        public BeeConfig flipped() {
            this.renderTransform = "flipped";
            return this;
        }
        public BeeConfig breedingItem(String breedingItem) {
            this.breedingItem = breedingItem;
            return this;
        }
        public BeeConfig breedingItemCount(Integer breedingItemCount) {
            this.breedingItemCount = breedingItemCount;
            return this;
        }
        public BeeConfig size(double size) {
            this.size = (float) size;
            return this;
        }
        public BeeConfig pollinatedSize(double pollinatedSize) {
            this.pollinatedSize = (float) pollinatedSize;
            return this;
        }
        public BeeConfig speed(double speed) {
            this.speed = (float) speed;
            return this;
        }
        public BeeConfig attack(double attack) {
            this.attack = attack;
            return this;
        }
        public BeeConfig attackResponse(String attackResponse) {
            this.attackResponse = attackResponse;
            return this;
        }
        public BeeConfig nestingPreference(String nestingPreference) {
            this.nestingPreference = nestingPreference;
            return this;
        }
        public BeeConfig postPollination(String postPollination) {
            this.postPollination = postPollination;
            return this;
        }
        public BeeConfig noComb() {
            this.createComb = false;
            return this;
        }
        public BeeConfig noSelfBreed() {
            this.selfBreed = false;
            return this;
        }
        public BeeConfig selfHeal() {
            this.selfHeal = true;
            return this;
        }
        public BeeConfig inverseFlower() {
            this.inverseFlower = true;
            return this;
        }
        public BeeConfig translucent() {
            this.translucent = true;
            return this;
        }
        public BeeConfig noGlow() {
            this.useGlowLayer = false;
            return this;
        }
        public BeeConfig teleporting() {
            this.teleporting = true;
            return this;
        }
        public BeeConfig redstoned() {
            this.redstoned = true;
            return this;
        }
        public BeeConfig irradiated() {
            this.irradiated = true;
            return this;
        }
        public BeeConfig slimy() {
            this.slimy = true;
            return this;
        }
        public BeeConfig fireproof() {
            this.fireproof = true;
            return this;
        }
        public BeeConfig draconic() {
            this.draconic = true;
            return this;
        }
        public BeeConfig withered() {
            this.withered = true;
            return this;
        }
        public BeeConfig blinding() {
            this.blinding = true;
            return this;
        }
        public BeeConfig stringy() {
            this.stringy = true;
            return this;
        }
        public BeeConfig waterproof() {
            this.waterproof = true;
            return this;
        }
        public BeeConfig coldResistant() {
            this.coldResistant = true;
            return this;
        }
        public BeeConfig munchies() {
            this.munchies = true;
            return this;
        }
        public BeeConfig stingless() {
            this.stingless = true;
            return this;
        }
        public BeeConfig invulnerability(List<String> invulnerability) {
            this.invulnerability = invulnerability;
            return this;
        }
        public BeeConfig attributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }
        public BeeConfig passiveEffects(List<PassiveEffect> effects) {
            this.passiveEffects = effects;
            return this;
        }
        public BeeConfig requireMod(String modId) {
            this.conditions.add(new ModLoadedCondition(modId));
            return this;
        }
        public BeeConfig requireEitherMod(String modId, String modId2) {
            this.conditions.add(new OrCondition(List.of(new ModLoadedCondition(modId), new ModLoadedCondition(modId2))));
            return this;
        }
        public BeeConfig missingMod(String modId) {
            this.conditions.add(new NotCondition(new ModLoadedCondition(modId)));
            return this;
        }
        public BeeConfig requireTag(String tag) {
            this.conditions.add(new NotCondition(new TagEmptyCondition(tag)));
            return this;
        }
        public BeeConfig missingTag(String tag) {
            this.conditions.add(new TagEmptyCondition(tag));
            return this;
        }
        public BeeConfig requireFluidTag(String nameSpace, String tag) {
            this.conditions.add(new NotCondition(new FluidTagEmptyCondition(nameSpace, tag)));
            return this;
        }
    }

    record PassiveEffect(String name, Integer duration){}
}
