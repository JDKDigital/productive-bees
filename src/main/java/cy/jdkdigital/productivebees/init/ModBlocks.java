package cy.jdkdigital.productivebees.init;

import com.google.common.base.Supplier;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.block.BambooHive;
import cy.jdkdigital.productivebees.block.DragonEggHive;
import cy.jdkdigital.productivebees.block.ExpansionBox;
import cy.jdkdigital.productivebees.block.nest.*;
import cy.jdkdigital.productivebees.fluid.HoneyFluid;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
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
public final class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ProductiveBees.MODID);

	public static final RegistryObject<Block> ADVANCED_OAK_BEEHIVE = createBlock("advanced_oak_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
	public static final RegistryObject<Block> EXPANSION_BOX = createBlock("expansion_box", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.from(Blocks.SCAFFOLDING).hardnessAndResistance(0.3F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DRAGON_EGG_HIVE = createBlock("dragon_egg_hive", () -> new DragonEggHive(Block.Properties.from(Blocks.DRAGON_EGG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> STONE_NEST = createBlock("stone_nest", () -> new StoneNest(Block.Properties.from(Blocks.STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new CoarseDirtNest(Block.Properties.from(Blocks.COARSE_DIRT)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SAND_NEST = createBlock("sand_nest", () -> new SandNest(Block.Properties.from(Blocks.SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SlimyNest(Block.Properties.from(Blocks.SLIME_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new GlowstoneNest(Block.Properties.from(Blocks.GLOWSTONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new NetherQuartzNest(Block.Properties.from(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new NetherBrickNest(Block.Properties.from(Blocks.NETHER_BRICKS)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> END_NEST = createBlock("end_stone_nest", () -> new EndStoneNest(Block.Properties.from(Blocks.END_STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new ObsidianNest(Block.Properties.from(Blocks.OBSIDIAN)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<FlowingFluidBlock> HONEY = createBlock("honey",
            () -> new FlowingFluidBlock(
                HoneyFluid.Source::new,
                Block.Properties.create(ModFluids.MATERIAL_HONEY).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()
            ),
            ModItemGroups.PRODUCTIVE_BEES,
            false
    );

    public static final RegistryObject<Block> ADVANCED_SPRUCE_BEEHIVE       = createBlockCompat("buzzierbees", "advanced_spruce_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BIRCH_BEEHIVE        = createBlockCompat("buzzierbees", "advanced_birch_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_JUNGLE_BEEHIVE       = createBlockCompat("buzzierbees", "advanced_jungle_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ACACIA_BEEHIVE       = createBlockCompat("buzzierbees", "advanced_acacia_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DARK_OAK_BEEHIVE     = createBlockCompat("buzzierbees", "advanced_dark_oak_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_CRIMSON_BEEHIVE      = createBlockCompat("buzzierbees", "advanced_crimson_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), null);
    public static final RegistryObject<Block> ADVANCED_WARPED_BEEHIVE       = createBlockCompat("buzzierbees", "advanced_warped_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), null);
    public static final RegistryObject<Block> ADVANCED_SNAKE_BLOCK_BEEHIVE  = createBlockCompat("buzzierbees", "advanced_snake_block_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> ADVANCED_ROSEWOOD_BEEHIVE     = createBlockCompatBB("atmospheric", "advanced_rosewood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_YUCCA_BEEHIVE        = createBlockCompatBB("atmospheric", "advanced_yucca_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_KOUSA_BEEHIVE        = createBlockCompatBB("atmospheric", "advanced_kousa_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ASPEN_BEEHIVE        = createBlockCompatBB("atmospheric", "advanced_aspen_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WILLOW_BEEHIVE       = createBlockCompatBB("swampexpansion", "advanced_willow_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WISTERIA_BEEHIVE     = createBlockCompatBB("bloomful", "advanced_wisteria_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BAMBOO_BEEHIVE       = createBlockCompatBB("bambooblocks", "advanced_bamboo_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_MAPLE_BEEHIVE        = createBlockCompatBB("autumnity", "advanced_maple_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DRIFTWOOD_BEEHIVE    = createBlockCompatBB("upgrade_aquatic", "advanced_driftwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_RIVER_BEEHIVE        = createBlockCompatBB("upgrade_aquatic", "advanced_river_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_POISE_BEEHIVE        = createBlockCompatBB("endergetic", "advanced_poise_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_FIR_BEEHIVE      = createBlockCompatBB("biomesoplenty", "advanced_bop_fir_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_DEAD_BEEHIVE     = createBlockCompatBB("biomesoplenty", "advanced_bop_dead_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_PALM_BEEHIVE     = createBlockCompatBB("biomesoplenty", "advanced_bop_palm_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAGIC_BEEHIVE    = createBlockCompatBB("biomesoplenty", "advanced_bop_magic_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_CHERRY_BEEHIVE   = createBlockCompatBB("biomesoplenty", "advanced_bop_cherry_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_UMBRAN_BEEHIVE   = createBlockCompatBB("biomesoplenty", "advanced_bop_umbran_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_WILLOW_BEEHIVE   = createBlockCompatBB("biomesoplenty", "advanced_bop_willow_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_REDWOOD_BEEHIVE  = createBlockCompatBB("biomesoplenty", "advanced_bop_redwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_HELLBARK_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_hellbark_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAHOGANY_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_mahogany_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_JACARANDA_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_jacaranda_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static <B extends Block> RegistryObject<B> createBlockCompatBB(String mod, String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlockCompat(mod, name, supplier, ModList.get().isLoaded("buzzierbees") ? itemGroup : null);
    }
    public static <B extends Block> RegistryObject<B> createBlockCompat(String mod, String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlockCompat(mod, name, supplier, itemGroup, true);
    }
    public static <B extends Block> RegistryObject<B> createBlockCompat(String mod, String name, Supplier<? extends B> supplier, ItemGroup itemGroup, boolean createItem) {
        ItemGroup group = itemGroup != null && ModList.get().isLoaded(mod) ? itemGroup : null;
        RegistryObject<B> block = BLOCKS.register(name, supplier);
        if (createItem) {
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group)));
        }
        return block;
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
    }
}
