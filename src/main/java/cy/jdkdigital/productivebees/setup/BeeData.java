package cy.jdkdigital.productivebees.setup;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Variant data for one configurable bee. Held in the {@link BeeRegistries#BEE_DATA} datapack
 * registry; loaded from {@code data/<ns>/productivebees/bee_data/<name>.json}.
 *
 * <p>The JSON schema is intentionally identical to the legacy {@code BeeReloadListener}
 * format, so existing custom bee datapacks only need to move files into a {@code bee_data/}
 * subfolder — the JSON content itself parses unchanged. The codec is hand-rolled because
 * Mojang's {@code RecordCodecBuilder} caps at 16 fields per group and a flat top-level schema
 * is required for backward compatibility (sub-records would force nested JSON).
 */
public record BeeData(
        int primaryColor,
        int secondaryColor,
        int tertiaryColor,
        Optional<Integer> particleColor,
        boolean colorCycle,

        Optional<String> description,

        Optional<String> flowerTag,
        Optional<String> flowerBlock,
        Optional<String> flowerFluid,
        Optional<String> flowerItem,
        boolean inverseFlower,
        String flowerType,

        Optional<String> nestingPreference,
        Optional<String> beeTexture,
        Optional<String> model,
        Optional<String> animation,
        String renderer,
        String renderTransform,
        String particleType,
        boolean translucent,
        boolean useGlowLayer,

        String breedingItem,
        int breedingItemCount,
        boolean selfbreed,
        boolean selfheal,

        float size,
        float pollinatedSize,
        float speed,
        double attack,
        Optional<String> attackResponse,
        List<String> invulnerability,

        Optional<String> postPollination,
        boolean fireproof,
        boolean withered,
        boolean blinding,
        boolean draconic,
        boolean slimy,
        boolean teleporting,
        boolean munchies,
        boolean redstoned,
        boolean stringy,
        boolean stingless,
        boolean waterproof,
        boolean coldResistant,
        boolean irradiated,

        BeeAttributes attributes,

        boolean createComb
) {
    public static final BeeAttributes EMPTY_ATTRIBUTES = new BeeAttributes(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /** Gene attribute strings — emitted as a nested JSON object under the {@code attributes} key. */
    public record BeeAttributes(
            Optional<String> productivity,
            Optional<String> endurance,
            Optional<String> temper,
            Optional<String> behavior,
            Optional<String> weatherTolerance
    ) {
        public static final MapCodec<BeeAttributes> MAP_CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                Codec.STRING.optionalFieldOf("productivity").forGetter(BeeAttributes::productivity),
                Codec.STRING.optionalFieldOf("endurance").forGetter(BeeAttributes::endurance),
                Codec.STRING.optionalFieldOf("temper").forGetter(BeeAttributes::temper),
                Codec.STRING.optionalFieldOf("behavior").forGetter(BeeAttributes::behavior),
                Codec.STRING.optionalFieldOf("weather_tolerance").forGetter(BeeAttributes::weatherTolerance)
        ).apply(b, BeeAttributes::new));
        public static final Codec<BeeAttributes> CODEC = MAP_CODEC.codec();
    }

    /** Hex-string codec ({@code "#RRGGBB"}) ↔ ARGB int, matching {@code BeeCreator.getColor()}. */
    public static final Codec<Integer> HEX_COLOR_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                String hex = s.startsWith("#") ? s.substring(1) : s;
                if (hex.length() != 6) {
                    return DataResult.error(() -> "Expected #RRGGBB color, got '" + s + "'");
                }
                try {
                    int r = Integer.parseInt(hex.substring(0, 2), 16);
                    int g = Integer.parseInt(hex.substring(2, 4), 16);
                    int b = Integer.parseInt(hex.substring(4, 6), 16);
                    return DataResult.success(ARGB.color(r, g, b));
                } catch (NumberFormatException e) {
                    return DataResult.error(() -> "Invalid hex color '" + s + "': " + e.getMessage());
                }
            },
            argb -> String.format("#%06x", argb & 0xFFFFFF)
    );

    public static final MapCodec<BeeData> MAP_CODEC = new MapCodec<>() {
        private static final List<String> FIELD_NAMES = List.of(
                "primaryColor", "secondaryColor", "tertiaryColor", "particleColor", "colorCycle",
                "description",
                "flowerTag", "flowerBlock", "flowerFluid", "flowerItem", "inverseFlower", "flowerType",
                "nestingPreference", "beeTexture", "model", "animation",
                "renderer", "renderTransform", "particleType", "translucent", "useGlowLayer",
                "breedingItem", "breedingItemCount", "selfbreed", "selfheal",
                "size", "pollinatedSize", "speed", "attack", "attackResponse", "invulnerability",
                "postPollination", "fireproof", "withered", "blinding", "draconic", "slimy",
                "teleporting", "munchies", "redstoned", "stringy", "stingless", "waterproof",
                "coldResistant", "irradiated",
                "attributes",
                "createComb"
        );

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return FIELD_NAMES.stream().map(ops::createString);
        }

        @Override
        public <T> DataResult<BeeData> decode(DynamicOps<T> ops, MapLike<T> input) {
            try {
                int primaryColor = required(ops, input, "primaryColor", HEX_COLOR_CODEC);
                int secondaryColor = optional(ops, input, "secondaryColor", HEX_COLOR_CODEC).orElse(primaryColor);
                int tertiaryColor = optional(ops, input, "tertiaryColor", HEX_COLOR_CODEC).orElse(primaryColor);
                Optional<Integer> particleColor = optional(ops, input, "particleColor", HEX_COLOR_CODEC);
                boolean colorCycle = optional(ops, input, "colorCycle", Codec.BOOL).orElse(false);

                Optional<String> description = optional(ops, input, "description", Codec.STRING);

                // Flower tag/block/fluid/item: legacy "!"-prefixed tag means inverse.
                String rawFlowerTag = optional(ops, input, "flowerTag", Codec.STRING).orElse(null);
                Optional<String> flowerTag;
                boolean inverseFlowerFromTag = false;
                if (rawFlowerTag != null && rawFlowerTag.startsWith("!")) {
                    inverseFlowerFromTag = true;
                    flowerTag = Optional.of(rawFlowerTag.substring(1));
                } else {
                    flowerTag = Optional.ofNullable(rawFlowerTag);
                }
                Optional<String> flowerBlock = optional(ops, input, "flowerBlock", Codec.STRING);
                Optional<String> flowerFluid = optional(ops, input, "flowerFluid", Codec.STRING);
                Optional<String> flowerItem = optional(ops, input, "flowerItem", Codec.STRING);
                boolean inverseFlower = inverseFlowerFromTag
                        || optional(ops, input, "inverseFlower", Codec.BOOL).orElse(false);
                if (flowerTag.isEmpty() && flowerBlock.isEmpty() && flowerFluid.isEmpty() && flowerItem.isEmpty()) {
                    flowerTag = Optional.of("minecraft:flowers");
                }
                String flowerType = optional(ops, input, "flowerType", Codec.STRING).orElse("blocks");

                Optional<String> nestingPreference = optional(ops, input, "nestingPreference", Codec.STRING);
                Optional<String> beeTexture = optional(ops, input, "beeTexture", Codec.STRING);
                Optional<String> model = optional(ops, input, "model", Codec.STRING);
                Optional<String> animation = optional(ops, input, "animation", Codec.STRING);
                String renderer = optional(ops, input, "renderer", Codec.STRING).orElse("default");
                String renderTransform = optional(ops, input, "renderTransform", Codec.STRING).orElse("none");
                String particleType = optional(ops, input, "particleType", Codec.STRING).orElse("drip");
                boolean translucent = optional(ops, input, "translucent", Codec.BOOL)
                        .orElse(renderer.equals("translucent_with_center"));
                boolean useGlowLayer = optional(ops, input, "useGlowLayer", Codec.BOOL)
                        .orElse(renderer.equals("default_crystal"));

                String breedingItem = optional(ops, input, "breedingItem", Codec.STRING).orElse("");
                int breedingItemCount = optional(ops, input, "breedingItemCount", Codec.INT).orElse(1);
                boolean selfbreed = optional(ops, input, "selfbreed", Codec.BOOL).orElse(true);
                boolean selfheal = optional(ops, input, "selfheal", Codec.BOOL).orElse(false);

                float size = optional(ops, input, "size", Codec.FLOAT).orElse(1.0f);
                float pollinatedSize = optional(ops, input, "pollinatedSize", Codec.FLOAT).orElse(size);
                float speed = optional(ops, input, "speed", Codec.FLOAT).orElse(1.0f);
                double attack = optional(ops, input, "attack", Codec.DOUBLE).orElse(2.0);
                Optional<String> attackResponse = optional(ops, input, "attackResponse", Codec.STRING);
                List<String> invulnerability = optional(ops, input, "invulnerability", Codec.STRING.listOf())
                        .orElse(List.of());

                Optional<String> postPollination = optional(ops, input, "postPollination", Codec.STRING);
                boolean fireproof = optional(ops, input, "fireproof", Codec.BOOL).orElse(false);
                boolean withered = optional(ops, input, "withered", Codec.BOOL).orElse(false);
                boolean blinding = optional(ops, input, "blinding", Codec.BOOL).orElse(false);
                boolean draconic = optional(ops, input, "draconic", Codec.BOOL).orElse(false);
                boolean slimy = optional(ops, input, "slimy", Codec.BOOL).orElse(false);
                boolean teleporting = optional(ops, input, "teleporting", Codec.BOOL).orElse(false);
                boolean munchies = optional(ops, input, "munchies", Codec.BOOL).orElse(false);
                boolean redstoned = optional(ops, input, "redstoned", Codec.BOOL).orElse(false);
                boolean stringy = optional(ops, input, "stringy", Codec.BOOL).orElse(false);
                boolean stingless = optional(ops, input, "stingless", Codec.BOOL).orElse(false);
                boolean waterproof = optional(ops, input, "waterproof", Codec.BOOL).orElse(false);
                boolean coldResistant = optional(ops, input, "coldResistant", Codec.BOOL).orElse(false);
                boolean irradiated = optional(ops, input, "irradiated", Codec.BOOL).orElse(false);

                BeeAttributes attributes = optional(ops, input, "attributes", BeeAttributes.CODEC).orElse(EMPTY_ATTRIBUTES);

                boolean createComb = optional(ops, input, "createComb", Codec.BOOL).orElse(true);

                return DataResult.success(new BeeData(
                        primaryColor, secondaryColor, tertiaryColor, particleColor, colorCycle,
                        description,
                        flowerTag, flowerBlock, flowerFluid, flowerItem, inverseFlower, flowerType,
                        nestingPreference, beeTexture, model, animation,
                        renderer, renderTransform, particleType, translucent, useGlowLayer,
                        breedingItem, breedingItemCount, selfbreed, selfheal,
                        size, pollinatedSize, speed, attack, attackResponse, invulnerability,
                        postPollination, fireproof, withered, blinding, draconic, slimy,
                        teleporting, munchies, redstoned, stringy, stingless, waterproof,
                        coldResistant, irradiated,
                        attributes,
                        createComb
                ));
            } catch (DecodeError e) {
                return DataResult.error(e::getMessage);
            }
        }

        @Override
        public <T> RecordBuilder<T> encode(BeeData value, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            // Always emit required + sensibly-defaulted fields. Skip truly-optional empties.
            prefix.add("primaryColor", HEX_COLOR_CODEC.encodeStart(ops, value.primaryColor));
            if (value.secondaryColor != value.primaryColor) {
                prefix.add("secondaryColor", HEX_COLOR_CODEC.encodeStart(ops, value.secondaryColor));
            }
            if (value.tertiaryColor != value.primaryColor) {
                prefix.add("tertiaryColor", HEX_COLOR_CODEC.encodeStart(ops, value.tertiaryColor));
            }
            value.particleColor.ifPresent(c -> prefix.add("particleColor", HEX_COLOR_CODEC.encodeStart(ops, c)));
            if (value.colorCycle) prefix.add("colorCycle", ops.createBoolean(true));

            value.description.ifPresent(d -> prefix.add("description", ops.createString(d)));

            // Flower: write tag with "!" prefix re-applied if inverse + a tag is present.
            value.flowerTag.ifPresent(t -> prefix.add("flowerTag",
                    ops.createString(value.inverseFlower ? "!" + t : t)));
            value.flowerBlock.ifPresent(b -> prefix.add("flowerBlock", ops.createString(b)));
            value.flowerFluid.ifPresent(f -> prefix.add("flowerFluid", ops.createString(f)));
            value.flowerItem.ifPresent(i -> prefix.add("flowerItem", ops.createString(i)));
            if (value.inverseFlower && value.flowerTag.isEmpty()) {
                prefix.add("inverseFlower", ops.createBoolean(true));
            }
            if (!value.flowerType.equals("blocks")) prefix.add("flowerType", ops.createString(value.flowerType));

            value.nestingPreference.ifPresent(n -> prefix.add("nestingPreference", ops.createString(n)));
            value.beeTexture.ifPresent(t -> prefix.add("beeTexture", ops.createString(t)));
            value.model.ifPresent(m -> prefix.add("model", ops.createString(m)));
            value.animation.ifPresent(a -> prefix.add("animation", ops.createString(a)));
            if (!value.renderer.equals("default")) prefix.add("renderer", ops.createString(value.renderer));
            if (!value.renderTransform.equals("none")) prefix.add("renderTransform", ops.createString(value.renderTransform));
            if (!value.particleType.equals("drip")) prefix.add("particleType", ops.createString(value.particleType));
            if (value.translucent != value.renderer.equals("translucent_with_center")) {
                prefix.add("translucent", ops.createBoolean(value.translucent));
            }
            if (value.useGlowLayer != value.renderer.equals("default_crystal")) {
                prefix.add("useGlowLayer", ops.createBoolean(value.useGlowLayer));
            }

            if (!value.breedingItem.isEmpty()) prefix.add("breedingItem", ops.createString(value.breedingItem));
            if (value.breedingItemCount != 1) prefix.add("breedingItemCount", ops.createInt(value.breedingItemCount));
            if (!value.selfbreed) prefix.add("selfbreed", ops.createBoolean(false));
            if (value.selfheal) prefix.add("selfheal", ops.createBoolean(true));

            if (value.size != 1.0f) prefix.add("size", ops.createFloat(value.size));
            if (value.pollinatedSize != value.size) prefix.add("pollinatedSize", ops.createFloat(value.pollinatedSize));
            if (value.speed != 1.0f) prefix.add("speed", ops.createFloat(value.speed));
            if (value.attack != 2.0) prefix.add("attack", ops.createDouble(value.attack));
            value.attackResponse.ifPresent(a -> prefix.add("attackResponse", ops.createString(a)));
            if (!value.invulnerability.isEmpty()) {
                prefix.add("invulnerability", Codec.STRING.listOf().encodeStart(ops, value.invulnerability));
            }

            value.postPollination.ifPresent(p -> prefix.add("postPollination", ops.createString(p)));
            if (value.fireproof) prefix.add("fireproof", ops.createBoolean(true));
            if (value.withered) prefix.add("withered", ops.createBoolean(true));
            if (value.blinding) prefix.add("blinding", ops.createBoolean(true));
            if (value.draconic) prefix.add("draconic", ops.createBoolean(true));
            if (value.slimy) prefix.add("slimy", ops.createBoolean(true));
            if (value.teleporting) prefix.add("teleporting", ops.createBoolean(true));
            if (value.munchies) prefix.add("munchies", ops.createBoolean(true));
            if (value.redstoned) prefix.add("redstoned", ops.createBoolean(true));
            if (value.stringy) prefix.add("stringy", ops.createBoolean(true));
            if (value.stingless) prefix.add("stingless", ops.createBoolean(true));
            if (value.waterproof) prefix.add("waterproof", ops.createBoolean(true));
            if (value.coldResistant) prefix.add("coldResistant", ops.createBoolean(true));
            if (value.irradiated) prefix.add("irradiated", ops.createBoolean(true));

            if (!value.attributes.equals(EMPTY_ATTRIBUTES)) {
                prefix.add("attributes", BeeAttributes.CODEC.encodeStart(ops, value.attributes));
            }

            if (!value.createComb) prefix.add("createComb", ops.createBoolean(false));

            return prefix;
        }
    };

    public static final Codec<BeeData> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, BeeData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    /** Display group derived from the path: subfolder name capitalized, or {@code "Minecraft"}. */
    public static String groupFor(Identifier beeId) {
        String path = beeId.getPath();
        int slash = path.lastIndexOf('/');
        if (slash < 0) {
            return "Minecraft";
        }
        String group = path.substring(0, slash);
        return group.isEmpty() ? "Minecraft" : group.substring(0, 1).toUpperCase() + group.substring(1);
    }

    // ── Codec helpers ───────────────────────────────────────────────────────────
    private static <T, A> A required(DynamicOps<T> ops, MapLike<T> input, String key, Codec<A> codec) throws DecodeError {
        T raw = input.get(key);
        if (raw == null) throw new DecodeError("Missing required field '" + key + "'");
        return codec.decode(ops, raw).map(Pair::getFirst)
                .getOrThrow(s -> new DecodeError("Field '" + key + "': " + s));
    }

    private static <T, A> Optional<A> optional(DynamicOps<T> ops, MapLike<T> input, String key, Codec<A> codec) throws DecodeError {
        T raw = input.get(key);
        if (raw == null) return Optional.empty();
        return Optional.of(codec.decode(ops, raw).map(Pair::getFirst)
                .getOrThrow(s -> new DecodeError("Field '" + key + "': " + s)));
    }

    private static class DecodeError extends RuntimeException {
        DecodeError(String msg) { super(msg); }
    }
}
