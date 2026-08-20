package cy.jdkdigital.productivebees.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Quadrant;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.color.BeeTintSource;
import cy.jdkdigital.productivebees.client.render.item.JarBlockItemRenderer;
import cy.jdkdigital.productivebees.client.render.item.property.GeneAttributeProperty;
import cy.jdkdigital.productivebees.client.render.item.property.HoneyTreatVariantProperty;
import cy.jdkdigital.productivebees.client.render.item.property.NestAngle;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class BlockstateProvider extends ModelProvider
{
    public BlockstateProvider(PackOutput packOutput) {
        super(packOutput, ProductiveBees.MODID);
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        List<Holder<Item>> known = new ArrayList<>();
        ProductiveBees.ITEMS.getEntries().forEach(holder -> known.add(holder.get().builtInRegistryHolder()));
        for (var holder : ModBlocks.HIVES.values()) {
            known.add(holder.get().asItem().builtInRegistryHolder());
        }
        for (var holder : ModBlocks.EXPANSIONS.values()) {
            known.add(holder.get().asItem().builtInRegistryHolder());
        }
        return known.stream();
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        // Only the blocks this provider emits — everything else ships hand-written JSON.
        List<Holder<Block>> known = new ArrayList<>();
        ModBlocks.HIVES.values().forEach(h -> known.add(h.get().builtInRegistryHolder()));
        ModBlocks.EXPANSIONS.values().forEach(b -> known.add(b.get().builtInRegistryHolder()));
        for (DyeColor color : DyeColor.values()) {
            BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, color.getSerializedName() + "_petrified_honey"))
                    .ifPresent(b -> known.add(b.builtInRegistryHolder()));
        }
        return known.stream();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        List<String> completed = new ArrayList<>();

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    String fullName = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    if (!completed.contains(fullName)) {
                        completed.add(fullName);
                        Block hive = ModBlocks.HIVES.get("advanced_" + fullName + "_beehive").get();
                        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + fullName).get();
                        generateHiveAndBox(blockModels, hive, box, fullName, type);
                    }
                });
            }
        });

        ModBlocks.hiveStyles.forEach(style -> {
            Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
            Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();
            generateHiveAndBox(blockModels, hive, box, style + "_canvas", new HiveType(false, "", style, Items.OAK_PLANKS, null));
        });

        for (DyeColor color : DyeColor.values()) {
            generateHoneyBlock(blockModels, color);
        }

        generateTintedItems(itemModels);
    }

    /**
     * Emits the default {@code items/<id>.json} pointer for every registered Item + BlockItem
     * that isn't claimed by a specific handler below. {@code ItemModelGenerators#itemModelOutput.accept}
     * throws on duplicate registration, so we must compute the skip-set upfront rather than rely
     * on "default-then-overwrite" semantics.
     */
    private void emitDefaultItemModels(ItemModelGenerators itemModels) {
        Set<Item> skip = new HashSet<>();
        // Spawn eggs — registered via the SPAWN_EGGS list.
        for (var holder : ModItems.SPAWN_EGGS) {
            skip.add(holder.get());
        }
        // Configurable honeycomb + comb block.
        skip.add(ModItems.CONFIGURABLE_HONEYCOMB.get());
        if (ModItems.CONFIGURABLE_COMB_BLOCK != null) {
            skip.add(ModItems.CONFIGURABLE_COMB_BLOCK.get());
        }
        // Non-textured advanced beehive + expansion items (handled in the HIVELIST loop with constant tints).
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    if (!type.hasTexture() && !type.primary().isEmpty()) {
                        String fullName = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                        skip.add(ModBlocks.HIVES.get("advanced_" + fullName + "_beehive").get().asItem());
                        skip.add(ModBlocks.EXPANSIONS.get("expansion_box_" + fullName).get().asItem());
                    }
                });
            }
        });
        // Canvas hives + expansion items.
        ModBlocks.hiveStyles.forEach(style -> {
            skip.add(ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get().asItem());
            skip.add(ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get().asItem());
        });
        // Jar item (composite block + special bee renderer).
        skip.add(ModBlocks.JAR.get().asItem());
        // Petrified honey dyed variants.
        for (var blockHolder : ModBlocks.PETRIFIED_HONEY_BLOCKS) {
            skip.add(blockHolder.get().asItem());
        }
        skip.add(ModItems.BEE_CAGE.get());
        skip.add(ModItems.STURDY_BEE_CAGE.get());
        skip.add(ModBlocks.BUMBLE_BEE_NEST.get().asItem());
        // Wood nests use tintindex 1 on the opening layer; handled with explicit tint sources below.
        skip.add(ModBlocks.OAK_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.SPRUCE_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.DARK_OAK_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.BIRCH_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.JUNGLE_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.ACACIA_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.CHERRY_WOOD_NEST.get().asItem());
        skip.add(ModBlocks.MANGROVE_WOOD_NEST.get().asItem());
        skip.add(ModItems.NEST_LOCATOR.get());
        skip.add(ModItems.GENE.get());
        emitGeneItemModel(itemModels);
        skip.add(ModItems.HONEY_TREAT.get());
        emitHoneyTreatItemModel(itemModels);

        Set<Item> seen = new HashSet<>();
        for (var holder : ProductiveBees.ITEMS.getEntries()) {
            Item item = holder.get();
            if (!skip.contains(item) && seen.add(item)) {
                emitDefault(itemModels, item);
            }
        }
        for (var holder : ModBlocks.HIVES.values()) {
            Item item = holder.get().asItem();
            if (!skip.contains(item) && seen.add(item)) {
                emitDefault(itemModels, item);
            }
        }
        for (var holder : ModBlocks.EXPANSIONS.values()) {
            Item item = holder.get().asItem();
            if (!skip.contains(item) && seen.add(item)) {
                emitDefault(itemModels, item);
            }
        }
    }

    private void emitGeneItemModel(ItemModelGenerators itemModels) {
        Identifier base = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/gene");
        ItemModel.Unbaked fallback = ItemModelUtils.plainModel(base);
        ItemModel.Unbaked selectModel = ItemModelUtils.select(
                new GeneAttributeProperty(),
                fallback,
                List.of(
                        ItemModelUtils.when("productivity", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/gene/productivity"))),
                        ItemModelUtils.when("endurance", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/gene/endurance"))),
                        ItemModelUtils.when("temper", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/gene/temper"))),
                        ItemModelUtils.when("behavior", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/gene/behavior"))),
                        ItemModelUtils.when("weather_tolerance", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/gene/tolerance")))
                )
        );
        itemModels.itemModelOutput.accept(ModItems.GENE.get(), selectModel);
    }

    private void emitHoneyTreatItemModel(ItemModelGenerators itemModels) {
        Identifier base = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/honey_treat");
        ItemModel.Unbaked fallback = ItemModelUtils.plainModel(base);
        ItemModel.Unbaked selectModel = ItemModelUtils.select(
                new HoneyTreatVariantProperty(),
                fallback,
                List.of(
                        ItemModelUtils.when("type", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/honey_treat_type"))),
                        ItemModelUtils.when("genetic", ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/honey_treat_genetic")))
                )
        );
        itemModels.itemModelOutput.accept(ModItems.HONEY_TREAT.get(), selectModel);
    }

    private void emitDefault(ItemModelGenerators itemModels, Item item) {
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        itemModels.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath())));
    }

    private void generateTintedItems(ItemModelGenerators itemModels) {
        emitDefaultItemModels(itemModels);

        // Spawn eggs share the layer0/layer1 template. The configurable bee's spawn egg reads
        // colour from the stack's entity_data; non-configurable bee spawn eggs use constant tints
        // baked into the item model from the per-entity colour map populated at registration.
        ItemTintSource primary = new BeeTintSource("", "primaryColor");
        ItemTintSource secondary = new BeeTintSource("", "secondaryColor");
        ItemTintSource tertiary = new BeeTintSource("", "tertiaryColor");
        Identifier spawnEggModel = pbId("item/template_spawn_egg");
        for (var holder : ModItems.SPAWN_EGGS) {
            Item item = holder.get();
            if (holder == ModItems.CONFIGURABLE_SPAWN_EGG) {
                itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(spawnEggModel, primary, secondary));
            } else {
                int[] colors = ModEntities.SPAWN_EGG_COLORS.get(holder);
                if (colors != null) {
                    itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(spawnEggModel,
                            ItemModelUtils.constantTint(0xFF000000 | colors[0]),
                            ItemModelUtils.constantTint(0xFF000000 | colors[1])));
                } else {
                    itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(spawnEggModel, primary, secondary));
                }
            }
        }

        // Configurable honeycomb: base layer tinted by primaryColor, crystal overlay by tertiaryColor.
        itemModels.itemModelOutput.accept(ModItems.CONFIGURABLE_HONEYCOMB.get(),
                ItemModelUtils.tintedModel(pbId("item/configurable_honeycomb"), primary, tertiary));

        // Configurable comb block item: single-tint, primary only.
        if (ModItems.CONFIGURABLE_COMB_BLOCK != null) {
            itemModels.itemModelOutput.accept(ModItems.CONFIGURABLE_COMB_BLOCK.get(),
                    ItemModelUtils.tintedModel(pbId("block/comb/configurable"), primary));
        }

        // Non-textured advanced beehive + expansion items — tintindex 0 = the per-type primary
        // colour baked in at datagen time, tintindex 1 = -1 (overlay keeps native texture).
        ItemTintSource noTint = ItemModelUtils.constantTint(-1);
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    if (!type.hasTexture() && !type.primary().isEmpty()) {
                        String fullName = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                        int color = TextColor.parseColor(type.primary()).result().map(TextColor::getValue).orElse(-1);
                        ItemTintSource primaryConst = ItemModelUtils.constantTint(0xFF000000 | color);
                        Item hiveItem = ModBlocks.HIVES.get("advanced_" + fullName + "_beehive").get().asItem();
                        Item boxItem = ModBlocks.EXPANSIONS.get("expansion_box_" + fullName).get().asItem();
                        itemModels.itemModelOutput.accept(hiveItem,
                                ItemModelUtils.tintedModel(pbId("item/advanced_" + fullName + "_beehive"), primaryConst, noTint));
                        itemModels.itemModelOutput.accept(boxItem,
                                ItemModelUtils.tintedModel(pbId("item/expansion_box_" + fullName), primaryConst, noTint));
                    }
                });
            }
        });

        // Canvas hive + expansion items — tintindex 0 reads DyedItemColor from the stack
        // (CanvasBlockItem applies a dye on right-click), defaulting to white.
        ItemTintSource canvasDye = new Dye(0xFFFFFFFF);
        ModBlocks.hiveStyles.forEach(style -> {
            String hiveName = "advanced_" + style + "_canvas_beehive";
            String boxName = "expansion_box_" + style + "_canvas";
            Item hiveItem = ModBlocks.HIVES.get(hiveName).get().asItem();
            Item boxItem = ModBlocks.EXPANSIONS.get(boxName).get().asItem();
            itemModels.itemModelOutput.accept(hiveItem,
                    ItemModelUtils.tintedModel(pbId("item/" + hiveName), canvasDye, noTint));
            itemModels.itemModelOutput.accept(boxItem,
                    ItemModelUtils.tintedModel(pbId("item/" + boxName), canvasDye, noTint));
        });

        // Wood nest items — tintindex 0 = -1 (base wood keeps native colour), tintindex 1 = the
        // per-species dark opening colour baked in.
        for (var entry : Map.of(
                ModBlocks.OAK_WOOD_NEST, "#382b18",
                ModBlocks.SPRUCE_WOOD_NEST, "#2e1608",
                ModBlocks.DARK_OAK_WOOD_NEST, "#292011",
                ModBlocks.BIRCH_WOOD_NEST, "#36342a",
                ModBlocks.JUNGLE_WOOD_NEST, "#3e3013",
                ModBlocks.ACACIA_WOOD_NEST, "#504b40",
                ModBlocks.CHERRY_WOOD_NEST, "#271620",
                ModBlocks.MANGROVE_WOOD_NEST, "#443522"
        ).entrySet()) {
            int openingColor = TextColor.parseColor(entry.getValue()).result().map(TextColor::getValue).orElse(0);
            ItemTintSource opening = ItemModelUtils.constantTint(0xFF000000 | openingColor);
            Item nestItem = entry.getKey().get().asItem();
            Identifier modelId = pbId("item/" + BuiltInRegistries.ITEM.getKey(nestItem).getPath());
            itemModels.itemModelOutput.accept(nestItem, ItemModelUtils.tintedModel(modelId, noTint, opening));
        }

        Identifier jarModel = pbId("block/jar");
        itemModels.itemModelOutput.accept(ModBlocks.JAR.get().asItem(),
                ItemModelUtils.composite(
                        ItemModelUtils.plainModel(jarModel),
                        ItemModelUtils.specialModel(jarModel, new JarBlockItemRenderer.Unbaked())));

        // Petrified honey: each dyed block's model lives at block/petrified_honey/<color>
        // (subfolder, see generateHoneyBlock). Without an explicit override here, vanilla's
        // auto-emitted items/<color>_petrified_honey.json points at block/<color>_petrified_honey
        // — a flat path that doesn't exist, producing 16 "Missing block model" warnings.
        for (var blockHolder : ModBlocks.PETRIFIED_HONEY_BLOCKS) {
            Block block = blockHolder.get();
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
            String color = blockId.getPath().replace("_petrified_honey", "");
            Identifier modelLocation = pbId("block/petrified_honey/" + color);
            itemModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(modelLocation));
        }

        // Cage swaps to _filled when CUSTOM_DATA is present (BeeCage.captureEntity sets it).
        itemModels.itemModelOutput.accept(ModItems.BEE_CAGE.get(),
                ItemModelUtils.conditional(
                        ItemModelUtils.hasComponent(DataComponents.CUSTOM_DATA),
                        ItemModelUtils.plainModel(pbId("item/bee_cage_filled")),
                        ItemModelUtils.plainModel(pbId("item/bee_cage"))));
        itemModels.itemModelOutput.accept(ModItems.STURDY_BEE_CAGE.get(),
                ItemModelUtils.conditional(
                        ItemModelUtils.hasComponent(DataComponents.CUSTOM_DATA),
                        ItemModelUtils.plainModel(pbId("item/sturdy_bee_cage_filled")),
                        ItemModelUtils.plainModel(pbId("item/sturdy_bee_cage"))));

        itemModels.itemModelOutput.accept(ModBlocks.BUMBLE_BEE_NEST.get().asItem(),
                ItemModelUtils.tintedModel(pbId("item/bumble_bee_nest"), new GrassColorSource()));

        List<RangeSelectItemModel.Entry> nestLocatorEntries = new ArrayList<>();
        for (int k = 0; k < 32; k++) {
            String frame = String.format("%02d", (k + 17) % 32);
            nestLocatorEntries.add(ItemModelUtils.override(
                    ItemModelUtils.plainModel(pbId("item/nest_locator/" + frame)),
                    k + 0.5F));
        }
        itemModels.itemModelOutput.accept(ModItems.NEST_LOCATOR.get(),
                ItemModelUtils.rangeSelect(
                        new NestAngle(),
                        32.0F,
                        ItemModelUtils.plainModel(pbId("item/nest_locator/16")),
                        nestLocatorEntries.toArray(RangeSelectItemModel.Entry[]::new)));
    }

    private void generateHiveAndBox(BlockModelGenerators blockModels, Block hive, Block box, String name, HiveType type) {
        String modId = BuiltInRegistries.BLOCK.getKey(hive).getNamespace();
        ModelTemplate hiveTemplate = hiveModelTemplate();
        ModelTemplate boxTemplate = expansionModelTemplate();

        Identifier hiveBase = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/hives/advanced_" + name + "_beehive");
        Identifier boxBase = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/expansion_boxes/expansion_box_" + name);

        Identifier single = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.NONE, false, hiveBase);
        Identifier up = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.UP, false, hiveBase.withSuffix("_up"));
        Identifier down = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.DOWN, false, hiveBase.withSuffix("_down"));
        Identifier left = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.LEFT, false, hiveBase.withSuffix("_left"));
        Identifier right = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.RIGHT, false, hiveBase.withSuffix("_right"));
        Identifier back = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.BACK, false, hiveBase.withSuffix("_back"));
        Identifier singleHoney = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.NONE, true, hiveBase.withSuffix("_honey"));
        Identifier upHoney = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.UP, true, hiveBase.withSuffix("_up_honey"));
        Identifier downHoney = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.DOWN, true, hiveBase.withSuffix("_down_honey"));
        Identifier leftHoney = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.LEFT, true, hiveBase.withSuffix("_left_honey"));
        Identifier rightHoney = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.RIGHT, true, hiveBase.withSuffix("_right_honey"));
        Identifier backHoney = hiveModel(blockModels, hive, hiveTemplate, name, type, VerticalHive.BACK, true, hiveBase.withSuffix("_back_honey"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(hive)
                        .with(PropertyDispatch.initial(AdvancedBeehive.EXPANDED, BlockStateProperties.LEVEL_HONEY).generate((expanded, level) -> {
                            boolean honey = level >= 5;
                            return switch (expanded) {
                                case NONE -> plainVariant(honey ? singleHoney : single);
                                case UP -> plainVariant(honey ? upHoney : up);
                                case DOWN -> plainVariant(honey ? downHoney : down);
                                case LEFT -> plainVariant(honey ? leftHoney : left);
                                case RIGHT -> plainVariant(honey ? rightHoney : right);
                                case BACK -> plainVariant(honey ? backHoney : back);
                            };
                        }))
                        .with(PropertyDispatch.modify(BeehiveBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270))));

        // Hive item model parents the small/un-honeyed variant.
        Identifier hiveItemParent = type.hasTexture()
                ? hiveBase
                : Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/small");
        itemTemplate(hiveItemParent).create(Identifier.fromNamespaceAndPath(modId, "item/advanced_" + name + "_beehive"), new TextureMapping(), blockModels.modelOutput);

        // Expansion box
        Identifier boxSingle = expansionModel(blockModels, box, boxTemplate, name, type, VerticalHive.NONE, boxBase);
        Identifier boxUp = expansionModel(blockModels, box, boxTemplate, name, type, VerticalHive.UP, boxBase.withSuffix("_up"));
        Identifier boxDown = expansionModel(blockModels, box, boxTemplate, name, type, VerticalHive.DOWN, boxBase.withSuffix("_down"));
        Identifier boxLeft = expansionModel(blockModels, box, boxTemplate, name, type, VerticalHive.LEFT, boxBase.withSuffix("_left"));
        Identifier boxRight = expansionModel(blockModels, box, boxTemplate, name, type, VerticalHive.RIGHT, boxBase.withSuffix("_right"));
        Identifier boxBack = expansionModel(blockModels, box, boxTemplate, name, type, VerticalHive.BACK, boxBase.withSuffix("_back"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(box)
                        .with(PropertyDispatch.initial(AdvancedBeehive.EXPANDED).generate(expanded -> switch (expanded) {
                            case NONE -> plainVariant(boxSingle);
                            case UP -> plainVariant(boxUp);
                            case DOWN -> plainVariant(boxDown);
                            case LEFT -> plainVariant(boxLeft);
                            case RIGHT -> plainVariant(boxRight);
                            case BACK -> plainVariant(boxBack);
                        }))
                        .with(PropertyDispatch.modify(BeehiveBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270))));

        Identifier boxItemParent = type.hasTexture()
                ? boxBase
                : Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/small");
        itemTemplate(boxItemParent).create(Identifier.fromNamespaceAndPath(modId, "item/expansion_box_" + name), new TextureMapping(), blockModels.modelOutput);
    }

    private Identifier hiveModel(BlockModelGenerators blockModels, Block hive, ModelTemplate template, String name, HiveType type, VerticalHive expand, boolean honey, Identifier modelLocation) {
        if (!type.hasTexture()) {
            String suffix = switch (expand) {
                case NONE -> "small";
                default -> expand.getSerializedName();
            };
            return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/" + suffix + (honey ? "_honey" : ""));
        }
        return template.create(modelLocation, hiveTextureMap(expand, name, honey), blockModels.modelOutput);
    }

    private Identifier expansionModel(BlockModelGenerators blockModels, Block box, ModelTemplate template, String name, HiveType type, VerticalHive expand, Identifier modelLocation) {
        if (!type.hasTexture()) {
            String suffix = switch (expand) {
                case NONE -> "small";
                default -> expand.getSerializedName();
            };
            return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/" + suffix);
        }
        return template.create(modelLocation, expansionTextureMap(expand, name), blockModels.modelOutput);
    }

    private void generateHoneyBlock(BlockModelGenerators blockModels, DyeColor color) {
        Identifier blockId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, color.getSerializedName() + "_petrified_honey");
        Block honey = BuiltInRegistries.BLOCK.getOptional(blockId).orElse(null);
        if (honey == null) {
            return;
        }
        Identifier modelLocation = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/petrified_honey/" + color.getSerializedName());
        // Honey-block style template (parents minecraft:block/honey_block) with translucent render type.
        ModelTemplate template = honeyBlockModelTemplate();
        template.create(modelLocation, honeyBlockTextureMap(color), (id, instance) ->
                blockModels.modelOutput.accept(id, () -> {
                    JsonElement json = instance.get();
                    if (json instanceof JsonObject obj) {
                        obj.addProperty("render_type", "translucent");
                    }
                    return json;
                }));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(honey, plainVariant(modelLocation)));

        itemTemplate(modelLocation).create(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "item/" + blockId.getPath()), new TextureMapping(), blockModels.modelOutput);
    }

    // === Texture maps ===

    private static TextureMapping hiveTextureMap(VerticalHive expand, String type, boolean honey) {
        Material front = pbTex("block/advanced_beehive/" + type + "_beehive_front" + (honey ? "_honey" : ""));
        Material back = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material right = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material left = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material top = pbTex("block/advanced_beehive/" + type + "_beehive_end");
        Material bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_front_" + expand.getSerializedName() + (honey ? "_honey" : ""));
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
            }
            case LEFT, RIGHT -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_front_" + expand.opposite() + (honey ? "_honey" : ""));
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_" + expand.getSerializedName());
                bottom = top;
            }
            case BACK -> {
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_left");
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_right");
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_front");
                bottom = pbTex("block/advanced_beehive/" + type + "_beehive_end_back");
            }
            default -> {}
        }
        return new TextureMapping()
                .put(TextureSlot.EAST, right)
                .put(TextureSlot.WEST, left)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.BACK, back)
                .copySlot(TextureSlot.EAST, TextureSlot.PARTICLE);
    }

    private static TextureMapping expansionTextureMap(VerticalHive expand, String type) {
        Material front = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material back = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material left = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material right = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material top = pbTex("block/advanced_beehive/" + type + "_beehive_end");
        Material bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
            }
            case LEFT, RIGHT -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_" + expand.opposite());
                bottom = top;
            }
            case BACK -> {
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_left");
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_right");
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_back");
                bottom = pbTex("block/advanced_beehive/" + type + "_beehive_end_front");
            }
            default -> {}
        }
        return new TextureMapping()
                .put(TextureSlot.EAST, right)
                .put(TextureSlot.WEST, left)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.BACK, back)
                .copySlot(TextureSlot.EAST, TextureSlot.PARTICLE);
    }

    private static TextureMapping honeyBlockTextureMap(DyeColor color) {
        return new TextureMapping()
                .put(TextureSlot.UP, pbTex("block/petrified_honey/" + color.getSerializedName() + "_top"))
                .put(TextureSlot.DOWN, pbTex("block/petrified_honey/" + color.getSerializedName() + "_bottom"))
                .put(TextureSlot.SIDE, pbTex("block/petrified_honey/" + color.getSerializedName() + "_side"))
                .copySlot(TextureSlot.UP, TextureSlot.PARTICLE);
    }

    // === Templates ===

    private static ModelTemplate hiveModelTemplate() {
        return new ModelTemplate(Optional.of(pbId("block/advanced_beehive_template")), Optional.empty(),
                TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    private static ModelTemplate expansionModelTemplate() {
        return new ModelTemplate(Optional.of(pbId("block/expansion_box_template")), Optional.empty(),
                TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    private static ModelTemplate honeyBlockModelTemplate() {
        return new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("block/honey_block")), Optional.empty(),
                TextureSlot.DOWN, TextureSlot.UP, TextureSlot.SIDE, TextureSlot.PARTICLE);
    }

    private static ModelTemplate itemTemplate(Identifier parent) {
        return new ModelTemplate(Optional.of(parent), Optional.empty());
    }

    // === Helpers ===

    private static Identifier pbId(String path) {
        return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, path);
    }

    private static Material pbTex(String path) {
        return new Material(pbId(path));
    }

    private static MultiVariant plainVariant(Identifier modelLocation) {
        return new MultiVariant(WeightedList.of(new Variant(modelLocation)));
    }
}
