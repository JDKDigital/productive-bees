package cy.jdkdigital.productivebees.client;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.ModLoader;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CombTextureLoader implements PreparableReloadListener, Predicate<ResourceLocation>
{
    public static final CombTextureLoader INSTANCE = new CombTextureLoader();

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller1, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            if (ModLoader.isLoadingStateValid()) {
                onReloadSafe(resourceManager);
            }
        }, backgroundExecutor).thenCompose(preparationBarrier::wait);
    }

    private final String folder;
    private final int trim;
    private final String extension;
    protected Set<ResourceLocation> resources;

    public CombTextureLoader() {
        this.folder = "textures/item/honeycombs";
        this.trim = "textures".length() + 1;
        this.extension = ".png";
        this.resources = ImmutableSet.of();
    }

    public void onReloadSafe(ResourceManager manager) {
        int extensionLength = extension.length();
        this.resources = manager.listResources(folder, (loc) -> {
            // must have proper extension and contain valid characters
            return loc.endsWith(extension) && isPathValid(loc);
        }).stream().map((location) -> {
            String path = location.getPath();
            return new ResourceLocation(location.getNamespace(), path.substring(trim, path.length() - extensionLength));
        }).collect(Collectors.toSet());
    }

    private static boolean isPathValid(String path) {
        return path.chars().allMatch((c) -> c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == '/' || c == '.' || c == '-');
    }

    @Override
    public boolean test(ResourceLocation location) {
        return resources.contains(location);
    }

    /**
     * Clears the resource cache, saves RAM as there could be a lot of locations
     */
    public void clear() {
        resources = ImmutableSet.of();
    }

    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (InventoryMenu.BLOCK_ATLAS.equals(event.getAtlas().location())) {
            this.resources.forEach(event::addSprite);
        }
    }
}
