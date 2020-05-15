package cy.jdkdigital.productivebees.init;

import com.google.common.base.Supplier;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.*;
import cy.jdkdigital.productivebees.block.nest.*;
import cy.jdkdigital.productivebees.fluid.HoneyFluid;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ProductiveBees.MODID);

    public static final RegistryObject<Block> CENTRIFUGE = createBlock("centrifuge", () -> new Centrifuge(Block.Properties.create(Material.IRON, MaterialColor.STONE).hardnessAndResistance(2.0F).notSolid()), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> OAK_WOOD_NEST = createBlock("oak_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SPRUCE_WOOD_NEST = createBlock("spruce_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DARK_OAK_WOOD_NEST = createBlock("dark_oak_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> BIRCH_WOOD_NEST = createBlock("birch_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> JUNGLE_WOOD_NEST = createBlock("jungle_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ACACIA_WOOD_NEST = createBlock("acacia_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.from(Blocks.SCAFFOLDING).hardnessAndResistance(0.3F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DRAGON_EGG_HIVE = createBlock("dragon_egg_hive", () -> new DragonEggHive(Block.Properties.from(Blocks.DRAGON_EGG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> STONE_NEST = createBlock("stone_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.STONE).notSolid()), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.COARSE_DIRT)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SAND_NEST = createBlock("sand_nest", () -> new SandNest(Block.Properties.from(Blocks.SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GRAVEL_NEST = createBlock("gravel_nest", () -> new GravelNest(Block.Properties.from(Blocks.GRAVEL)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SUGAR_CANE_NEST = createBlock("sugar_cane_nest", () -> new SugarCaneNest(Block.Properties.from(Blocks.SUGAR_CANE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SlimyNest(Block.Properties.from(Blocks.SLIME_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new GlowstoneNest(Block.Properties.from(Blocks.GLOWSTONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new NetherQuartzNest(Block.Properties.from(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new NetherBrickNest(Block.Properties.from(Blocks.NETHER_BRICKS)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> END_NEST = createBlock("end_stone_nest", () -> new EndStoneNest(Block.Properties.from(Blocks.END_STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new ObsidianNest(Block.Properties.from(Blocks.OBSIDIAN)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<FlowingFluidBlock> HONEY = createBlock("honey",
            () -> new HoneyFluidBlock(
                    HoneyFluid.Source::new,
                    Block.Properties.create(ModFluids.MATERIAL_HONEY).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops().speedFactor(0.3F)
            ),
            ModItemGroups.PRODUCTIVE_BEES,
            false
    );
    public static final RegistryObject<Block> ADVANCED_OAK_BEEHIVE = createBlock("advanced_oak_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_OAK = createBlock("expansion_box_oak", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> ADVANCED_SPRUCE_BEEHIVE = createBlockCompat("buzzierbees", "advanced_spruce_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BIRCH_BEEHIVE = createBlockCompat("buzzierbees", "advanced_birch_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_JUNGLE_BEEHIVE = createBlockCompat("buzzierbees", "advanced_jungle_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ACACIA_BEEHIVE = createBlockCompat("buzzierbees", "advanced_acacia_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DARK_OAK_BEEHIVE = createBlockCompat("buzzierbees", "advanced_dark_oak_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_CRIMSON_BEEHIVE = createBlockCompat("buzzierbees", "advanced_crimson_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), null);
    public static final RegistryObject<Block> ADVANCED_WARPED_BEEHIVE = createBlockCompat("buzzierbees", "advanced_warped_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), null);
    public static final RegistryObject<Block> ADVANCED_SNAKE_BLOCK_BEEHIVE = createBlockCompat("buzzierbees", "advanced_snake_block_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> ADVANCED_ROSEWOOD_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_rosewood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_YUCCA_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_yucca_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_KOUSA_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_kousa_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ASPEN_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_aspen_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WILLOW_BEEHIVE = createBlockCompatBB("swampexpansion", "advanced_willow_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WISTERIA_BEEHIVE = createBlockCompatBB("bloomful", "advanced_wisteria_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BAMBOO_BEEHIVE = createBlockCompatBB("bambooblocks", "advanced_bamboo_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_MAPLE_BEEHIVE = createBlockCompatBB("autumnity", "advanced_maple_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DRIFTWOOD_BEEHIVE = createBlockCompatBB("upgrade_aquatic", "advanced_driftwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_RIVER_BEEHIVE = createBlockCompatBB("upgrade_aquatic", "advanced_river_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_POISE_BEEHIVE = createBlockCompatBB("endergetic", "advanced_poise_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_FIR_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_fir_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_DEAD_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_dead_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_PALM_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_palm_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAGIC_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_magic_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_CHERRY_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_cherry_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_UMBRAN_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_umbran_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_WILLOW_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_willow_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_REDWOOD_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_redwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_HELLBARK_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_hellbark_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAHOGANY_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_mahogany_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_JACARANDA_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_jacaranda_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> EXPANSION_BOX_SPRUCE = createBlockCompat("buzzierbees", "expansion_box_spruce", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BIRCH = createBlockCompat("buzzierbees", "expansion_box_birch", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_JUNGLE = createBlockCompat("buzzierbees", "expansion_box_jungle", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_ACACIA = createBlockCompat("buzzierbees", "expansion_box_acacia", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_DARK_OAK = createBlockCompat("buzzierbees", "expansion_box_dark_oak", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_CRIMSON = createBlockCompat("buzzierbees", "expansion_box_crimson", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), null);
    public static final RegistryObject<Block> EXPANSION_BOX_WARPED = createBlockCompat("buzzierbees", "expansion_box_warped", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), null);
    public static final RegistryObject<Block> EXPANSION_BOX_SNAKE_BLOCK = createBlockCompat("buzzierbees", "expansion_box_snake_block", () -> new ExpansionBox(Block.Properties.from(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> EXPANSION_BOX_ROSEWOOD = createBlockCompatBB("atmospheric", "expansion_box_rosewood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_YUCCA = createBlockCompatBB("atmospheric", "expansion_box_yucca", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_KOUSA = createBlockCompatBB("atmospheric", "expansion_box_kousa", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_ASPEN = createBlockCompatBB("atmospheric", "expansion_box_aspen", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WILLOW = createBlockCompatBB("swampexpansion", "expansion_box_willow", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WISTERIA = createBlockCompatBB("bloomful", "expansion_box_wisteria", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BAMBOO = createBlockCompatBB("bambooblocks", "expansion_box_bamboo", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_MAPLE = createBlockCompatBB("autumnity", "expansion_box_maple", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_DRIFTWOOD = createBlockCompatBB("upgrade_aquatic", "expansion_box_driftwood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_RIVER = createBlockCompatBB("upgrade_aquatic", "expansion_box_river", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_POISE = createBlockCompatBB("endergetic", "expansion_box_poise", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_FIR = createBlockCompatBB("biomesoplenty", "expansion_box_bop_fir", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_DEAD = createBlockCompatBB("biomesoplenty", "expansion_box_bop_dead", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_PALM = createBlockCompatBB("biomesoplenty", "expansion_box_bop_palm", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_MAGIC = createBlockCompatBB("biomesoplenty", "expansion_box_bop_magic", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_CHERRY = createBlockCompatBB("biomesoplenty", "expansion_box_bop_cherry", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_UMBRAN = createBlockCompatBB("biomesoplenty", "expansion_box_bop_umbran", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_WILLOW = createBlockCompatBB("biomesoplenty", "expansion_box_bop_willow", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_REDWOOD = createBlockCompatBB("biomesoplenty", "expansion_box_bop_redwood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_HELLBARK = createBlockCompatBB("biomesoplenty", "expansion_box_bop_hellbark", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_MAHOGANY = createBlockCompatBB("biomesoplenty", "expansion_box_bop_mahogany", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_JACARANDA = createBlockCompatBB("biomesoplenty", "expansion_box_bop_jacaranda", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static <B extends Block> RegistryObject<B> createBlockCompatBB(String mod, String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlockCompat(mod, name, supplier, ModList.get().isLoaded("buzzierbees") ? itemGroup : null);
    }

    public static <B extends Block> RegistryObject<B> createBlockCompat(String mod, String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlockCompat(mod, name, supplier, itemGroup, true);
    }

    public static <B extends Block> RegistryObject<B> createBlockCompat(String mod, String name, Supplier<? extends B> supplier, ItemGroup itemGroup, boolean createItem) {
        ItemGroup group = itemGroup != null && ModList.get().isLoaded(mod) ? itemGroup : null;
        return createBlock(name, supplier, group, createItem);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlock(name, supplier, itemGroup, true);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, ItemGroup itemGroup, boolean createItem) {
        RegistryObject<B> block = BLOCKS.register(name, supplier);
        if (createItem) {
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(itemGroup)));
        }
        return block;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRendering() {
        RenderTypeLookup.setRenderLayer(ModBlocks.SLIMY_NEST.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.SUGAR_CANE_NEST.get(), RenderType.getCutout());
    }
}
