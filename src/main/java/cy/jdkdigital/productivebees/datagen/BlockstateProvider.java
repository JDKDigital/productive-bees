package cy.jdkdigital.productivebees.datagen;

import com.google.gson.JsonElement;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fml.ModList;

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
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                ProductiveBees.LOGGER.info("\"block.productivebees.advanced_" + name + "_beehive\": \"Advanced " + (name.substring(0, 1).toUpperCase() + name.substring(1)) + " Beehive\"");
            });
        });
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                ProductiveBees.LOGGER.info("\"block.productivebees.expansion_box_" + name + "\": \"" + (name.substring(0, 1).toUpperCase() + name.substring(1)) + " Expansion Box\"");
            });
        });

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
        Block baseHive = ModBlocks.HIVES.get("advanced_oak_beehive").get();
        Block baseBox = ModBlocks.EXPANSIONS.get("expansion_box_oak").get();

        var hiveTemplate = getHiveModelTemplate();
        var tintedHiveTemplate = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted");
        var tintedHiveTemplateUp = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_up");
        var tintedHiveTemplateDown = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_down");
        var tintedHiveTemplateLeft = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_left");
        var tintedHiveTemplateRight = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_right");
        var tintedHiveTemplateBack = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_back");
        var tintedHiveTemplateHoney = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_honey");
        var tintedHiveTemplateUpHoney = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_up_honey");
        var tintedHiveTemplateDownHoney = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_down_honey");
        var tintedHiveTemplateLeftHoney = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_left_honey");
        var tintedHiveTemplateRightHoney = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_right_honey");
        var tintedHiveTemplateBackHoney = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_tinted_back_honey");

        var expansionTemplate = getExpansionModelTemplate();
        var tintedExpansionTemplate = new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_tinted");
        var tintedExpansionTemplateUp = new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_tinted_up");
        var tintedExpansionTemplateDown = new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_tinted_down");
        var tintedExpansionTemplateLeft = new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_tinted_left");
        var tintedExpansionTemplateRight = new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_tinted_right");
        var tintedExpansionTemplateBack = new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_tinted_back");

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                if (!completedTypes.contains(name)) {
                    completedTypes.add(name);

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
                    var singleModel = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase), textureMap, this.modelOutput) : tintedHiveTemplate;
                    var upModel = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_up"), textureMapUp, this.modelOutput) : tintedHiveTemplateUp;
                    var downModel = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_down"), textureMapDown, this.modelOutput) : tintedHiveTemplateDown;
                    var leftModel = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_left"), textureMapLeft, this.modelOutput) : tintedHiveTemplateLeft;
                    var rightModel = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_right"), textureMapRight, this.modelOutput) : tintedHiveTemplateRight;
                    var backModel = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_back"), textureMapBack, this.modelOutput) : tintedHiveTemplateBack;
                    var singleModelHoney = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_honey"), textureMapHoney, this.modelOutput) : tintedHiveTemplateHoney;
                    var upModelHoney = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_up_honey"), textureMapUpHoney, this.modelOutput) : tintedHiveTemplateUpHoney;
                    var downModelHoney = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_down_honey"), textureMapDownHoney, this.modelOutput) : tintedHiveTemplateDownHoney;
                    var leftModelHoney = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_left_honey"), textureMapLeftHoney, this.modelOutput) : tintedHiveTemplateLeftHoney;
                    var rightModelHoney = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_right_honey"), textureMapRightHoney, this.modelOutput) : tintedHiveTemplateRightHoney;
                    var backModelHoney = type.hasTexture() ? hiveTemplate.create(new ResourceLocation(ProductiveBees.MODID, hiveModelBase + "_back_honey"), textureMapBackHoney, this.modelOutput) : tintedHiveTemplateBackHoney;

                    this.blockstates.put(new ResourceLocation(ProductiveBees.MODID, "advanced_" + name + "_beehive"),
                            MultiVariantGenerator.multiVariant(baseHive)
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
                    getItemTemplate(type.hasTexture() ? hiveModelBase : "block/advanced_beehive_tinted").create(new ResourceLocation(ProductiveBees.MODID, "item/advanced_" + name + "_beehive"), new TextureMapping(), this.modelOutput);

                    // Expansion box
                    var expansionTextureMap = type.hasTexture() ? getExpansionTextureMap(VerticalHive.NONE, name) : new TextureMapping();
                    var expansionTextureMapUp = type.hasTexture() ? getExpansionTextureMap(VerticalHive.UP, name) : new TextureMapping();
                    var expansionTextureMapDown = type.hasTexture() ? getExpansionTextureMap(VerticalHive.DOWN, name) : new TextureMapping();
                    var expansionTextureMapLeft = type.hasTexture() ? getExpansionTextureMap(VerticalHive.LEFT, name) : new TextureMapping();
                    var expansionTextureMapRight = type.hasTexture() ? getExpansionTextureMap(VerticalHive.RIGHT, name) : new TextureMapping();
                    var expansionTextureMapBack = type.hasTexture() ? getExpansionTextureMap(VerticalHive.BACK, name) : new TextureMapping();

                    String boxModelBase = "block/expansion_boxes/expansion_box_" + name;
                    var expansionSingleModel = type.hasTexture() ? expansionTemplate.create(new ResourceLocation(ProductiveBees.MODID, boxModelBase), expansionTextureMap, this.modelOutput): tintedExpansionTemplate;
                    var expansionUpModel = type.hasTexture() ? expansionTemplate.create(new ResourceLocation(ProductiveBees.MODID, boxModelBase + "_up"), expansionTextureMapUp, this.modelOutput) : tintedExpansionTemplateUp;
                    var expansionDownModel = type.hasTexture() ? expansionTemplate.create(new ResourceLocation(ProductiveBees.MODID, boxModelBase + "_down"), expansionTextureMapDown, this.modelOutput) : tintedExpansionTemplateDown;
                    var expansionLeftModel = type.hasTexture() ? expansionTemplate.create(new ResourceLocation(ProductiveBees.MODID, boxModelBase + "_left"), expansionTextureMapLeft, this.modelOutput) : tintedExpansionTemplateLeft;
                    var expansionRightModel = type.hasTexture() ? expansionTemplate.create(new ResourceLocation(ProductiveBees.MODID, boxModelBase + "_right"), expansionTextureMapRight, this.modelOutput) : tintedExpansionTemplateRight;
                    var expansionBackModel = type.hasTexture() ? expansionTemplate.create(new ResourceLocation(ProductiveBees.MODID, boxModelBase + "_back"), expansionTextureMapBack, this.modelOutput) : tintedExpansionTemplateBack;

                    this.blockstates.put(new ResourceLocation(ProductiveBees.MODID, "expansion_box_" + name),
                            MultiVariantGenerator.multiVariant(baseBox)
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
                    getItemTemplate(type.hasTexture() ? boxModelBase : "block/expansion_box_tinted").create(new ResourceLocation(ProductiveBees.MODID, "item/expansion_box_" + name), new TextureMapping(), this.modelOutput);
                }
            });
        });
    }

    private TextureMapping getHiveTextureMap(VerticalHive expand, String type, boolean hasHoney) {
        var front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_front" + (hasHoney ? "_honey" : ""));
        var back = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var right = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var left = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var top = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end");
        var bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_front_" + expand + (hasHoney ? "_honey" : ""));
                right = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                left = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                back = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
            }
            case LEFT, RIGHT -> {
                front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_front_" + expand.opposite()+ (hasHoney ? "_honey" : ""));
                back = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                top = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_" + expand);
                bottom = top;
            }
            case BACK -> {
                right = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_left");
                left = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_right");
                top = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_front");
                bottom = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_back");
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

    private TextureMapping getExpansionTextureMap(VerticalHive expand, String type) {
        var front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var back = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var left = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var right = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side");
        var top = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end");
        var bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                left = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                right = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                back = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
            }
            case LEFT, RIGHT -> {
                front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand);
                back = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                top = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_" + expand.opposite());
                bottom = top;
            }
            case BACK -> {
                left = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_left");
                right = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_side_right");
                top = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_back");
                bottom = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + type + "_beehive_end_front");
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

    private ModelTemplate getHiveModelTemplate() {
        return new ModelTemplate(Optional.of(new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive_template")), Optional.empty(), TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    private ModelTemplate getExpansionModelTemplate() {
        return new ModelTemplate(Optional.of(new ResourceLocation(ProductiveBees.MODID, "block/expansion_box_template")), Optional.empty(), TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    private ModelTemplate getItemTemplate(String parent) {
        return new ModelTemplate(Optional.of(new ResourceLocation(ProductiveBees.MODID, parent)), Optional.empty());
    }
}
