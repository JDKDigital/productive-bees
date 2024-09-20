package cy.jdkdigital.productivebees.client.helper;

import com.google.common.collect.Streams;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.item.AmberItem;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.BeeFloweringRecipe;
import cy.jdkdigital.productivebees.common.recipe.IncubationRecipe;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeHelper
{
    public static List<BeeFloweringRecipe> getFlowersRecipes(Map<String, BeeIngredient> beeList) {
        List<BeeFloweringRecipe> recipes = new ArrayList<>();

        // Hardcoded for now until bees are moved to config
        Map<String, TagKey<Block>> flowering = new HashMap<>();
        flowering.put("productivebees:blue_banded_bee", ModTags.RIVER_FLOWERS);
        flowering.put("productivebees:green_carpenter_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:nomad_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:chocolate_mining_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:ashy_mining_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:reed_bee", ModTags.SWAMP_FLOWERS);
        flowering.put("productivebees:resin_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:sweat_bee", ModTags.SNOW_FLOWERS);
        flowering.put("productivebees:yellow_black_carpenter_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:lumber_bee", ModTags.LUMBER);
        flowering.put("productivebees:quarry_bee", ModTags.QUARRY);
        flowering.put("productivebees:creeper_bee", ModTags.POWDERY);

        TagKey<Block> defaultBlockTag = BlockTags.FLOWERS;

        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            if (entry.getValue().isConfigurable()) {
                CompoundTag nbt = BeeReloadListener.INSTANCE.getData(entry.getValue().getBeeType());
                if (nbt.getString("flowerType").equals("entity_types")) {
                    if (nbt.contains("flowerTag")) {
                        TagKey<EntityType<?>> flowerTag = ModTags.getEntityTag(ResourceLocation.parse(nbt.getString("flowerTag")));
                        var entityTypeList = Streams.stream(BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(flowerTag)).map(Holder::value).toList();
                        entityTypeList.forEach(entityType -> {
                            recipes.add(BeeFloweringRecipe.createItem(id(entry.getValue().getBeeType().getPath() + "_" + entityType.getDescriptionId().replace(".", "")), AmberItem.getFakeAmberItem(entityType), entry.getValue()));
                        });
                    }
                } else {
                    if (nbt.contains("flowerTag")) {
                        TagKey<Block> flowerTag = ModTags.getBlockTag(ResourceLocation.parse(nbt.getString("flowerTag")));
                        TagKey<Item> itemFlowerTag = ModTags.getItemTag(ResourceLocation.parse(nbt.getString("flowerTag")));
                        recipes.add(BeeFloweringRecipe.createBlock(id(entry.getValue().getBeeType().getPath()), flowerTag, itemFlowerTag, entry.getValue()));
                    } else if (nbt.contains("flowerBlock")) {
                        Block flowerBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("flowerBlock")));
                        if (flowerBlock != null && !flowerBlock.equals(Blocks.AIR)) {
                            recipes.add(BeeFloweringRecipe.createBlock(id(entry.getValue().getBeeType().getPath()), flowerBlock, entry.getValue()));
                        }
                    } else if (nbt.contains("flowerFluid")) {
                        if (nbt.getString("flowerFluid").contains("#")) {
                            TagKey<Fluid> flowerFluid = ModTags.getFluidTag(ResourceLocation.parse(nbt.getString("flowerFluid").replace("#", "")));
                            recipes.add(BeeFloweringRecipe.createFluid(id(entry.getValue().getBeeType().getPath()), flowerFluid, entry.getValue()));
                        } else {
                            Fluid flowerFluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(nbt.getString("flowerFluid")));
                            recipes.add(BeeFloweringRecipe.createFluid(id(entry.getValue().getBeeType().getPath()), flowerFluid, entry.getValue()));
                        }
                    } else if (nbt.contains("flowerItem")) {
                        Item flowerItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(nbt.getString("flowerItem")));
                        recipes.add(BeeFloweringRecipe.createItem(id(entry.getValue().getBeeType().getPath()), new ItemStack(flowerItem), entry.getValue()));
                    } else {
                        recipes.add(BeeFloweringRecipe.createBlock(id(entry.getValue().getBeeType().getPath()), defaultBlockTag, null, entry.getValue()));
                    }
                }
            } else if (entry.getValue().getBeeType().toString().equals("productivebees:rancher_bee")) {
                var entityTypeList = Streams.stream(BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(ModTags.RANCHABLES)).map(Holder::value).toList();
                entityTypeList.forEach(entityType -> {
                    recipes.add(BeeFloweringRecipe.createItem(id(entry.getValue().getBeeType().getPath() + "_" + entityType.getDescriptionId().replace(".", "")), AmberItem.getFakeAmberItem(entityType), entry.getValue()));
                });
            } else if (flowering.containsKey(entry.getValue().getBeeType().toString())) {
                TagKey<Block> blockTag = flowering.get(entry.getValue().getBeeType().toString());
                recipes.add(BeeFloweringRecipe.createBlock(id(entry.getValue().getBeeType().getPath()), blockTag, null, entry.getValue()));
            } else {
                recipes.add(BeeFloweringRecipe.createBlock(id(entry.getValue().getBeeType().getPath()), defaultBlockTag, null, entry.getValue()));
            }
        }
        return recipes;
    }

    public static List<RecipeHolder<IncubationRecipe>> getRecipes(Map<String, BeeIngredient> beeList) {
        List<RecipeHolder<IncubationRecipe>> recipes = new ArrayList<>();

        if (Minecraft.getInstance().level != null) {
            // babee to adult incubation
            Bee bee = EntityType.BEE.create(Minecraft.getInstance().level);
            Bee baBee = EntityType.BEE.create(Minecraft.getInstance().level);
            if (bee != null && baBee != null) {
                ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
                ItemStack babeeCage = new ItemStack(ModItems.BEE_CAGE.get());

                baBee.setAge(-24000);
                BeeCage.captureEntity(bee, cage);
                BeeCage.captureEntity(baBee, babeeCage);
                ItemStack treats = new ItemStack(ModItems.HONEY_TREAT.get(), ProductiveBeesConfig.GENERAL.incubatorTreatUse.get());
                recipes.add(new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "/babee_incubation"), new IncubationRecipe(DataComponentIngredient.of(false, babeeCage), Ingredient.of(treats), cage, 300)));
            }

            // Spawn egg incubation
            for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
                ItemStack spawnEgg = BeeCreator.getSpawnEgg(ResourceLocation.parse(entry.getKey()));
                Ingredient treat = DataComponentIngredient.of(false, HoneyTreat.getTypeStack(entry.getKey(), 100));
                recipes.add(new RecipeHolder<>(ResourceLocation.parse(entry.getKey()).withPath(p -> "/" + p + "_incubation"), new IncubationRecipe(Ingredient.of(Tags.Items.EGGS), treat, spawnEgg, 300)));
            }
        }

        return recipes;
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, name);
    }
}
