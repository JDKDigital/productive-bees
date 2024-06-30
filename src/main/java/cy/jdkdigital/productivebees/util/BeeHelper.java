package cy.jdkdigital.productivebees.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class BeeHelper
{
    private static final Map<String, List<RecipeHolder<BlockConversionRecipe>>> blockConversionRecipeMap = new HashMap<>();
    private static final Map<String, List<RecipeHolder<ItemConversionRecipe>>> itemConversionRecipeMap = new HashMap<>();
    private static final Map<String, List<RecipeHolder<BeeNBTChangerRecipe>>> nbtChangerRecipeMap = new HashMap<>();

    public static Entity itemInteract(Bee entity, ItemStack itemStack, ServerLevel level, CompoundTag nbt, Player player) {
        Entity bee = null;

        if (!entity.isBaby()) {
            var beeInv = new IdentifierInventory(entity, BuiltInRegistries.ITEM.getKey(itemStack.getItem()) + "");

            List<BeeConversionRecipe> recipes = new ArrayList<>();

            // Conversion recipes
            List<RecipeHolder<BeeConversionRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
            for (RecipeHolder<BeeConversionRecipe> entry : allRecipes) {
                BeeConversionRecipe recipe = entry.value();
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
                        ((ConfigurableBee) bee).setDefaultAttributes();
                    }

                    if (bee instanceof ProductiveBee && entity instanceof ProductiveBee) {
                        setOffspringAttributes((ProductiveBee) bee, (ProductiveBee) entity, entity);
                    }
                }
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
            }
        }

        if (bee != null) {
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
        RecipeHolder<BeeBreedingRecipe> recipe = getRandomBreedingRecipe(beeEntity, targetEntity, world);
        if (recipe != null) {
            if (recipe.value().offspring.get() != null) {
                BeeIngredient beeIngredient = recipe.value().offspring.get();

                if (beeIngredient != null) {
                    Entity newBee = beeIngredient.getBeeEntity().create(world);
                    if (newBee instanceof ConfigurableBee) {
                        ((ConfigurableBee) newBee).setBeeType(beeIngredient.getBeeType().toString());
                        ((ConfigurableBee) newBee).setDefaultAttributes();
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
            ResourceLocation type = ((ConfigurableBee) beeEntity).getBeeType();
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(type.toString());
            if (nbt != null && ((ConfigurableBee) beeEntity).canSelfBreed()) {
                ConfigurableBee newBee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                newBee.setBeeType(type.toString());
                newBee.setDefaultAttributes();
                return newBee;
            }
        }

        // If no specific recipe exist for the target bee or the bees are the same type, create a child like the parent
        if (beeEntity != null && (!(beeEntity instanceof ProductiveBee) || ((ProductiveBee) beeEntity).canSelfBreed())) {
            return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(beeEntity.getEncodeId())).create(world);
        }

        return null;
    }

    public static RecipeHolder<BeeBreedingRecipe> getRandomBreedingRecipe(Bee beeEntity, AgeableMob targetEntity, ServerLevel level) {
        // Get breeding recipes
        List<RecipeHolder<BeeBreedingRecipe>> recipes = getBreedingRecipes(beeEntity, targetEntity, level);

        if (!recipes.isEmpty()) {
            return recipes.get(level.random.nextInt(recipes.size()));
        }

        return null;
    }

    public static List<RecipeHolder<BeeBreedingRecipe>> getBreedingRecipes(Bee beeEntity, AgeableMob targetEntity, ServerLevel level) {
        IdentifierInventory beeInv = new IdentifierInventory(beeEntity, (Bee) targetEntity);
        return getBreedingRecipes(beeInv, level);
    }


    public static List<RecipeHolder<BeeBreedingRecipe>> getBreedingRecipes(IdentifierInventory beeInv, ServerLevel level) {
        List<RecipeHolder<BeeBreedingRecipe>> recipes = new ArrayList<>();
        // Get breeding recipes
        List<RecipeHolder<BeeBreedingRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        for (RecipeHolder<BeeBreedingRecipe> entry : allRecipes) {
            if (entry.value().matches(beeInv, level)) {
                recipes.add(entry);
            }
        }

        // If the two bees are the same, add a runtime breeding recipe
        ResourceLocation bee1Id = ResourceLocation.parse(beeInv.getIdentifier(0));
        var bee1Data = BeeReloadListener.INSTANCE.getData(bee1Id.toString());
        if (bee1Id.toString().equals(beeInv.getIdentifier(1)) && (!(bee1Id.getNamespace().equals(ProductiveBees.MODID)) || bee1Data == null || bee1Data.getBoolean("selfbreed"))) {
            Supplier<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeInv.getIdentifier()));
            recipes.add(new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_breeding_" + ResourceLocation.parse(beeInv.getIdentifier()).getPath() + "_self"), new BeeBreedingRecipe(beeIngredient, beeIngredient, beeIngredient)));
        }

        return recipes;
    }

    public static RecipeHolder<BlockConversionRecipe> getBlockConversionRecipe(Bee beeEntity, BlockState flowerBlockState) {
        List<RecipeHolder<BlockConversionRecipe>> recipes = new ArrayList<>();
        BlockStateInventory beeInv = new BlockStateInventory(beeEntity, flowerBlockState);
        String cacheKey = beeInv.getIdentifier(0) + beeInv.getIdentifier(1);
        if (blockConversionRecipeMap.containsKey(cacheKey)) {
            recipes = blockConversionRecipeMap.get(cacheKey);
        } else if (beeEntity.level() instanceof ServerLevel) {
            // Get block conversion recipes
            List<RecipeHolder<BlockConversionRecipe>> allRecipes = beeEntity.level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
            for (RecipeHolder<BlockConversionRecipe> recipe : allRecipes) {
                if (recipe.value().matches(beeInv, beeEntity.level())) {
                    recipes.add(recipe);
                }
            }

            blockConversionRecipeMap.put(cacheKey, recipes);
        }

        if (!recipes.isEmpty()) {
            return recipes.get(beeEntity.level().random.nextInt(recipes.size()));
        }
        return null;
    }

    public static boolean hasBlockConversionRecipe(Bee beeEntity, BlockState flowerBlockState) {
        return getBlockConversionRecipe(beeEntity, flowerBlockState) != null;
    }

    public static RecipeHolder<ItemConversionRecipe> getItemConversionRecipe(Bee beeEntity, ItemStack item) {
        List<RecipeHolder<ItemConversionRecipe>> recipes = new ArrayList<>();
        ItemInventory beeInv = new ItemInventory(beeEntity, item);
        String cacheKey = beeInv.getIdentifier(0) + beeInv.getIdentifier(1);
        if (itemConversionRecipeMap.containsKey(cacheKey)) {
            recipes = itemConversionRecipeMap.get(cacheKey);
        } else if (beeEntity.level() instanceof ServerLevel) {
            // Get item conversion recipes
            List<RecipeHolder<ItemConversionRecipe>> allRecipes = beeEntity.level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.ITEM_CONVERSION_TYPE.get());
            for (RecipeHolder<ItemConversionRecipe> recipe : allRecipes) {
                if (recipe.value().matches(beeInv, beeEntity.level())) {
                    recipes.add(recipe);
                }
            }

            itemConversionRecipeMap.put(cacheKey, recipes);
        }

        if (!recipes.isEmpty()) {
            return recipes.get(beeEntity.level().random.nextInt(recipes.size()));
        }
        return null;
    }

    public static boolean hasItemConversionRecipe(Bee beeEntity, ItemStack item) {
        return getItemConversionRecipe(beeEntity, item) != null;
    }

    public static RecipeHolder<BeeNBTChangerRecipe> getNBTChangerRecipe(Bee beeEntity, ItemStack item) {
        List<RecipeHolder<BeeNBTChangerRecipe>> recipes = new ArrayList<>();
        var inv = new ItemInventory(beeEntity, item);
        String cacheKey = inv.getIdentifier(0) + inv.getIdentifier(1);
        if (nbtChangerRecipeMap.containsKey(cacheKey)) {
            recipes = nbtChangerRecipeMap.get(cacheKey);
        } else if (beeEntity.level() instanceof ServerLevel) {
            List<RecipeHolder<BeeNBTChangerRecipe>> allRecipes = beeEntity.level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.BEE_NBT_CHANGER_TYPE.get());
            for (RecipeHolder<BeeNBTChangerRecipe> recipe : allRecipes) {
                if (recipe.value().matches(inv, beeEntity.level())) {
                    recipes.add(recipe);
                }
            }

            nbtChangerRecipeMap.put(cacheKey, recipes);
        }
        if (!recipes.isEmpty()) {
            return recipes.get(beeEntity.level().random.nextInt(recipes.size()));
        }
        return null;
    }

    public static boolean hasNBTChangerRecipe(Bee beeEntity, ItemStack item) {
        return getNBTChangerRecipe(beeEntity, item) != null;
    }

    public static List<ItemStack> getBeeProduce(Level level, Bee beeEntity, boolean hasCombBlockUpgrade, double modifier) {
        RecipeHolder<AdvancedBeehiveRecipe> matchedRecipe = null;
        BlockPos flowerPos = beeEntity.getSavedFlowerPos();
        List<ItemStack> outputList = new ArrayList<>();
        String beeId = beeEntity.getEncodeId();
        if (beeId == null ) {
            return outputList;
        }

        if (beeEntity instanceof ConfigurableBee) {
            beeId = ((ConfigurableBee) beeEntity).getBeeType().toString();
        }

        List<RecipeHolder<AdvancedBeehiveRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        var beeInv = new IdentifierInventory(beeId);
        for (RecipeHolder<AdvancedBeehiveRecipe> recipe : allRecipes) {
            if (recipe.value().matches(beeInv, level)) {
                matchedRecipe = recipe;
            }
        }

        int rolls = Math.max(1, (int) modifier);

        if (matchedRecipe != null) {
            matchedRecipe.value().getRecipeOutputs().forEach((itemStack, chancedOutput) -> {
                for (var i = 0; i < rolls; i++) {
                    if (level.random.nextFloat() <= chancedOutput.chance()) {
                        ItemStack stack = itemStack.copy();
                        int count = Mth.nextInt(level.random, Mth.floor(chancedOutput.min()), Mth.floor(chancedOutput.max()));
                        if (hasCombBlockUpgrade && itemStack.getItem() instanceof HoneycombItem) {
                            stack = getCombBlockFromHoneyComb(itemStack);
                        }
                        stack.setCount(count);
                        outputList.add(stack);
                    }
                }
            });
        } else if (beeId.equals("productivebees:lumber_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlockFromTag(level, flowerPos, ModTags.LUMBER, (ProductiveBee) beeEntity);
                if (flowerBlock != null && !flowerBlock.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                    outputList.add(new ItemStack(flowerBlock.asItem(), rolls));
                }
            }
        } else if (beeId.equals("productivebees:quarry_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlockFromTag(level, flowerPos, ModTags.QUARRY, (ProductiveBee) beeEntity);
                if (flowerBlock != null && !flowerBlock.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                    outputList.add(new ItemStack(flowerBlock.asItem(), rolls));
                }
            }
        } else if (beeId.equals("productivebees:dye_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlockFromTag(level, flowerPos, BlockTags.FLOWERS, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    Item flowerItem = flowerBlock.asItem();

                    ItemStack dye = getRecipeOutputFromInput(level, flowerItem);
                    if (!dye.isEmpty()) {
                        dye.setCount(rolls);
                        outputList.add(dye);
                    }
                }
            }
        } else if (beeId.equals("productivebees:wanna")) {
            if (flowerPos != null && level instanceof ServerLevel serverLevel) {
                Entity entity = null;
                var blockEntity = level.getBlockEntity(flowerPos);
                if (blockEntity instanceof AmberBlockEntity amberBlockEntity) {
                    entity = amberBlockEntity.getCachedEntity();
                } else if (blockEntity instanceof FeederBlockEntity feederBlockEntity) {
                    ItemStack amberItem = feederBlockEntity.getSpecificItemFromInventory(ModBlocks.AMBER.get().asItem(), level.random);
                    var tag = amberItem.get(DataComponents.ENTITY_DATA);
                    if(tag != null && tag.contains("EntityData")) {
                        entity = AmberBlockEntity.createEntity(serverLevel, tag.copyTag().getCompound("EntityData"));
                    }
                }

                if (entity instanceof Mob mob) {
                    LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(mob.getLootTable());
                    if (!lootTable.equals(LootTable.EMPTY)) {
                        Player fakePlayer = FakePlayerFactory.get(serverLevel, new GameProfile(ModEntities.WANNA_BEE_UUID, "wanna_bee"));
                        LootParams.Builder lootContextBuilder = new LootParams.Builder(serverLevel);
                        lootContextBuilder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer);
                        lootContextBuilder.withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic());
                        lootContextBuilder.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, fakePlayer);
                        lootContextBuilder.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, fakePlayer);
                        lootContextBuilder.withParameter(LootContextParams.THIS_ENTITY, entity);
                        lootContextBuilder.withParameter(LootContextParams.ORIGIN, new Vec3(flowerPos.getX(), flowerPos.getY(), flowerPos.getZ()));

                        List<ItemStack> list = lootTable.getRandomItems(lootContextBuilder.create(LootContextParamSets.ENTITY)).stream().filter(itemStack -> !itemStack.is(ModTags.WANNABEE_LOOT_BLACKLIST)).toList();
                        if (list.size() > 0) {
                            for (var i = 0; i < rolls; i++) {
                                outputList.add(list.get(level.random.nextInt(list.size())));
                            }
                        }
                    }
                }
            }
        }

        return outputList;
    }

    public static ItemStack getCombBlockFromHoneyComb(ItemStack itemStack) {
        ItemStack stack = ItemStack.EMPTY;
        if (itemStack.getItem() instanceof Honeycomb) {
            stack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
            stack.set(ModDataComponents.BEE_TYPE, itemStack.get(ModDataComponents.BEE_TYPE));
        } else {
            stack = switch (BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString()) {
                case "productivebees:honeycomb_ghostly" -> new ItemStack(ModBlocks.COMB_GHOSTLY.get());
                case "productivebees:honeycomb_milky" -> new ItemStack(ModBlocks.COMB_MILKY.get());
                case "productivebees:honeycomb_powdery" -> new ItemStack(ModBlocks.COMB_POWDERY.get());
                case "minecraft:honeycomb" -> new ItemStack(Blocks.HONEYCOMB_BLOCK);
                default -> stack;
            };
        }
        return stack;
    }

    public static ItemStack getRecipeOutputFromInput(Level level, Item input) {
        List<RecipeHolder<CraftingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        for (RecipeHolder<CraftingRecipe> recipe : recipes) {
            List<Ingredient> ingredients = recipe.value().getIngredients();
            if (ingredients.size() == 1) {
                Ingredient ingredient = ingredients.get(0);
                ItemStack[] stacks = ingredient.getItems();
                if (stacks.length > 0 && stacks[0].getItem().equals(input)) {
                    return recipe.value().getResultItem(level.registryAccess()).copy();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public static RecipeHolder<CentrifugeRecipe> getCentrifugeRecipe(Level level, InventoryHandlerHelper.BlockEntityItemStackHandler inputHandler) {
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.CENTRIFUGE_TYPE.get(), inputHandler, level).orElse(null);
    }

    private static Block getFloweringBlockFromTag(Level level, BlockPos flowerPos, TagKey<Block> tag, ProductiveBee bee) {
        BlockState flowerBlockState = level.getBlockState(flowerPos);
        Block flowerBlock = flowerBlockState.getBlock();
        if (flowerBlock instanceof Feeder) {
            BlockEntity feederTile = level.getBlockEntity(flowerPos);
            if (feederTile instanceof FeederBlockEntity feederBlockEntity && ProductiveBee.isValidFeeder(bee, feederTile, bee::isFlowerBlock, bee::isFlowerItem)) {
                return feederBlockEntity.getRandomBlockFromInventory(tag, level.random);
            }
        }
        return flowerBlockState.is(tag) ? flowerBlock : null;
    }

    public static void encaseMob(Mob target, Level level, Direction direction) {
        // Encase mob
        if (target != null && !target.getType().is(ModTags.BEE_ENCASE_BLACKLIST) && target.isAlive() && !target.isRemoved()) {
            if (target instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
                return;
            }
            if (level.isEmptyBlock(target.blockPosition()) && !target.isRemoved()) {
                level.setBlockAndUpdate(target.blockPosition(), ModBlocks.AMBER.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));
                if (level.getBlockEntity(target.blockPosition()) instanceof AmberBlockEntity amberBlockEntity) {
                    amberBlockEntity.setEntity(target);
                    target.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
    }

    public static void setOffspringAttributes(ProductiveBee newBee, ProductiveBee parent1, AgeableMob parent2) {
        Map<GeneAttribute, GeneValue> attributeMapParent1 = parent1.getBeeAttributes();
        Map<GeneAttribute, GeneValue> attributeMapParent2 = parent2.getData(ProductiveBees.ATTRIBUTE_HANDLER).getAttributes();
        Map<GeneAttribute, GeneValue> attributeMapChild = newBee.getBeeAttributes();

        attributeMapChild.forEach((geneAttribute, geneValue) -> {
            ProductiveBees.LOGGER.info("attributeMapChild: " + geneAttribute.getSerializedName() + " " + geneValue);
        });

        int parentProductivity = Mth.nextInt(newBee.level().random,
                attributeMapParent1.get(GeneAttribute.PRODUCTIVITY).getValue(),
                attributeMapParent2.get(GeneAttribute.PRODUCTIVITY).getValue());
        newBee.setAttributeValue(GeneAttribute.PRODUCTIVITY, GeneValue.productivity(Math.max(attributeMapChild.get(GeneAttribute.PRODUCTIVITY).getValue(), parentProductivity)));

        int parentEndurance = Mth.nextInt(newBee.level().random,
                attributeMapParent1.get(GeneAttribute.ENDURANCE).getValue(),
                attributeMapParent2.get(GeneAttribute.ENDURANCE).getValue());
        newBee.setAttributeValue(GeneAttribute.ENDURANCE, GeneValue.endurance(Math.max(attributeMapChild.get(GeneAttribute.ENDURANCE).getValue(), parentEndurance)));

        int parentTemper = Mth.nextInt(newBee.level().random, attributeMapParent1.get(GeneAttribute.TEMPER).getValue(), attributeMapParent2.get(GeneAttribute.TEMPER).getValue());
        newBee.setAttributeValue(GeneAttribute.TEMPER, GeneValue.temper(Math.min(attributeMapChild.get(GeneAttribute.TEMPER).getValue(), parentTemper)));

        int parentBehavior = Mth.nextInt(newBee.level().random, attributeMapParent1.get(GeneAttribute.BEHAVIOR).getValue(), attributeMapParent2.get(GeneAttribute.BEHAVIOR).getValue());
        newBee.setAttributeValue(GeneAttribute.BEHAVIOR, GeneValue.behavior(Math.max(attributeMapChild.get(GeneAttribute.BEHAVIOR).getValue(), parentBehavior)));

        int parentWeatherTolerance = Mth.nextInt(newBee.level().random, attributeMapParent1.get(GeneAttribute.WEATHER_TOLERANCE).getValue(), attributeMapParent2.get(GeneAttribute.WEATHER_TOLERANCE).getValue());
        newBee.setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.weatherTolerance(Math.max(attributeMapChild.get(GeneAttribute.WEATHER_TOLERANCE).getValue(), parentWeatherTolerance)));
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
        return populateBeeInfoFromTag(tag, list, false);
    }

    public static List<Component> populateBeeInfoFromTag(CompoundTag tag, @Nullable List<Component> list, boolean minified) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(Component.translatable(tag.getInt("Age") < 0 ? "productivebees.information.age.child" : "productivebees.information.age.adult").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));

        if (tag.contains(AttachmentHolder.ATTACHMENTS_NBT_KEY)) {
            if (!minified) {
                float current = tag.getFloat("Health");
                float max = tag.contains("MaxHealth") ? tag.getFloat("MaxHealth") : 10.0f;
                list.add((Component.translatable("productivebees.information.attribute.health", current, max)).withStyle(ChatFormatting.DARK_GRAY));
            }

            String type = "hive";
            if (tag.contains("bee_type")) {
                type = tag.getString("bee_type");
            }
            Component type_value = Component.translatable("productivebees.information.attribute.type." + type).withStyle(ColorUtil.getBeeTypeColor(type));
            list.add((Component.translatable("productivebees.information.attribute.type", type_value)).withStyle(ChatFormatting.DARK_GRAY));

            var attachments = tag.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY);
            if (attachments.contains("productivebees:attributes_handler")) {
                var attributeTag = attachments.getCompound("productivebees:attributes_handler");

                var productivity = GeneValue.byName(attributeTag.getString("bee_productivity"));
                Component productivity_value = Component.translatable("productivebees.information.attribute." + productivity.getSerializedName()).withStyle(ColorUtil.getAttributeColor(productivity));
                list.add((Component.translatable("productivebees.information.attribute.productivity", productivity_value)).withStyle(ChatFormatting.DARK_GRAY));

                var tolerance = GeneValue.byName(attributeTag.getString("bee_weather_tolerance"));
                Component tolerance_value = Component.translatable("productivebees.information.attribute." + tolerance.getSerializedName()).withStyle(ColorUtil.getAttributeColor(tolerance));
                list.add((Component.translatable("productivebees.information.attribute.weather_tolerance", tolerance_value)).withStyle(ChatFormatting.DARK_GRAY));

                var behavior = GeneValue.byName(attributeTag.getString("bee_behavior"));
                Component behavior_value = Component.translatable("productivebees.information.attribute." + behavior.getSerializedName()).withStyle(ColorUtil.getAttributeColor(behavior));
                list.add((Component.translatable("productivebees.information.attribute.behavior", behavior_value)).withStyle(ChatFormatting.DARK_GRAY));

                var endurance = GeneValue.byName(attributeTag.getString("bee_endurance"));
                Component endurance_value = Component.translatable("productivebees.information.attribute." + endurance.getSerializedName()).withStyle(ColorUtil.getAttributeColor(endurance));
                list.add((Component.translatable("productivebees.information.attribute.endurance", endurance_value)).withStyle(ChatFormatting.DARK_GRAY));

                var temper = GeneValue.byName(attributeTag.getString("bee_temper"));
                Component temper_value = Component.translatable("productivebees.information.attribute." + temper.getSerializedName()).withStyle(ColorUtil.getAttributeColor(temper));
                list.add((Component.translatable("productivebees.information.attribute.temper", temper_value)).withStyle(ChatFormatting.DARK_GRAY));
            }

            if (!minified) {
                if (tag.contains("type")) {
                    CompoundTag beeData = BeeReloadListener.INSTANCE.getData(ResourceLocation.parse(tag.getString("type")));
                    MutableComponent breedingItemText = Component.translatable("productivebees.information.breeding_item_default");
                    if (beeData != null && beeData.contains("breedingItem") && !beeData.getString("breedingItem").isEmpty()) {
                        Item breedingItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(beeData.getString("breedingItem")));
                        breedingItemText = Component.literal(beeData.getInt("breedingItemCount") + " " + Component.translatable(breedingItem.getDescriptionId()).getString());
                    }
                    list.add(Component.translatable("productivebees.information.breeding_item", breedingItemText.withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
                    if (beeData != null && !beeData.getBoolean("selfbreed")) {
                        list.add(Component.translatable("productivebees.information.selfbreed_disabled").withStyle(ChatFormatting.GRAY));
                    }
                }

                if (tag.contains("HivePos")) {
                    BlockPos hivePos = NbtUtils.readBlockPos(tag, "HivePos").orElse(BlockPos.ZERO);
                    list.add(Component.translatable("productivebees.information.home_position", hivePos.getX(), hivePos.getY(), hivePos.getZ()));
                }
            }
        }

        return list;
    }

    public static class IdentifierInventory implements RecipeInput
    {
        private final List<String> identifiers = new ArrayList<>();

        public IdentifierInventory(String identifier) {
            this.identifiers.add(identifier);
        }

        public IdentifierInventory(Bee bee1, Bee bee2) {
            String identifier1 = BuiltInRegistries.ENTITY_TYPE.getKey(bee1.getType()).toString();
            if (bee1 instanceof ConfigurableBee) {
                identifier1 = ((ConfigurableBee) bee1).getBeeType().toString();
            }
            String identifier2 = BuiltInRegistries.ENTITY_TYPE.getKey(bee2.getType()).toString();
            if (bee2 instanceof ConfigurableBee) {
                identifier2 = ((ConfigurableBee) bee2).getBeeType().toString();
            }
            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public IdentifierInventory(Bee bee1, String identifier2) {
            String identifier1 = BuiltInRegistries.ENTITY_TYPE.getKey(bee1.getType()).toString();
            if (bee1 instanceof ConfigurableBee) {
                identifier1 = ((ConfigurableBee) bee1).getBeeType().toString();
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
        public int size() {
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

    public static class ItemInventory extends IdentifierInventory {
        private ItemStack input;

        public ItemInventory(Bee bee1, ItemStack input) {
            super(bee1, input.toString());
            this.input = input;
        }

        public ItemStack getInput() {
            return input;
        }
    }
}
