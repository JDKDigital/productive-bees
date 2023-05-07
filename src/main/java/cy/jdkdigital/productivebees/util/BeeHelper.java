package cy.jdkdigital.productivebees.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.StoneChip;
import cy.jdkdigital.productivebees.common.item.WoodChip;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BeeHelper
{
    private static final Map<String, List<BlockConversionRecipe>> blockConversionRecipeMap = new HashMap<>();
    private static final Map<String, List<BeeNBTChangerRecipe>> nbtChangerRecipeMap = new HashMap<>();

    public static Entity itemInteract(Bee entity, ItemStack itemStack, ServerLevel level, CompoundTag nbt, Player player) {
        Entity bee = null;

        if (!entity.isBaby()) {
            Container beeInv = new IdentifierInventory(entity, ForgeRegistries.ITEMS.getKey(itemStack.getItem()) + "");

            List<BeeConversionRecipe> recipes = new ArrayList<>();

            // Conversion recipes
            Map<ResourceLocation, BeeConversionRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
            for (Map.Entry<ResourceLocation, BeeConversionRecipe> entry : allRecipes.entrySet()) {
                BeeConversionRecipe recipe = entry.getValue();
                if (recipe.matches(beeInv, level)) {
                    recipes.add(recipe);
                }
            }

            if (!recipes.isEmpty()) {
                BeeConversionRecipe recipe = recipes.get(level.random.nextInt(recipes.size()));
                if (level.random.nextInt(100) < recipe.chance) {
                    bee = recipe.result.get().getBeeEntity().create(level);
                    if (bee instanceof ConfigurableBee) {
                        ((ConfigurableBee) bee).setBeeType(recipe.result.get().getBeeType().toString());
                        ((ConfigurableBee) bee).setAttributes();
                    }

                    if (bee instanceof ProductiveBee && entity instanceof ProductiveBee) {
                        setOffspringAttributes((ProductiveBee) bee, (ProductiveBee) entity, entity);
                    }
                }
            }
        }

        if (bee != null) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }

            BlockPos pos = entity.blockPosition();
            bee.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());
            if (bee instanceof LivingEntity) {
                ((LivingEntity) bee).setHealth(entity.getHealth());
                ((LivingEntity) bee).yBodyRot = entity.yBodyRot;
            }
            if (bee instanceof Animal) {
                if (entity.getAge() > 0) {
                    ((Animal) bee).setAge(entity.getAge());
                }
            }

            return bee;
        }
        return null;
    }

    @Nullable
    public static Entity getBreedingResult(Bee beeEntity, AgeableMob targetEntity, ServerLevel world) {
        BeeBreedingRecipe recipe = getRandomBreedingRecipe(beeEntity, targetEntity, world);
        if (recipe != null) {
            if (recipe.offspring != null) {
                BeeIngredient beeIngredient = recipe.offspring.get();

                if (beeIngredient != null) {
                    Entity newBee = beeIngredient.getBeeEntity().create(world);
                    if (newBee instanceof ConfigurableBee) {
                        ((ConfigurableBee) newBee).setBeeType(beeIngredient.getBeeType().toString());
                        ((ConfigurableBee) newBee).setAttributes();
                    }
                    return newBee;
                }
            }
        }

        // No recipe, check if any of the parents are not self breedable
        if ((beeEntity instanceof ProductiveBee && !((ProductiveBee) beeEntity).canSelfBreed()) || (targetEntity instanceof ProductiveBee && !((ProductiveBee) targetEntity).canSelfBreed())) {
            return null;
        }

        // Check if bee is configurable and make a new of same type
        if (beeEntity instanceof ConfigurableBee) {
            ResourceLocation type = new ResourceLocation(((ConfigurableBee) beeEntity).getBeeType());
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(type.toString());
            if (nbt != null && ((ConfigurableBee) beeEntity).canSelfBreed()) {
                ConfigurableBee newBee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                newBee.setBeeType(type.toString());
                newBee.setAttributes();
                return newBee;
            }
        }

        // If no specific recipe exist for the target bee or the bees are the same type, create a child like the parent
        if (beeEntity != null && (!(beeEntity instanceof ProductiveBee) || ((ProductiveBee) beeEntity).canSelfBreed())) {
            return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(beeEntity.getEncodeId())).create(world);
        }

        return null;
    }

    public static BeeBreedingRecipe getRandomBreedingRecipe(Bee beeEntity, AgeableMob targetEntity, ServerLevel level) {
        // Get breeding recipes
        List<BeeBreedingRecipe> recipes = getBreedingRecipes(beeEntity, targetEntity, level);

        if (!recipes.isEmpty()) {
            return recipes.get(level.random.nextInt(recipes.size()));
        }

        return null;
    }

    public static List<BeeBreedingRecipe> getBreedingRecipes(Bee beeEntity, AgeableMob targetEntity, ServerLevel level) {
        IdentifierInventory beeInv = new IdentifierInventory(beeEntity, (Bee) targetEntity);

        // Get breeding recipes
        List<BeeBreedingRecipe> recipes = new ArrayList<>();
        Map<ResourceLocation, BeeBreedingRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        for (Map.Entry<ResourceLocation, BeeBreedingRecipe> entry : allRecipes.entrySet()) {
            BeeBreedingRecipe recipe = entry.getValue();
            if (recipe.matches(beeInv, level)) {
                recipes.add(recipe);
            }
        }

        // If the two bees are the same, add a runtime breeding recipe
        if (beeEntity.getType().equals(targetEntity.getType()) && (!(beeEntity instanceof ProductiveBee peeBee) || (peeBee.canSelfBreed() && peeBee.getBeeType().equals(((ProductiveBee) targetEntity).getBeeType())))) {
            Lazy<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeInv.getIdentifier()));
            recipes.add(new BeeBreedingRecipe(new ResourceLocation(ProductiveBees.MODID, "bee_breeding_" + new ResourceLocation(beeInv.getIdentifier()).getPath() + "_self"), List.of(beeIngredient, beeIngredient), beeIngredient));
        }

        return recipes;
    }

    public static BlockConversionRecipe getBlockConversionRecipe(Bee beeEntity, BlockState flowerBlockState) {
        List<BlockConversionRecipe> recipes = new ArrayList<>();
        BlockStateInventory beeInv = new BlockStateInventory(beeEntity, flowerBlockState);
        String cacheKey = beeInv.getIdentifier(0) + beeInv.getIdentifier(1);
        if (blockConversionRecipeMap.containsKey(cacheKey)) {
            recipes = blockConversionRecipeMap.get(cacheKey);
        } else if (beeEntity.level instanceof ServerLevel) {
            // Get block conversion recipes
            Map<ResourceLocation, BlockConversionRecipe> allRecipes = beeEntity.level.getRecipeManager().byType(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
            for (Map.Entry<ResourceLocation, BlockConversionRecipe> entry : allRecipes.entrySet()) {
                BlockConversionRecipe recipe = entry.getValue();
                if (recipe.matches(beeInv, beeEntity.level)) {
                    recipes.add(recipe);
                }
            }

            blockConversionRecipeMap.put(cacheKey, recipes);
        }

        if (!recipes.isEmpty()) {
            return recipes.get(beeEntity.level.random.nextInt(recipes.size()));
        }
        return null;
    }

    public static boolean hasBlockConversionRecipe(Bee beeEntity, BlockState flowerBlockState) {
        return getBlockConversionRecipe(beeEntity, flowerBlockState) != null;
    }

    public static List<BeeNBTChangerRecipe> getNBTChangerRecipes(Bee beeEntity, BlockEntity blockEntity) {
        if (blockEntity instanceof FeederBlockEntity feederBlockEntity) {
            var items = feederBlockEntity.getInventoryItems();
            if (items.size() > 0) {
                List<BeeNBTChangerRecipe> recipes = new ArrayList<>();
                Map<ResourceLocation, BeeNBTChangerRecipe> allRecipes = null;
                for (ItemStack item : items) {
                    var inv = new IdentifierInventory(beeEntity, ForgeRegistries.ITEMS.getKey(item.getItem()).toString());
                    String cacheKey = inv.getIdentifier(0) + inv.getIdentifier(1);
                    if (nbtChangerRecipeMap.containsKey(cacheKey)) {
                        recipes = nbtChangerRecipeMap.get(cacheKey);
                    } else if (beeEntity.level instanceof ServerLevel) {
                        if (allRecipes == null) {
                            allRecipes = beeEntity.level.getRecipeManager().byType(ModRecipeTypes.BEE_NBT_CHANGER_TYPE.get());
                        }
                        for (Map.Entry<ResourceLocation, BeeNBTChangerRecipe> entry : allRecipes.entrySet()) {
                            if (entry.getValue().matches(inv, beeEntity.level)) {
                                recipes.add(entry.getValue());
                            }
                        }
                        nbtChangerRecipeMap.put(cacheKey, recipes);
                    }
                }
                if (!recipes.isEmpty()) {
                    return recipes;
                }
            }
        }
        return new ArrayList<>();
    }

    public static boolean hasNBTChangerRecipe(Bee beeEntity, BlockEntity feederBlockEntity) {
        return getNBTChangerRecipes(beeEntity, feederBlockEntity).size() > 0;
    }

    public static List<ItemStack> getBeeProduce(Level level, Bee beeEntity, boolean hasCombBlockUpgrade) {
        AdvancedBeehiveRecipe matchedRecipe = null;
        BlockPos flowerPos = beeEntity.getSavedFlowerPos();
        List<ItemStack> outputList = new ArrayList<>();

        String beeId = beeEntity.getEncodeId();
        if (beeId == null ) {
            return outputList;
        }

        if (beeEntity instanceof ConfigurableBee) {
            beeId = ((ConfigurableBee) beeEntity).getBeeType();
        }

        Map<ResourceLocation, AdvancedBeehiveRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        Container beeInv = new IdentifierInventory(beeId);
        for (Map.Entry<ResourceLocation, AdvancedBeehiveRecipe> entry : allRecipes.entrySet()) {
            AdvancedBeehiveRecipe recipe = entry.getValue();
            if (recipe.matches(beeInv, level)) {
                matchedRecipe = recipe;
            }
        }

        if (matchedRecipe != null) {
            matchedRecipe.getRecipeOutputs().forEach((itemStack, bounds) -> {
                if (level.random.nextInt(100) <= bounds.get(2).getAsInt()) {
                    int count = Mth.nextInt(level.random, Mth.floor(bounds.get(0).getAsInt()), Mth.floor(bounds.get(1).getAsInt()));
                    ItemStack stack = itemStack.copy();
                    stack.setCount(count);
                    outputList.add(stack);
                }
            });
        } else if (beeId.equals("productivebees:lumber_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(level, flowerPos, ModTags.LUMBER, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    ItemStack woodChip;
                    if (hasCombBlockUpgrade) {
                        woodChip = new ItemStack(flowerBlock.asItem());
                    } else {
                        woodChip = WoodChip.getStack(flowerBlock, level.random.nextInt(6) + 1);
                    }
                    outputList.add(woodChip);
                }
            }
        } else if (beeId.equals("productivebees:quarry_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(level, flowerPos, ModTags.QUARRY, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    ItemStack stoneChip;
                    if (hasCombBlockUpgrade) {
                        stoneChip = new ItemStack(flowerBlock.asItem());
                    } else {
                        stoneChip = StoneChip.getStack(flowerBlock, level.random.nextInt(6) + 1);
                    }
                    outputList.add(stoneChip);
                }
            }
        } else if (beeId.equals("productivebees:dye_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(level, flowerPos, BlockTags.FLOWERS, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    Item flowerItem = flowerBlock.asItem();

                    ItemStack dye = getRecipeOutputFromInput(level, flowerItem);
                    if (!dye.isEmpty()) {
                        dye.setCount(1);
                        outputList.add(dye);
                    }
                }
            }
        }

        return outputList;
    }

    public static ItemStack getRecipeOutputFromInput(Level level, Item input) {
        Map<ResourceLocation, CraftingRecipe> recipes = level.getRecipeManager().byType(RecipeType.CRAFTING);
        for (Map.Entry<ResourceLocation, CraftingRecipe> entry : recipes.entrySet()) {
            Recipe<CraftingContainer> recipe = entry.getValue();
            List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients.size() == 1) {
                Ingredient ingredient = ingredients.get(0);
                ItemStack[] stacks = ingredient.getItems();
                if (stacks.length > 0 && stacks[0].getItem().equals(input)) {
                    return recipe.getResultItem(level.registryAccess()).copy();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public static CentrifugeRecipe getCentrifugeRecipe(Level level, IItemHandlerModifiable inputHandler) {
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.CENTRIFUGE_TYPE.get(), new RecipeWrapper(inputHandler), level).orElse(null);
    }

    private static Block getFloweringBlock(Level level, BlockPos flowerPos, TagKey<Block> tag, ProductiveBee bee) {
        BlockState flowerBlockState = level.getBlockState(flowerPos);
        Block flowerBlock = flowerBlockState.getBlock();
        if (flowerBlock instanceof Feeder) {
            BlockEntity feederTile = level.getBlockEntity(flowerPos);
            if (feederTile instanceof FeederBlockEntity && ProductiveBee.isValidFeeder(bee, feederTile, bee::isFlowerBlock)) {
                return ((FeederBlockEntity) feederTile).getRandomBlockFromInventory(tag, level.random);
            }
        }
        return flowerBlockState.is(tag) ? flowerBlock : null;
    }

    public static void setOffspringAttributes(ProductiveBee newBee, ProductiveBee parent1, AgeableMob parent2) {
        Map<BeeAttribute<?>, Object> attributeMapParent1 = parent1.getBeeAttributes();
        Map<BeeAttribute<?>, Object> attributeMapParent2 = new HashMap<>();
        if (parent2 instanceof ProductiveBee) {
            attributeMapParent2 = ((ProductiveBee) parent2).getBeeAttributes();
        } else {
            // Default bee attributes
            attributeMapParent2.put(BeeAttributes.PRODUCTIVITY, 0);
            attributeMapParent2.put(BeeAttributes.ENDURANCE, 0);
            attributeMapParent2.put(BeeAttributes.TEMPER, 1);
            attributeMapParent2.put(BeeAttributes.BEHAVIOR, 0);
            attributeMapParent2.put(BeeAttributes.WEATHER_TOLERANCE, 0);
        }

        Map<BeeAttribute<?>, Object> attributeMapChild = newBee.getBeeAttributes();

        int parentProductivity = Mth.nextInt(newBee.level.random, (int) attributeMapParent1.get(BeeAttributes.PRODUCTIVITY), (int) attributeMapParent2.get(BeeAttributes.PRODUCTIVITY));
        newBee.setAttributeValue(BeeAttributes.PRODUCTIVITY, Math.max((int) attributeMapChild.get(BeeAttributes.PRODUCTIVITY), parentProductivity));

        int parentEndurance = Mth.nextInt(newBee.level.random, (int) attributeMapParent1.get(BeeAttributes.ENDURANCE), (int) attributeMapParent2.get(BeeAttributes.ENDURANCE));
        newBee.setAttributeValue(BeeAttributes.ENDURANCE, Math.max((int) attributeMapChild.get(BeeAttributes.ENDURANCE), parentEndurance));

        int parentTemper = Mth.nextInt(newBee.level.random, (int) attributeMapParent1.get(BeeAttributes.TEMPER), (int) attributeMapParent2.get(BeeAttributes.TEMPER));
        newBee.setAttributeValue(BeeAttributes.TEMPER, Math.min((int) attributeMapChild.get(BeeAttributes.TEMPER), parentTemper));

        int parentBehavior = Mth.nextInt(newBee.level.random, (int) attributeMapParent1.get(BeeAttributes.BEHAVIOR), (int) attributeMapParent2.get(BeeAttributes.BEHAVIOR));
        newBee.setAttributeValue(BeeAttributes.BEHAVIOR, Math.max((int) attributeMapChild.get(BeeAttributes.BEHAVIOR), parentBehavior));

        int parentWeatherTolerance = Mth.nextInt(newBee.level.random, (int) attributeMapParent1.get(BeeAttributes.WEATHER_TOLERANCE), (int) attributeMapParent2.get(BeeAttributes.WEATHER_TOLERANCE));
        newBee.setAttributeValue(BeeAttributes.WEATHER_TOLERANCE, Math.max((int) attributeMapChild.get(BeeAttributes.WEATHER_TOLERANCE), parentWeatherTolerance));
    }

    public static CompoundTag getBeeAsCompoundTag(BeeIngredient beeIngredient) throws CommandSyntaxException {
        CompoundTag bee;
        if (beeIngredient.isConfigurable()) {
            String type = beeIngredient.getBeeType().getPath();
            bee = TagParser.parseTag("{id:\"productivebees:configurable_bee\",bee_type: \"hive\", type: \"productivebees:" + type + "\", HasConverted: false}");
        } else {
            bee = TagParser.parseTag("{id:\"" + beeIngredient.getBeeType().toString() + "\",bee_type: \"solitary\", HasConverted: false}");
        }

        Random random = new Random();
        bee.putInt("bee_productivity", random.nextInt(3));
        bee.putInt("bee_temper", 1);
        bee.putInt("bee_endurance", random.nextInt(3));
        bee.putInt("bee_behavior", 0);
        bee.putInt("bee_weather_tolerance", 0);

        switch (beeIngredient.getBeeType().getPath()) {
            case "mason_bee", "blue_banded_bee" -> bee.putInt("bee_temper", 0);
            case "sweat_bee" -> bee.putInt("bee_temper", 2);
        }

        if (beeIngredient.isConfigurable()) {
            CompoundTag data = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
            if (data.contains("productivity")) {
                bee.putInt("bee_productivity", data.getInt("productivity"));
            }
            if (data.contains("temper")) {
                bee.putInt("bee_temper", data.getInt("temper"));
            }
            if (data.contains(("endurance"))) {
                bee.putInt("bee_endurance", data.getInt("endurance"));
            }
            if (data.contains(("behavior"))) {
                bee.putInt("bee_behavior", data.getInt("behavior"));
            }
            if (data.contains(("weather_tolerance"))) {
                bee.putInt("bee_weather_tolerance", data.getInt("weather_tolerance"));
            }
        }

        return bee;
    }

    public static void populateBeeInfoFromEntity(Bee bee, List<Component> list) {
        var tag = new CompoundTag();
        bee.saveWithoutId(tag);
        if (bee instanceof ProductiveBee) {
            tag.putBoolean("isProductiveBee", true);
        }
        populateBeeInfoFromTag(tag, list);
    }

    public static List<Component> populateBeeInfoFromTag(CompoundTag tag, @Nullable List<Component> list) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(Component.translatable(tag.getInt("Age") < 0 ? "productivebees.information.age.child" : "productivebees.information.age.adult").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));

        if (tag.getBoolean("isProductiveBee")) {
            float current = tag.getFloat("Health");
            float max = tag.contains("MaxHealth") ? tag.getFloat("MaxHealth") : 10.0f;
            list.add((Component.translatable("productivebees.information.attribute.health", current, max)).withStyle(ChatFormatting.DARK_GRAY));

            String type = tag.getString("bee_type");
            Component type_value = Component.translatable("productivebees.information.attribute.type." + type).withStyle(ColorUtil.getColor(type));
            list.add((Component.translatable("productivebees.information.attribute.type", type_value)).withStyle(ChatFormatting.DARK_GRAY));

            int productivity = tag.getInt("bee_productivity");
            Component productivity_value = Component.translatable(BeeAttributes.keyMap.get(BeeAttributes.PRODUCTIVITY).get(productivity)).withStyle(ColorUtil.getColor(productivity));
            list.add((Component.translatable("productivebees.information.attribute.productivity", productivity_value)).withStyle(ChatFormatting.DARK_GRAY));

            int tolerance = tag.getInt("bee_weather_tolerance");
            Component tolerance_value = Component.translatable(BeeAttributes.keyMap.get(BeeAttributes.WEATHER_TOLERANCE).get(tolerance)).withStyle(ColorUtil.getColor(tolerance));
            list.add((Component.translatable("productivebees.information.attribute.weather_tolerance", tolerance_value)).withStyle(ChatFormatting.DARK_GRAY));

            int behavior = tag.getInt("bee_behavior");
            Component behavior_value = Component.translatable(BeeAttributes.keyMap.get(BeeAttributes.BEHAVIOR).get(behavior)).withStyle(ColorUtil.getColor(behavior));
            list.add((Component.translatable("productivebees.information.attribute.behavior", behavior_value)).withStyle(ChatFormatting.DARK_GRAY));

            int endurance = tag.getInt("bee_endurance");
            Component endurance_value = Component.translatable(BeeAttributes.keyMap.get(BeeAttributes.ENDURANCE).get(endurance)).withStyle(ColorUtil.getColor(endurance));
            list.add((Component.translatable("productivebees.information.attribute.endurance", endurance_value)).withStyle(ChatFormatting.DARK_GRAY));

            int temper = tag.getInt("bee_temper");
            Component temper_value = Component.translatable(BeeAttributes.keyMap.get(BeeAttributes.TEMPER).get(temper)).withStyle(ColorUtil.getColor(temper));
            list.add((Component.translatable("productivebees.information.attribute.temper", temper_value)).withStyle(ChatFormatting.DARK_GRAY));

            CompoundTag beeData = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            MutableComponent breedingItemText = Component.translatable("productivebees.information.breeding_item_default");
            if (beeData != null && beeData.contains("breedingItem") && !beeData.getString("breedingItem").isEmpty()) {
                Item breedingItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(beeData.getString("breedingItem")));
                breedingItemText = Component.literal(beeData.getInt("breedingItemCount") + " " + Component.translatable(breedingItem.getDescriptionId()).getString());
            }
            list.add(Component.translatable("productivebees.information.breeding_item", breedingItemText.withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

            if (beeData != null && !beeData.getBoolean("selfbreed")) {
                list.add(Component.translatable("productivebees.information.selfbreed_disabled").withStyle(ChatFormatting.GRAY));
            }

            if (tag.contains("HivePos")) {
                BlockPos hivePos = NbtUtils.readBlockPos(tag.getCompound("HivePos"));
                list.add(Component.translatable("productivebees.information.home_position", hivePos.getX(), hivePos.getY(), hivePos.getZ()));
            }
        } else {
            list.add((Component.literal("Mod: " + tag.getString("mod"))).withStyle(ChatFormatting.DARK_AQUA));
        }

        return list;
    }

    public static class IdentifierInventory implements Container
    {
        private final List<String> identifiers = new ArrayList<>();

        public IdentifierInventory(String identifier) {
            this.identifiers.add(identifier);
        }

        public IdentifierInventory(Bee bee1, Bee bee2) {
            String identifier1 = bee1.getEncodeId();
            if (bee1 instanceof ConfigurableBee) {
                identifier1 = ((ConfigurableBee) bee1).getBeeType();
            }
            String identifier2 = bee2.getEncodeId();
            if (bee2 instanceof ConfigurableBee) {
                identifier2 = ((ConfigurableBee) bee2).getBeeType();
            }
            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public IdentifierInventory(Bee bee1, String identifier2) {
            String identifier1 = bee1.getEncodeId();
            if (bee1 instanceof ConfigurableBee) {
                identifier1 = ((ConfigurableBee) bee1).getBeeType();
            }
            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public IdentifierInventory(String identifier1, String identifier2) {
            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public String getIdentifier() {
            return getIdentifier(0);
        }

        public String getIdentifier(int index) {
            return this.identifiers.get(index);
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return identifiers.isEmpty();
        }

        @Nonnull
        @Override
        public ItemStack getItem(int i) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack removeItem(int i, int i1) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack removeItemNoUpdate(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int i, @Nonnull ItemStack itemStack) {

        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(@Nonnull Player playerEntity) {
            return false;
        }

        @Override
        public void clearContent() {
            this.identifiers.clear();
        }
    }

    public static class BlockStateInventory extends IdentifierInventory {
        private BlockState state;

        public BlockStateInventory(Bee bee1, BlockState state) {
            super(bee1, state.toString());
            this.state = state;
        }

        public BlockState getState() {
            return state;
        }
    }
}
