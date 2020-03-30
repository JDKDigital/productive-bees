package cy.jdkdigital.productivebees.init;

import com.google.common.base.Supplier;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.block.BambooHive;
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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ProductiveBees.MODID);

	public static final RegistryObject<Block> ADVANCED_BEEHIVE = createBlock("advanced_beehive", () -> new AdvancedBeehive(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)), ModItemGroups.PRODUCTIVE_BEES);
	public static final RegistryObject<Block> EXPANSION_BOX = createBlock("expansion_box", () -> new ExpansionBox(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.create(Material.BAMBOO).hardnessAndResistance(0.3F).sound(SoundType.BAMBOO)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> STONE_NEST = createBlock("stone_nest", () -> new StoneNest(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.7F, 6.0F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new CoarseDirtNest(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.7F, 2.0F).sound(SoundType.GROUND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SAND_NEST = createBlock("sand_nest", () -> new SandNest(Block.Properties.create(Material.SAND).hardnessAndResistance(0.7F, 2.0F).sound(SoundType.SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SlimyNest(Block.Properties.from(Blocks.SLIME_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new GlowstoneNest(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.5F, 2.0F).sound(SoundType.GLASS).lightValue(15)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new NetherQuartzNest(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.2F, 4.0F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new NetherBrickNest(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.2F, 4.0F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> END_NEST = createBlock("end_stone_nest", () -> new EndStoneNest(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(3.2F, 9.0F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new ObsidianNest(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(50.0F, 1200.0F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<FlowingFluidBlock> HONEY = createBlock("honey",
            () -> new FlowingFluidBlock(
                HoneyFluid.Source::new,
                Block.Properties.create(ModFluids.MATERIAL_HONEY).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()
            ),
            ModItemGroups.PRODUCTIVE_BEES,
            false
    );

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
