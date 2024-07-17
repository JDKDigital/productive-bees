package cy.jdkdigital.productivebees.datagen;

import com.google.gson.JsonElement;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockstateProvider implements DataProvider
{
    protected final PackOutput packOutput;

    protected final Map<ResourceLocation, BlockStateGenerator> blockstates = new HashMap<>();
    protected final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
    protected final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = models::put;

    public BlockstateProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        try {
            registerStatesAndModels();
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error registering states and models", e);
        }

        PackOutput.PathProvider blockstatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        PackOutput.PathProvider modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        List<CompletableFuture<?>> output = new ArrayList<>();

        blockstates.forEach((id, stateGenerator) -> {
            Path path = blockstatePathProvider.json(id);
            output.add(DataProvider.saveStable(cache, stateGenerator.get(), path));
        });

        for (Map.Entry<ResourceLocation, Supplier<JsonElement>> e : models.entrySet()) {
            ResourceLocation modelId = e.getKey();
            Path path = modelPathProvider.json(modelId);
            output.add(DataProvider.saveStable(cache, e.getValue().get(), path));
        }
        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Productive Bees Blockstate and Model generator";
    }

    protected void registerStatesAndModels() {
        List<String> completedTypes = new ArrayList<>();

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    if (!completedTypes.contains(name)) {
                        completedTypes.add(name);
                        Block hive = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "advanced_" + name + "_beehive"));
                        Block box = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "expansion_box_" + name));
                        generateModels(hive, box, name, type, blockstates, modelOutput);
                    }
                });
            }
        });
        ModBlocks.hiveStyles.forEach(style ->  {
            Block hive = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "advanced_" + style + "_canvas_beehive"));
            Block box = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "expansion_box_" + style + "_canvas"));
            generateModels(hive, box, style + "_canvas", new HiveType(false, "", style, Items.OAK_PLANKS, null), blockstates, modelOutput);
        });
        for (DyeColor color: DyeColor.values()) {
            generateHoneyModels(color);
        }
    }


    public static void generateModels(Block hive, Block box, String name, HiveType type, Map<ResourceLocation, BlockStateGenerator> blockstates, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        String modId = BuiltInRegistries.BLOCK.getKey(hive).getNamespace();
        var hiveTemplate = getHiveModelTemplate();
        var expansionTemplate = getExpansionModelTemplate();

        // Hive
        var textureMap = type.hasTexture() ? getHiveTextureMap(VerticalHive.NONE, name, false) : new TextureMapping();
        var textureMapUp = type.hasTexture() ? getHiveTextureMap(VerticalHive.UP, name, false) : new TextureMapping();
        var textureMapDown = type.hasTexture() ? getHiveTextureMap(VerticalHive.DOWN, name, false) : new TextureMapping();
        var textureMapLeft = type.hasTexture() ? getHiveTextureMap(VerticalHive.LEFT, name, false) : new TextureMapping();
        var textureMapRight = type.hasTexture() ? getHiveTextureMap(VerticalHive.RIGHT, name, false) : new TextureMapping();
        var textureMapBack = type.hasTexture() ? getHiveTextureMap(VerticalHive.BACK, name, false) : new TextureMapping();
        var textureMapHoney = type.hasTexture() ? getHiveTextureMap(VerticalHive.NONE, name, true) : new TextureMapping();
        var textureMapUpHoney = type.hasTexture() ? getHiveTextureMap(VerticalHive.UP, name, true) : new TextureMapping();
        var textureMapDownHoney = type.hasTexture() ? getHiveTextureMap(VerticalHive.DOWN, name, true) : new TextureMapping();
        var textureMapLeftHoney = type.hasTexture() ? getHiveTextureMap(VerticalHive.LEFT, name, true) : new TextureMapping();
        var textureMapRightHoney = type.hasTexture() ? getHiveTextureMap(VerticalHive.RIGHT, name, true) : new TextureMapping();
        var textureMapBackHoney = type.hasTexture() ? getHiveTextureMap(VerticalHive.BACK, name, true) : new TextureMapping();

        String hiveModelBase = "block/hives/advanced_" + name + "_beehive";
        var singleModel = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase), textureMap, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/small");
        var upModel = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_up"), textureMapUp, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/up");
        var downModel = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_down"), textureMapDown, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/down");
        var leftModel = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_left"), textureMapLeft, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/left");
        var rightModel = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_right"), textureMapRight, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/right");
        var backModel = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_back"), textureMapBack, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/back");
        var singleModelHoney = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_honey"), textureMapHoney, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/small_honey");
        var upModelHoney = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_up_honey"), textureMapUpHoney, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/up_honey");
        var downModelHoney = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_down_honey"), textureMapDownHoney, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/down_honey");
        var leftModelHoney = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_left_honey"), textureMapLeftHoney, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/left_honey");
        var rightModelHoney = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_right_honey"), textureMapRightHoney, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/right_honey");
        var backModelHoney = type.hasTexture() ? hiveTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, hiveModelBase + "_back_honey"), textureMapBackHoney, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/back_honey");

        blockstates.put(ResourceLocation.fromNamespaceAndPath(modId, "advanced_" + name + "_beehive"),
                MultiVariantGenerator.multiVariant(hive)
                        .with(
                                PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant())
                        )
                        .with(
                                PropertyDispatch.properties(AdvancedBeehive.EXPANDED, BlockStateProperties.LEVEL_HONEY).generate((expanded, level) -> switch (expanded) {
                                    case NONE -> Variant.variant().with(VariantProperties.MODEL, level.compareTo(5) >= 0 ? singleModelHoney : singleModel);
                                    case UP -> Variant.variant().with(VariantProperties.MODEL, level.compareTo(5) >= 0 ? upModelHoney : upModel);
                                    case DOWN -> Variant.variant().with(VariantProperties.MODEL, level.compareTo(5) >= 0 ? downModelHoney : downModel);
                                    case LEFT -> Variant.variant().with(VariantProperties.MODEL, level.compareTo(5) >= 0 ? leftModelHoney : leftModel);
                                    case RIGHT -> Variant.variant().with(VariantProperties.MODEL, level.compareTo(5) >= 0 ? rightModelHoney : rightModel);
                                    case BACK -> Variant.variant().with(VariantProperties.MODEL, level.compareTo(5) >= 0 ? backModelHoney : backModel);
                                })
                        )
        );
        // Item model
        getItemTemplate(type.hasTexture() ? ResourceLocation.fromNamespaceAndPath(modId, hiveModelBase) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/small")).create(ResourceLocation.fromNamespaceAndPath(modId, "item/advanced_" + name + "_beehive"), new TextureMapping(), modelOutput);

        // Expansion box
        var expansionTextureMap = type.hasTexture() ? getExpansionTextureMap(VerticalHive.NONE, name) : new TextureMapping();
        var expansionTextureMapUp = type.hasTexture() ? getExpansionTextureMap(VerticalHive.UP, name) : new TextureMapping();
        var expansionTextureMapDown = type.hasTexture() ? getExpansionTextureMap(VerticalHive.DOWN, name) : new TextureMapping();
        var expansionTextureMapLeft = type.hasTexture() ? getExpansionTextureMap(VerticalHive.LEFT, name) : new TextureMapping();
        var expansionTextureMapRight = type.hasTexture() ? getExpansionTextureMap(VerticalHive.RIGHT, name) : new TextureMapping();
        var expansionTextureMapBack = type.hasTexture() ? getExpansionTextureMap(VerticalHive.BACK, name) : new TextureMapping();

        String boxModelBase = "block/expansion_boxes/expansion_box_" + name;
        var expansionSingleModel = type.hasTexture() ? expansionTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, boxModelBase), expansionTextureMap, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/small");
        var expansionUpModel = type.hasTexture() ? expansionTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, boxModelBase + "_up"), expansionTextureMapUp, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/up");;
        var expansionDownModel = type.hasTexture() ? expansionTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, boxModelBase + "_down"), expansionTextureMapDown, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/down");;
        var expansionLeftModel = type.hasTexture() ? expansionTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, boxModelBase + "_left"), expansionTextureMapLeft, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/left");;
        var expansionRightModel = type.hasTexture() ? expansionTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, boxModelBase + "_right"), expansionTextureMapRight, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/right");;
        var expansionBackModel = type.hasTexture() ? expansionTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, boxModelBase + "_back"), expansionTextureMapBack, modelOutput) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/back");;

        blockstates.put(ResourceLocation.fromNamespaceAndPath(modId, "expansion_box_" + name),
                MultiVariantGenerator.multiVariant(box)
                        .with(
                                PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant())
                        )
                        .with(
                                PropertyDispatch.property(AdvancedBeehive.EXPANDED).generate((expanded) -> switch (expanded) {
                                    case NONE -> Variant.variant().with(VariantProperties.MODEL, expansionSingleModel);
                                    case UP -> Variant.variant().with(VariantProperties.MODEL, expansionUpModel);
                                    case DOWN -> Variant.variant().with(VariantProperties.MODEL, expansionDownModel);
                                    case LEFT -> Variant.variant().with(VariantProperties.MODEL, expansionLeftModel);
                                    case RIGHT -> Variant.variant().with(VariantProperties.MODEL, expansionRightModel);
                                    case BACK -> Variant.variant().with(VariantProperties.MODEL, expansionBackModel);
                                })
                        )
        );

        // Item model
        getItemTemplate(type.hasTexture() ? ResourceLocation.fromNamespaceAndPath(modId, boxModelBase) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/small")).create(ResourceLocation.fromNamespaceAndPath(modId, "item/expansion_box_" + name), new TextureMapping(), modelOutput);
    }

    private void generateHoneyModels(DyeColor color) {
        var id = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, color.getSerializedName() + "_petrified_honey");
        var modelLocation = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/petrified_honey/" + color.getSerializedName());
        Block honey = BuiltInRegistries.BLOCK.get(id);

        var modelTemplate = getHoneyBlockModelTemplate();
        modelTemplate.create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/petrified_honey/" + color.getSerializedName()), getHoneyBlockTextureMap(color), this.modelOutput, (rLoc, textureMap) -> {
            var json = modelTemplate.createBaseTemplate(rLoc, textureMap);
            json.addProperty("render_type", "translucent");
            return json;
        });

        this.blockstates.put(id, createSimpleBlock(honey, modelLocation));

        getItemTemplate(modelLocation).create(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "item/" + id.getPath()), new TextureMapping(), this.modelOutput);
    }

    public static TextureMapping getHiveTextureMap(VerticalHive expand, String type, boolean hasHoney) {
        var front = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_front" + (hasHoney ? "_honey" : ""));
        var back = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var right = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var left = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var top = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end");
        var bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_front_" + expand + (hasHoney ? "_honey" : ""));
                right = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                left = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                back = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
            }
            case LEFT, RIGHT -> {
                front = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_front_" + expand.opposite()+ (hasHoney ? "_honey" : ""));
                back = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                top = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_" + expand);
                bottom = top;
            }
            case BACK -> {
                right = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_left");
                left = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_right");
                top = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_front");
                bottom = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_back");
            }
        }

        return (new TextureMapping())
                .put(TextureSlot.EAST, right)
                .put(TextureSlot.WEST, left)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.BACK, back)
                .copySlot(TextureSlot.EAST, TextureSlot.PARTICLE);
    }

    public static TextureMapping getExpansionTextureMap(VerticalHive expand, String type) {
        var front = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var back = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var left = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var right = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var top = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end");
        var bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                left = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                right = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                back = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
            }
            case LEFT, RIGHT -> {
                front = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                back = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                top = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_" + expand.opposite());
                bottom = top;
            }
            case BACK -> {
                left = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_left");
                right = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_right");
                top = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_back");
                bottom = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_front");
            }
        }

        return (new TextureMapping())
                .put(TextureSlot.EAST, right)
                .put(TextureSlot.WEST, left)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.BACK, back)
                .copySlot(TextureSlot.EAST, TextureSlot.PARTICLE);
    }

    private TextureMapping getHoneyBlockTextureMap(DyeColor color) {
        return (new TextureMapping())
                .put(TextureSlot.UP, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/petrified_honey/" + color.getSerializedName() + "_top"))
                .put(TextureSlot.DOWN, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/petrified_honey/" + color.getSerializedName() + "_bottom"))
                .put(TextureSlot.SIDE, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/petrified_honey/" + color.getSerializedName() + "_side"))
                .copySlot(TextureSlot.UP, TextureSlot.PARTICLE);
    }

    public static ModelTemplate getHiveModelTemplate() {
        return new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/advanced_beehive_template")), Optional.empty(), TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    public static ModelTemplate getExpansionModelTemplate() {
        return new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/expansion_box_template")), Optional.empty(), TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    public static ModelTemplate getHoneyBlockModelTemplate() {
        return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/honey_block")), Optional.empty(), TextureSlot.DOWN, TextureSlot.UP, TextureSlot.SIDE, TextureSlot.PARTICLE);
    }

    public static ModelTemplate getItemTemplate(ResourceLocation parent) {
        return new ModelTemplate(Optional.of(parent), Optional.empty());
    }

    public static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation id) {
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, id));
    }

    private String capName(String name) {
        String[] nameParts = name.split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts);
    }
}
