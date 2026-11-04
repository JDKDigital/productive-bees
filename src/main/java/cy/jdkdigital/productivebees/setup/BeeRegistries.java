package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/** Datapack registry key + static helpers for {@link BeeData}. */
public final class BeeRegistries
{
    public static final ResourceKey<Registry<BeeData>> BEE_DATA = ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_data"));

    /** Volatile so recipe-sync IO threads see the server-thread write from {@code ServerStartedEvent}. */
    @Nullable
    private static volatile RegistryAccess cachedAccess;

    /** Read by datagen providers that run before {@code DatapackBuiltinEntriesProvider} materialises the registry. */
    private static Map<Identifier, BeeData> datagenFallback = Map.of();

    /** Bees whose tag gates fail on this server — hidden from discovery, still resolvable by id. */
    private static Set<Identifier> hiddenIds = Set.of();

    /** {@code <ns>:<last-segment>} → canonical full id, for legacy simple-name lookups. */
    private static volatile Map<Identifier, Identifier> simpleNameIndex = Map.of();

    private BeeRegistries() {}

    public static void setRegistries(@Nullable RegistryAccess registries) {
        cachedAccess = registries;
    }

    public static void setDatagenFallback(Map<Identifier, BeeData> data) {
        datagenFallback = data;
    }

    /** Rebuilds {@link #hiddenIds} and {@link #simpleNameIndex}. Call after tags bind. */
    public static void evaluateRuntimeGates(@Nullable RegistryAccess registries) {
        Optional<Registry<BeeData>> beeLookup = registries == null ? Optional.empty() : registries.lookup(BEE_DATA);
        if (beeLookup.isEmpty()) {
            hiddenIds = Set.of();
            simpleNameIndex = Map.of();
            return;
        }
        Set<Identifier> hidden = new HashSet<>();
        Map<Identifier, Identifier> simpleIndex = new HashMap<>();
        beeLookup.get().listElements().forEach(holder -> {
            Identifier fullId = holder.key().identifier();
            String path = fullId.getPath();
            int slash = path.lastIndexOf('/');
            if (slash >= 0) {
                Identifier simpleId = Identifier.fromNamespaceAndPath(fullId.getNamespace(), path.substring(slash + 1));
                simpleIndex.putIfAbsent(simpleId, fullId);
            }
            BeeData data = holder.value();
            if (!gatesPass(registries, data)) {
                hidden.add(fullId);
                return;
            }
            // A bee whose only flower is a block tag nothing fills has nothing to pollinate.
            // entity_types flowerType reads the tag against the entity registry, not item/block.
            if (data.flowerTag().isEmpty() || data.inverseFlower() || !"blocks".equals(data.flowerType())) {
                return;
            }
            Identifier tagId = Identifier.parse(data.flowerTag().get());
            if (itemTagEmpty(registries.lookup(Registries.ITEM), tagId)
                    && blockTagEmpty(registries.lookup(Registries.BLOCK), tagId)) {
                hidden.add(fullId);
            }
        });
        hiddenIds = Set.copyOf(hidden);
        simpleNameIndex = Map.copyOf(simpleIndex);
    }

    /**
     * Tag gates declared on the bee itself, evaluated once tags bind. They are not
     * {@code neoforge:conditions} because a client loading bee data from its own jar gets
     * {@link net.neoforged.neoforge.common.conditions.ICondition.IContext#EMPTY}, where every tag
     * reads as empty and the entry is dropped client-side while the server keeps it.
     */
    public static boolean gatesPass(RegistryAccess registries, BeeData data) {
        Optional<Registry<Item>> items = registries.lookup(Registries.ITEM);
        Optional<Registry<Block>> blocks = registries.lookup(Registries.BLOCK);
        for (String tag : data.requiredTags()) {
            Identifier id = Identifier.parse(tag);
            if (itemTagEmpty(items, id) && blockTagEmpty(blocks, id)) {
                return false;
            }
        }
        for (String tag : data.forbiddenTags()) {
            Identifier id = Identifier.parse(tag);
            if (!itemTagEmpty(items, id) || !blockTagEmpty(blocks, id)) {
                return false;
            }
        }
        if (!data.requiredFluidTags().isEmpty()) {
            Optional<Registry<Fluid>> fluids = registries.lookup(Registries.FLUID);
            for (String tag : data.requiredFluidTags()) {
                if (fluidTagEmpty(fluids, Identifier.parse(tag))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean fluidTagEmpty(Optional<Registry<Fluid>> lookup, Identifier tagId) {
        if (lookup.isEmpty()) return true;
        return lookup.get().get(TagKey.create(Registries.FLUID, tagId)).map(named -> named.size() == 0).orElse(true);
    }

    private static boolean itemTagEmpty(Optional<Registry<Item>> lookup, Identifier tagId) {
        if (lookup.isEmpty()) return true;
        return lookup.get().get(TagKey.create(Registries.ITEM, tagId)).map(named -> named.size() == 0).orElse(true);
    }

    private static boolean blockTagEmpty(Optional<Registry<Block>> lookup, Identifier tagId) {
        if (lookup.isEmpty()) return true;
        return lookup.get().get(TagKey.create(Registries.BLOCK, tagId)).map(named -> named.size() == 0).orElse(true);
    }

    /** Maps a simple-name id to its full path; returns the input unchanged when no mapping exists. */
    public static Identifier resolveId(Identifier beeId) {
        if (beeId == null) return null;
        Identifier mapped = simpleNameIndex.get(beeId);
        return mapped != null ? mapped : beeId;
    }



    /** Returns the bee data by id, ignoring the hidden set. */
    @Nullable
    public static BeeData lookup(@Nullable Identifier beeId) {
        if (beeId == null) return null;
        if (cachedAccess != null) {
            return get(cachedAccess, beeId);
        }
        return datagenFallback.get(beeId);
    }

    @Nullable
    public static BeeData get(RegistryAccess registries, Identifier beeId) {
        if (beeId == null) return null;
        return registries.lookup(BEE_DATA)
                .flatMap(reg -> {
                    Optional<Holder.Reference<BeeData>> direct = reg.get(ResourceKey.create(BEE_DATA, beeId));
                    if (direct.isPresent()) return direct;
                    Identifier resolved = simpleNameIndex.get(beeId);
                    return resolved != null ? reg.get(ResourceKey.create(BEE_DATA, resolved)) : Optional.empty();
                })
                .map(Holder.Reference::value)
                .orElse(null);
    }

    public static Optional<Holder.Reference<BeeData>> getHolder(RegistryAccess registries, Identifier beeId) {
        if (beeId == null) return Optional.empty();
        return registries.lookup(BEE_DATA)
                .flatMap(reg -> {
                    Optional<Holder.Reference<BeeData>> direct = reg.get(ResourceKey.create(BEE_DATA, beeId));
                    if (direct.isPresent()) return direct;
                    Identifier resolved = simpleNameIndex.get(beeId);
                    return resolved != null ? reg.get(ResourceKey.create(BEE_DATA, resolved)) : Optional.empty();
                });
    }

    /** Visible bees only (filters {@link #hiddenIds}) — for discovery surfaces. */
    public static Stream<Holder.Reference<BeeData>> all() {
        if (cachedAccess != null) {
            return cachedAccess.lookup(BEE_DATA)
                    .map(reg -> reg.listElements().filter(h -> !hiddenIds.contains(h.key().identifier())))
                    .orElseGet(Stream::empty);
        }
        return Stream.empty();
    }

    /** Every registered bee — for recipe-ingredient resolution. */
    public static Stream<Holder.Reference<BeeData>> allRegistered() {
        if (cachedAccess != null) {
            return cachedAccess.lookup(BEE_DATA).map(Registry::listElements).orElseGet(Stream::empty);
        }
        return Stream.empty();
    }

    public static int size() {
        return (int) all().count();
    }

    public static int registeredSize() {
        return (int) allRegistered().count();
    }
}
