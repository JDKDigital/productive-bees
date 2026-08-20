package cy.jdkdigital.productivebees.gametest;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.SharedConstants;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the empty NBT structure templates used as gametest playgrounds.
 *
 * <p>The vanilla {@code minecraft:empty} template is 1×1×1 — too small for a hive
 * stack or feeder+slab arrangement — so we ship a 7×7×7 variant and reference it
 * from {@link ProductiveBeesGameTests}. The contents are deliberately empty:
 * the test body places every block it needs at runtime via {@code helper.setBlock}.
 */
public class GameTestStructureProvider implements DataProvider
{
    private static final Vec3i SIZE = new Vec3i(7, 7, 7);
    private static final String NAME = "empty_7x7";

    private final PackOutput.PathProvider pathProvider;

    public GameTestStructureProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "structure");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path path = pathProvider.file(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, NAME), "nbt");
        return CompletableFuture.runAsync(() -> {
            try {
                CompoundTag tag = buildEmptyStructure(SIZE);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                HashingOutputStream hashedBytes = new HashingOutputStream(Hashing.sha1(), byteStream);
                NbtIo.writeCompressed(tag, hashedBytes);
                cache.writeIfNeeded(path, byteStream.toByteArray(), hashedBytes.hash());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write gametest structure " + path, e);
            }
        });
    }

    private static CompoundTag buildEmptyStructure(Vec3i size) {
        CompoundTag root = new CompoundTag();
        ListTag sizeTag = new ListTag();
        sizeTag.add(IntTag.valueOf(size.getX()));
        sizeTag.add(IntTag.valueOf(size.getY()));
        sizeTag.add(IntTag.valueOf(size.getZ()));
        root.put("size", sizeTag);
        root.put("blocks", new ListTag());
        root.put("palette", new ListTag());
        root.put("entities", new ListTag());
        root.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
        return root;
    }

    @Override
    public String getName() {
        return "ProductiveBees GameTest Structures";
    }
}
