package cy.jdkdigital.productivebees.init;

import com.google.common.collect.ImmutableSet;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModPointOfInterestTypes
{
    private static Method blockStatesInjector;

    public static final DeferredRegister<PointOfInterestType> POINT_OF_INTEREST_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, ProductiveBees.MODID);

    public static final RegistryObject<PointOfInterestType> SOLITARY_HIVE = register("solitary_hive", ModBlocks.BAMBOO_HIVE, 1);
    public static final RegistryObject<PointOfInterestType> SOLITARY_NEST = register("solitary_nest", () -> {
        List<RegistryObject<Block>> blocks = new ArrayList<>();
        blocks.add(ModBlocks.STONE_NEST);
        blocks.add(ModBlocks.SAND_NEST);
        blocks.add(ModBlocks.SNOW_NEST);
        blocks.add(ModBlocks.COARSE_DIRT_NEST);
        blocks.add(ModBlocks.GRAVEL_NEST);
        blocks.add(ModBlocks.SLIMY_NEST);
        blocks.add(ModBlocks.NETHER_BRICK_NEST);
        blocks.add(ModBlocks.NETHER_QUARTZ_NEST);
        blocks.add(ModBlocks.GLOWSTONE_NEST);
        blocks.add(ModBlocks.SUGAR_CANE_NEST);
        blocks.add(ModBlocks.END_NEST);
        blocks.add(ModBlocks.OAK_WOOD_NEST);
        blocks.add(ModBlocks.BIRCH_WOOD_NEST);
        blocks.add(ModBlocks.SPRUCE_WOOD_NEST);
        blocks.add(ModBlocks.DARK_OAK_WOOD_NEST);
        blocks.add(ModBlocks.ACACIA_WOOD_NEST);
        return blocks;
    }, 1);

    public static final RegistryObject<PointOfInterestType> DRACONIC_NEST = register("draconic_nest", () -> {
        List<RegistryObject<Block>> blocks = new ArrayList<>();
        blocks.add(ModBlocks.DRAGON_EGG_HIVE);
        blocks.add(ModBlocks.OBSIDIAN_PILLAR_NEST);
        return blocks;
    }, 1);

    private static RegistryObject<PointOfInterestType> register(String name, RegistryObject<Block> block, int maxFreeTickets) {
        List<RegistryObject<Block>> blocks = new ArrayList<>();
        blocks.add(block);
        return register(name, blocks, maxFreeTickets);
    }

    private static RegistryObject<PointOfInterestType> register(String name, Supplier<List<RegistryObject<Block>>> supplier, int maxFreeTickets) {
        return register(name, supplier.get(), maxFreeTickets);
    }

    private static RegistryObject<PointOfInterestType> register(String name, List<RegistryObject<Block>> blocks, int maxFreeTickets) {
        return register(name, () -> {
            Set<BlockState> blockStates = new HashSet<>();
            for (RegistryObject<Block> block : blocks) {
                blockStates.addAll(PointOfInterestType.getAllStates(block.get()));
            }
            return new PointOfInterestType(name, blockStates, maxFreeTickets, 1);
        });
    }

    private static RegistryObject<PointOfInterestType> register(String name, Supplier<PointOfInterestType> supplier) {
        return POINT_OF_INTEREST_TYPES.register(name, supplier);
    }

    public static void fixPOITypeBlockStates(PointOfInterestType poiType) {
        try {
            blockStatesInjector.invoke(null, poiType);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static {
        blockStatesInjector = ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "func_221052_a", PointOfInterestType.class);
    }
}
