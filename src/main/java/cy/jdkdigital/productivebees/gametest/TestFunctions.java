package cy.jdkdigital.productivebees.gametest;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registers our test bodies into {@link BuiltInRegistries#TEST_FUNCTION}.
 *
 * <p><b>Why this isn't done via {@code TestFunctionLoader}.</b> Vanilla's loader hook
 * ({@code TestFunctionLoader.registerLoader}) only fires when {@code BuiltInRegistries.bootStrap()}
 * calls {@code BuiltinTestFunctions.bootstrap}. In gameTestServer mode, vanilla's
 * {@code GameTestMainUtil.runGameTestServer} runs {@code Bootstrap.bootStrap()} <i>before</i>
 * {@code ServerModLoader.load(true)} — so by the time our mod constructor executes,
 * TEST_FUNCTION has already been populated and frozen. Registering a loader at that point is a
 * no-op.
 *
 * <p><b>What we do instead.</b> Unfreeze {@link BuiltInRegistries#TEST_FUNCTION}, register each
 * test function, refreeze. Mirrors how NeoForge handles modded entries for other built-in
 * registries that get frozen during {@code Bootstrap.bootStrap()}. The unfreeze hook is marked
 * {@code @Deprecated for internal use only} but is the only available path — and the alternative
 * (failing test instances at runtime with "Trying to access missing test function") is worse.
 *
 * <p>Called from the {@link ProductiveBees} mod constructor. After {@link #init()} returns,
 * {@code BuiltInRegistries.TEST_FUNCTION} contains every entry pushed via {@link #register}.
 */
public final class TestFunctions
{
    private static final Map<String, Consumer<GameTestHelper>> FUNCTIONS = new LinkedHashMap<>();
    private static boolean published;

    private TestFunctions() {}

    /**
     * Records a test function body. Returns the ResourceKey for use in the
     * {@code FunctionGameTestInstance.function} field of the corresponding
     * {@code test_instance/<name>.json}.
     *
     * <p>Must be called before {@link #init()} runs.
     */
    public static ResourceKey<Consumer<GameTestHelper>> register(String name, Consumer<GameTestHelper> body) {
        if (published) {
            throw new IllegalStateException("TestFunctions.register called after init() — too late, registry is already published.");
        }
        if (FUNCTIONS.put(name, body) != null) {
            throw new IllegalStateException("Duplicate test function registration: " + name);
        }
        return key(name);
    }

    public static ResourceKey<Consumer<GameTestHelper>> key(String name) {
        return ResourceKey.create(Registries.TEST_FUNCTION, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, name));
    }

    /** Iterable view used by {@link TestEntriesProvider} to emit one JSON per test. */
    public static Iterable<String> names() {
        return FUNCTIONS.keySet();
    }

    /**
     * Publishes recorded functions to {@link BuiltInRegistries#TEST_FUNCTION}. Idempotent —
     * safe to call multiple times. Must be called after the FUNCTIONS map is fully populated
     * (i.e. after {@code ProductiveBeesGameTests} class init).
     */
    public static void init() {
        if (published) return;
        published = true;

        Registry<Consumer<GameTestHelper>> registry = BuiltInRegistries.TEST_FUNCTION;
        if (!(registry instanceof MappedRegistry<Consumer<GameTestHelper>> mapped)) {
            throw new IllegalStateException("BuiltInRegistries.TEST_FUNCTION is not a MappedRegistry — cannot unfreeze");
        }

        mapped.unfreeze(false);
        try {
            FUNCTIONS.forEach((name, body) -> Registry.register(mapped, key(name), body));
        } finally {
            mapped.freeze();
        }
    }
}
