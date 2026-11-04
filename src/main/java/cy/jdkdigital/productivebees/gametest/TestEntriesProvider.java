package cy.jdkdigital.productivebees.gametest;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Emits {@code data/productivebees/test_instance/<test>.json}, a vanilla
 * {@code minecraft:function} test pointing at a {@code productivebees:<test>} function key
 * (registered by {@link TestFunctions}).
 *
 * <p>Every {@code FunctionGameTestInstance} must reference an environment Holder. These all
 * reference {@code minecraft:default}, an empty {@code all_of} that ships inside the vanilla
 * jar, so it resolves on both sides of dev singleplayer: the server during datapack reload and
 * the client during {@code NetworkRegistryLoadTask}, where the server's KnownPack-matched sync
 * skips encoding the content. Sharing one environment also lets the runner batch the tests
 * together instead of giving each its own batch.
 */
public class TestEntriesProvider implements DataProvider
{
    private static final Identifier DEFAULT_ENVIRONMENT = Identifier.fromNamespaceAndPath("minecraft", "default");

    private final PackOutput.PathProvider instancePath;

    public TestEntriesProvider(PackOutput output) {
        this.instancePath = output.createPathProvider(PackOutput.Target.DATA_PACK, "test_instance");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        // Force <clinit> of ProductiveBeesGameTests so register(...) calls populate MAX_TICKS.
        // Datagen is its own JVM and wouldn't otherwise touch this class.
        @SuppressWarnings("unused")
        var force = ProductiveBeesGameTests.MAX_TICKS;

        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ProductiveBeesGameTests.MAX_TICKS.entrySet()) {
            String name = entry.getKey();
            int maxTicks = entry.getValue();
            Identifier instanceId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, name);

            futures.add(writeJson(cache, instancePath.json(instanceId), instanceJson(name, DEFAULT_ENVIRONMENT, maxTicks)));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private static CompletableFuture<?> writeJson(CachedOutput cache, Path path, JsonObject json) {
        // Mirrors the (private) DataProvider.saveStable(CachedOutput, JsonElement, Path) shape:
        // serialise to bytes via Gson, hash with SHA-1, hand to CachedOutput.writeIfNeeded.
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HashingOutputStream hashed = new HashingOutputStream(Hashing.sha1(), bytes);
                try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(hashed, StandardCharsets.UTF_8))) {
                    writer.setSerializeNulls(false);
                    writer.setIndent("  ");
                    GsonHelper.writeValue(writer, json, null);
                }
                cache.writeIfNeeded(path, bytes.toByteArray(), hashed.hash());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write " + path, e);
            }
        });
    }


    private static JsonObject instanceJson(String name, Identifier envId, int maxTicks) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:function");
        obj.addProperty("environment", envId.toString());
        obj.addProperty("function", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, name).toString());
        obj.addProperty("structure", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "empty_7x7").toString());
        obj.addProperty("max_ticks", maxTicks);
        obj.addProperty("required", true);
        obj.addProperty("setup_ticks", 0);
        return obj;
    }

    @Override
    public String getName() {
        return "ProductiveBees GameTest Entries";
    }
}
