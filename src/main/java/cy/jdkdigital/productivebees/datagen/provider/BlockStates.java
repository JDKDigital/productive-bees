package cy.jdkdigital.productivebees.datagen.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.block.ExpansionBox;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Function;

public class BlockStates extends BlockStateProvider
{
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ProductiveBees.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (RegistryObject<Block> registryObject : ModBlocks.BLOCKS.getEntries()) {
            Block block = registryObject.get();
            ResourceLocation name = block.getRegistryName();

            assert name != null;

            if (name.getPath().contains("comb_")) {
                registerCombBlock(block, name);
            }
            else if (name.getPath().contains("advanced_")) {
                registerAdvancedHive(block, name);
            }
            else if (name.getPath().contains("expansion_box_")) {
                registerExpansionBox(block, name);
            }
            else if (name.getPath().contains("_wood_nest")) {
                registerWoodNest(block, name);
            }
            else if (name.getPath().equals("slimy_nest")) {
                registerSlimyNest(block, name);
            }
            else if (name.getPath().contains("_nest")) {
                registerSolitaryNest(block, name);
            }
            else if (name.getPath().contains("inactive_dragon_egg")) {
                this.simpleBlock(block, models().getExistingFile(new ResourceLocation("block/dragon_egg")));
            }
            else if (name.getPath().contains("dragon_egg_hive")) {
                this.simpleBlock(block, models().getExistingFile(new ResourceLocation(ProductiveBees.MODID, "block/dragon_egg_hive")));
            }
            else if (name.getPath().equals("honey")) {
                this.simpleBlock(block, models().getBuilder("honey").texture("particle", new ResourceLocation(ProductiveBees.MODID, "block/honey/still")));
            }
            else if (name.getPath().equals("bamboo_hive")) {
                registerBambooHive(block, name);
            }

            // bottler
            // centrifuge
            // powered_centrifuge
//            else if (name.getPath().contains("centrifuge")) {
//                this.simpleBlock(block, models().getExistingFile(new ResourceLocation(ProductiveBees.MODID, "block/centrifuge/idle")));
//            }
        }
    }

    private void registerSlimyNest(Block block, ResourceLocation name) {
        this.simpleBlock(block, models().getExistingFile(new ResourceLocation(ProductiveBees.MODID, "block/nest/slimy_nest")));
    }

    private void registerCombBlock(Block block, ResourceLocation name) {
        this.simpleBlock(block, models().cubeAll("block/comb/" + name.getPath(), new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath().replace("_", "/"))));
    }

    private void registerAdvancedHive(Block block, ResourceLocation name) {
        String simpleName = name.getPath().replace("advanced_", "");

        ResourceLocation side = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + simpleName + "_side");
        ResourceLocation front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + simpleName + "_front");
        ResourceLocation top = new ResourceLocation("buzzierbees", "block/" + simpleName + "_end");
        ResourceLocation front_honey = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + simpleName + "_front_honey");

        BlockModelBuilder model = models().orientableWithBottom("block/advanced_beehive/" + name.getPath(), side, front, top, top);
        BlockModelBuilder modelHoney = models().orientableWithBottom("block/advanced_beehive/" + name.getPath() + "_honey", side, front_honey, top, top);
        ModelFile.UncheckedModelFile bbModel = new BlockModelBuilder.UncheckedModelFile("productivebees:block/advanced_beehive/small/" + simpleName);
        ModelFile.UncheckedModelFile bbModelHoney = new BlockModelBuilder.UncheckedModelFile("productivebees:block/advanced_beehive/small/" + simpleName + "_honey");

        this.directionalBlock(block, state -> {
            if (!state.get(AdvancedBeehive.EXPANDED)) {
                if (state.get(BeehiveBlock.HONEY_LEVEL) == 5) {
                    return bbModelHoney;
                }
                return bbModel;
            } else {
                if (state.get(BeehiveBlock.HONEY_LEVEL) == 5) {
                    return modelHoney;
                }
                return model;
            }
        });
    }

    private void registerExpansionBox(Block block, ResourceLocation name) {
        String simpleName = name.getPath().replace("expansion_box_", "") + "_beehive";

        ResourceLocation sideSmall = new ResourceLocation("buzzierbees", "block/" + simpleName + "_side");
        ResourceLocation side = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + simpleName + "_side_top");
        ResourceLocation front = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + simpleName + "_side_top");
        ResourceLocation top = new ResourceLocation("buzzierbees", "block/" + simpleName + "_end");
        ResourceLocation front_honey = new ResourceLocation(ProductiveBees.MODID, "block/advanced_beehive/" + simpleName + "_side_top_honey");

        BlockModelBuilder model = models().orientableWithBottom("block/expansion_box/" + name.getPath(), side, front, top, top);
        BlockModelBuilder modelSmall = models().orientableWithBottom("block/expansion_box/" + name.getPath() + "_small", sideSmall, front, top, top);
        BlockModelBuilder modelHoney = models().orientableWithBottom("block/expansion_box/" + name.getPath() + "_honey", side, front_honey, top, top);

        this.directionalBlock(block, state -> {
            if (!state.get(AdvancedBeehive.EXPANDED)) {
                return modelSmall;
            } else {
                if (state.get(ExpansionBox.HAS_HONEY)) {
                    return modelHoney;
                }
                return model;
            }
        });
    }

    private void registerWoodNest(Block block, ResourceLocation name) {
        String woodName = name.getPath().replace("_wood_nest", "");
        ResourceLocation end = new ResourceLocation("block/" + woodName + "_log_top");
        ResourceLocation side = new ResourceLocation("block/" + woodName + "_log");
        ResourceLocation front = new ResourceLocation(ProductiveBees.MODID, "block/nest/wood/" + woodName);

        Function<BlockState, ModelFile> modelFunc = blockState -> {
            return models().withExistingParent("block/nest/" + name.getPath(), ProductiveBees.MODID + ":" + ModelProvider.BLOCK_FOLDER + "/nest/wood_nest")
                    .texture("end", end)
                    .texture("front", front)
                    .texture("side", side);
        };

        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.get(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.getHorizontalAngle()) + 180) % 360)
                            .build();
                });
    }

    private void registerSolitaryNest(Block block, ResourceLocation name) {
        String simpleName = name.getPath().replace("_nest", "")
                .replace("nether_quartz", "nether_quartz_ore")
                .replace("nether_brick", "nether_bricks");
        ResourceLocation side = new ResourceLocation("block/" + simpleName);
        ResourceLocation front = new ResourceLocation(ProductiveBees.MODID, "block/nest/" + simpleName);

        BlockModelBuilder model = models().orientable("block/nest/" + name.getPath(), side, front, side);
        BlockModelBuilder modelVertical = models().orientableVertical("block/nest/" + name.getPath() + "_vertical", side, front);

        this.directionalBlock(block, state -> {
            if (state.get(BlockStateProperties.FACING) == Direction.UP || state.get(BlockStateProperties.FACING) == Direction.DOWN) {
                return modelVertical;
            }
            return model;
        });
    }

    private void registerBambooHive(Block block, ResourceLocation name) {
        ResourceLocation side = new ResourceLocation(ProductiveBees.MODID, "block/bamboo_hive/side");
        ResourceLocation end = new ResourceLocation(ProductiveBees.MODID, "block/bamboo_hive/front");

        BlockModelBuilder model = models().cubeColumn(name.getPath(), side, end);

        this.directionalBlock(block, blockState -> model);
    }

    private void registerCentrifuge(Block block, ResourceLocation name) {
        BlockModelBuilder dimCellFrame = models().getBuilder("block/complex/main");

        floatingCube(dimCellFrame, 0f, 3f, 0f, 0.5f, 16f, 16f, "#side", "#top", "#inside");
        floatingCube(dimCellFrame, 0.5f, 3f, 0f, 1f, 16f, 16f, "#side_inner", "#top", "#inside");
        floatingCube(dimCellFrame, 1f, 3f, 1f, 15f, 4f, 15f, "", "#inside", "#inside");
        floatingCube(dimCellFrame, 15.5f, 3f, 0f, 16f, 16f, 16f, "#side", "#top", "#inside");
        floatingCube(dimCellFrame, 15f, 3f, 0f, 15.5f, 16f, 16f, "#side_inner", "#top", "#inside");
        floatingCube(dimCellFrame, 1f, 3f, 0f, 15f, 16f, 0.5f, "#side", "#top", "#inside");
        floatingCube(dimCellFrame, 1f, 3f, 0.5f, 15f, 16f, 1f, "#side_inner", "#top", "#inside");
        floatingCube(dimCellFrame, 1f, 3f, 15.5f, 15f, 16f, 16f, "#side", "#top", "#inside");
        floatingCube(dimCellFrame, 1f, 3f, 15f, 15f, 16f, 15.5f, "#side", "#top", "#inside");
        floatingCube(dimCellFrame, 0f, 0f, 0f, 3f, 3f, 1f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 0f, 0f, 1f, 1f, 3f, 3f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 0f, 0f, 1f, 1f, 3f, 3f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 13f, 0f, 0f, 16f, 3f, 1f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 15f, 0f, 1f, 16f, 3f, 3f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 0f, 0f, 15f, 3f, 3f, 16f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 0f, 0f, 13f, 1f, 3f, 15f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 13f, 0f, 15f, 16f, 3f, 16f, "#side", "#top", "#bottom");
        floatingCube(dimCellFrame, 15f, 0f, 13f, 16f, 3f, 15f, "#side", "#top", "#bottom");

        // Grindstone insert
        dimCellFrame.element().from(2f, 4f, 2f).to(14f, 8f, 14f)
            .faces((direction, faceBuilder) -> {
                if (!direction.equals(Direction.DOWN)) {
                    if (direction.equals(Direction.UP)) {
                        faceBuilder.texture(("#grindstone_side")).cullface(direction).uvs(0, 0, 12, 12);
                    }
                    else {
                        faceBuilder.texture("#grindstone_round").cullface(direction).uvs(0, 0, 12, 4);
                    }
                }
            }).end();
        dimCellFrame.element().from(6f, 8f, 6f).to(10f, 9f, 10f)
            .faces((direction, faceBuilder) -> {
                if (!direction.equals(Direction.DOWN)) {
                    if (direction.equals(Direction.UP)) {
                        faceBuilder.texture(("#grindstone_pivot")).cullface(direction).uvs(0, 0, 4, 4);
                    }
                    else {
                        faceBuilder.texture("#grindstone_pivot").cullface(direction).uvs(0, 0, 4, 1);
                    }
                }
            }).end();

        dimCellFrame.texture("top",  modLoc("block/centrifuge/top"));
        dimCellFrame.texture("bottom",  modLoc("block/centrifuge/bottom"));
        dimCellFrame.texture("particle",  modLoc("block/centrifuge/side"));
        dimCellFrame.texture("side",  modLoc("block/centrifuge/side"));
        dimCellFrame.texture("side_inner",  modLoc("block/centrifuge/side"));
        dimCellFrame.texture("inside",  modLoc("block/centrifuge/inner"));
        dimCellFrame.texture("grindstone_round", "block/grindstone_round");
        dimCellFrame.texture("grindstone_side", "block/grindstone_side");
        dimCellFrame.texture("grindstone_pivot", "block/grindstone_pivot");

        createCentrifugeCellModel(block, dimCellFrame);
    }

    private static void floatingCube(BlockModelBuilder builder, float fx, float fy, float fz, float tx, float ty, float tz, String defTex, String upTex, String downTex) {
        builder.element().from(fx, fy, fz).to(tx, ty, tz)
            .faces((direction, faceBuilder) -> {
                if (direction.equals(Direction.UP)) {
                    faceBuilder.texture((upTex)).cullface(direction);
                }
                else if (direction.equals(Direction.DOWN)) {
                    faceBuilder.texture((downTex));
                }
                else if (!defTex.isEmpty()) {
                    faceBuilder.texture(defTex).cullface(direction);
                }
            })
            .end();
    }

    private void createCentrifugeCellModel(Block block, BlockModelBuilder dimCellFrame) {
        BlockModelBuilder idle = models().withExistingParent("idle", "block");
        BlockModelBuilder running = models().withExistingParent("running", new ResourceLocation(ProductiveBees.MODID, "block/centrifuge/idle"));

//        getVariantBuilder(block).forAllStates(state -> {
//            if (state.get(Centrifuge.RUNNING)) {
//                return running;
//            }
//            return idle;
//        });
    }
}
