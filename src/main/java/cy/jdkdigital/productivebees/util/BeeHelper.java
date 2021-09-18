package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.StoneChip;
import cy.jdkdigital.productivebees.common.item.WoodChip;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.recipe.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BeeHelper
{
    public static Entity itemInteract(Bee entity, ItemStack itemStack, ServerLevel world, CompoundTag nbt, Player player) {
        Entity bee = null;

        if (!entity.isBaby()) {
            Container beeInv = new IdentifierInventory(entity, itemStack.getItem().getRegistryName() + "");

            List<BeeConversionRecipe> recipes = new ArrayList<>();

            // Conversion recipes
            Map<ResourceLocation, Recipe<Container>> allRecipes = world.getRecipeManager().byType(BeeConversionRecipe.BEE_CONVERSION);
            for (Map.Entry<ResourceLocation, Recipe<Container>> entry : allRecipes.entrySet()) {
                BeeConversionRecipe recipe = (BeeConversionRecipe) entry.getValue();
                if (recipe.matches(beeInv, world)) {
                    recipes.add(recipe);
                }
            }

            if (!recipes.isEmpty()) {
                BeeConversionRecipe recipe = recipes.get(ProductiveBees.rand.nextInt(recipes.size()));
                if (ProductiveBees.rand.nextInt(100) < recipe.chance) {
                    bee = recipe.result.get().getBeeEntity().create(world);
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
            Map<Lazy<BeeIngredient>, Integer> possibleOffspring = recipe.offspring;
            if (possibleOffspring != null && possibleOffspring.size() > 0) {
                // Get weighted offspring chance
                int maxWeight = 0;
                for (Map.Entry<Lazy<BeeIngredient>, Integer> entry : possibleOffspring.entrySet()) {
                    maxWeight = maxWeight + entry.getValue();
                }

                BeeIngredient beeIngredient = null;

                int i = ProductiveBees.rand.nextInt(maxWeight);
                int currentWeight = 0;
                for (Map.Entry<Lazy<BeeIngredient>, Integer> entry : possibleOffspring.entrySet()) {
                    currentWeight = currentWeight + entry.getValue();
                    if (i < currentWeight) {
                        beeIngredient = entry.getKey().get();
                    }
                }

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
            return ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeEntity.getEncodeId())).create(world);
        }

        return null;
    }

    public static BeeBreedingRecipe getRandomBreedingRecipe(Bee beeEntity, AgeableMob targetEntity, ServerLevel world) {
        Container beeInv = new IdentifierInventory(beeEntity, (Bee) targetEntity);

        // Get breeding recipes
        List<BeeBreedingRecipe> recipes = new ArrayList<>();
        Map<ResourceLocation, Recipe<Container>> allRecipes = world.getRecipeManager().byType(BeeBreedingRecipe.BEE_BREEDING);
        for (Map.Entry<ResourceLocation, Recipe<Container>> entry : allRecipes.entrySet()) {
            BeeBreedingRecipe recipe = (BeeBreedingRecipe) entry.getValue();
            if (recipe.matches(beeInv, world)) {
                recipes.add(recipe);
            }
        }

        if (!recipes.isEmpty()) {
            return recipes.get(ProductiveBees.rand.nextInt(recipes.size()));
        }

        return null;
    }

    public static BlockConversionRecipe getRandomBlockConversionRecipe(Bee beeEntity) {
        if (beeEntity.savedFlowerPos != null && beeEntity.level instanceof ServerLevel) {
            Container beeInv = new BlockStateInventory(beeEntity, beeEntity.level.getBlockState(beeEntity.savedFlowerPos));

            // Get breeding recipes
            List<BlockConversionRecipe> recipes = new ArrayList<>();
            Map<ResourceLocation, Recipe<Container>> allRecipes = beeEntity.level.getRecipeManager().byType(BlockConversionRecipe.BLOCK_CONVERSION);
            for (Map.Entry<ResourceLocation, Recipe<Container>> entry : allRecipes.entrySet()) {
                BlockConversionRecipe recipe = (BlockConversionRecipe) entry.getValue();
                if (recipe.matches(beeInv, beeEntity.level)) {
                    recipes.add(recipe);
                }
            }

            if (!recipes.isEmpty()) {
                return recipes.get(ProductiveBees.rand.nextInt(recipes.size()));
            }
        }

        return null;
    }

    public static List<ItemStack> getBeeProduce(Level world, Bee beeEntity, boolean hasCombBlockUpgrade) {
        AdvancedBeehiveRecipe matchedRecipe = null;
        BlockPos flowerPos = beeEntity.getSavedFlowerPos();

        String beeId = beeEntity.getEncodeId();
        if (beeEntity instanceof ConfigurableBee) {
            beeId = ((ConfigurableBee) beeEntity).getBeeType();
        }

        Map<ResourceLocation, Recipe<Container>> allRecipes = world.getRecipeManager().byType(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE);
        Container beeInv = new IdentifierInventory(beeId);
        for (Map.Entry<ResourceLocation, Recipe<Container>> entry : allRecipes.entrySet()) {
            AdvancedBeehiveRecipe recipe = (AdvancedBeehiveRecipe) entry.getValue();
            if (recipe.matches(beeInv, world)) {
                matchedRecipe = recipe;
            }
        }

        List<ItemStack> outputList = new ArrayList<>();
        if (matchedRecipe != null) {
            matchedRecipe.getRecipeOutputs().forEach((itemStack, bounds) -> {
                if (ProductiveBees.rand.nextInt(100) <= bounds.get(2).getAsInt()) {
                    int count = Mth.nextInt(ProductiveBees.rand, Mth.floor(bounds.get(0).getAsInt()), Mth.floor(bounds.get(1).getAsInt()));
                    ItemStack stack = itemStack.copy();
                    stack.setCount(count);
                    if (hasCombBlockUpgrade) {
                        stack = convertToCombBlock(stack);
                    }
                    outputList.add(stack);
                }
            });
        } else if (beeId.equals("productivebees:lumber_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(world, flowerPos, BlockTags.LOGS, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    ItemStack woodChip;
                    if (hasCombBlockUpgrade) {
                        woodChip = new ItemStack(flowerBlock.asItem());
                    } else {
                        woodChip = WoodChip.getStack(flowerBlock, world.random.nextInt(6) + 1);
                    }
                    outputList.add(woodChip);
                }
            }
        } else if (beeId.equals("productivebees:quarry_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(world, flowerPos, ModTags.QUARRY, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    ItemStack stoneChip;
                    if (hasCombBlockUpgrade) {
                        stoneChip = new ItemStack(flowerBlock.asItem());
                    } else {
                        stoneChip = StoneChip.getStack(flowerBlock, world.random.nextInt(6) + 1);
                    }
                    outputList.add(stoneChip);
                }
            }
        } else if (beeId.equals("productivebees:dye_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(world, flowerPos, BlockTags.FLOWERS, (ProductiveBee) beeEntity);
                if (flowerBlock != null) {
                    Item flowerItem = flowerBlock.asItem();

                    Map<ResourceLocation, Recipe<CraftingContainer>> recipes = world.getRecipeManager().byType(RecipeType.CRAFTING);
                    Optional<Recipe<CraftingContainer>> flowerRecipe = recipes.values().stream().flatMap((craftingRecipe) -> {
                        AtomicBoolean hasMatchingItem = new AtomicBoolean(false);
                        List<Ingredient> ingredients = craftingRecipe.getIngredients();
                        if (ingredients.size() == 1) {
                            Ingredient ingredient = ingredients.get(0);
                            ItemStack[] stacks = ingredient.getItems();
                            if (stacks.length > 0 && stacks[0].getItem().equals(flowerItem)) {
                                hasMatchingItem.set(true);
                            }
                        }
                        return Util.toStream(hasMatchingItem.get() ? Optional.of(craftingRecipe) : Optional.empty());
                    }).findFirst();

                    flowerRecipe.ifPresent(craftingInventoryIRecipe -> {
                        ItemStack dye = new ItemStack(craftingInventoryIRecipe.getResultItem().getItem(), 1);
                        outputList.add(dye);
                    });
                }
            }
        }

        return outputList;
    }

    @Nullable
    public static CentrifugeRecipe getCentrifugeRecipe(RecipeManager recipeManager, IItemHandlerModifiable inputHandler) {
        Level world = ProductiveBees.proxy.getWorld();

        if (world == null) {
            return null;
        }

        return recipeManager.getRecipeFor(CentrifugeRecipe.CENTRIFUGE, new RecipeWrapper(inputHandler), world).orElse(null);
    }

    private static ItemStack convertToCombBlock(ItemStack stack) {
        // Change to comb block
        ItemStack newStack = null;
        if (stack.getItem().equals(Items.HONEYCOMB)) {
            newStack = new ItemStack(Items.HONEYCOMB_BLOCK, stack.getCount());
        }
        else if (stack.getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
            newStack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get(), stack.getCount());
            newStack.setTag(stack.getTag());
        }
        else {
            ResourceLocation rl = stack.getItem().getRegistryName();
            Item newItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(rl.getNamespace(), rl.getPath().replace("honey", ""))); // honeycomb_glowing -> comb_glowing
            if (newItem != Items.AIR) {
                newStack = new ItemStack(newItem, stack.getCount());
            }
        }
        if (newStack != null) {
            stack = newStack;
        }

        return stack;
    }

    private static Block getFloweringBlock(Level world, BlockPos flowerPos, Tag<Block> tag, ProductiveBee bee) {
        BlockState flowerBlockState = world.getBlockState(flowerPos);
        Block flowerBlock = flowerBlockState.getBlock();
        if (!tag.contains(flowerBlock)) {
            return null;
        }
        if (flowerBlock instanceof Feeder) {
            BlockEntity feederTile = world.getBlockEntity(flowerPos);
            if (feederTile instanceof FeederBlockEntity && ProductiveBee.isValidFeeder(feederTile, bee::isFlowerBlock)) {
                return ((FeederBlockEntity) feederTile).getRandomBlockFromInventory(tag);
            }
        }
        return flowerBlock;
    }

    public static void setOffspringAttributes(ProductiveBee newBee, ProductiveBee productiveBeeEntity, AgeableMob targetEntity) {
        Map<BeeAttribute<?>, Object> attributeMapParent1 = productiveBeeEntity.getBeeAttributes();
        Map<BeeAttribute<?>, Object> attributeMapParent2 = new HashMap<>();
        if (targetEntity instanceof ProductiveBee) {
            attributeMapParent2 = ((ProductiveBee) targetEntity).getBeeAttributes();
        }
        else {
            // Default bee attributes
            attributeMapParent2.put(BeeAttributes.PRODUCTIVITY, 0);
            attributeMapParent2.put(BeeAttributes.ENDURANCE, 0);
            attributeMapParent2.put(BeeAttributes.TEMPER, 1);
            attributeMapParent2.put(BeeAttributes.BEHAVIOR, 0);
            attributeMapParent2.put(BeeAttributes.WEATHER_TOLERANCE, 0);
        }

        Map<BeeAttribute<?>, Object> attributeMapChild = newBee.getBeeAttributes();

        int parentProductivity = Mth.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.PRODUCTIVITY), (int) attributeMapParent2.get(BeeAttributes.PRODUCTIVITY));
        attributeMapChild.put(BeeAttributes.PRODUCTIVITY, Math.max((int) attributeMapChild.get(BeeAttributes.PRODUCTIVITY), parentProductivity));

        int parentEndurance = Mth.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.ENDURANCE), (int) attributeMapParent2.get(BeeAttributes.ENDURANCE));
        attributeMapChild.put(BeeAttributes.ENDURANCE, Math.max((int) attributeMapChild.get(BeeAttributes.ENDURANCE), parentEndurance));

        int parentTemper = Mth.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.TEMPER), (int) attributeMapParent2.get(BeeAttributes.TEMPER));
        attributeMapChild.put(BeeAttributes.TEMPER, Math.max((int) attributeMapChild.get(BeeAttributes.TEMPER), parentTemper));

        int parentBehavior = Mth.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.BEHAVIOR), (int) attributeMapParent2.get(BeeAttributes.BEHAVIOR));
        attributeMapChild.put(BeeAttributes.BEHAVIOR, Math.max((int) attributeMapChild.get(BeeAttributes.BEHAVIOR), parentBehavior));

        int parentWeatherTolerance = Mth.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.WEATHER_TOLERANCE), (int) attributeMapParent2.get(BeeAttributes.WEATHER_TOLERANCE));
        attributeMapChild.put(BeeAttributes.WEATHER_TOLERANCE, Math.max((int) attributeMapChild.get(BeeAttributes.WEATHER_TOLERANCE), parentWeatherTolerance));
    }

    public static List<Component> populateBeeInfoFromTag(CompoundTag tag, @Nullable List<Component> list) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(new TranslatableComponent(tag.getInt("Age") < 0 ? "productivebees.information.age.child" : "productivebees.information.age.adult").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));

        if (tag.getBoolean("isProductiveBee")) {
            float current = tag.getFloat("Health");
            float max = tag.contains("MaxHealth") ? tag.getFloat("MaxHealth") : 10.0f;
            list.add((new TranslatableComponent("productivebees.information.attribute.health", current, max)).withStyle(ChatFormatting.DARK_GRAY));

            String type = tag.getString("bee_type");
            Component type_value = new TranslatableComponent("productivebees.information.attribute.type." + type).withStyle(ColorUtil.getColor(type));
            list.add((new TranslatableComponent("productivebees.information.attribute.type", type_value)).withStyle(ChatFormatting.DARK_GRAY));

            int productivity = tag.getInt("bee_productivity");
            Component productivity_value = new TranslatableComponent(BeeAttributes.keyMap.get(BeeAttributes.PRODUCTIVITY).get(productivity)).withStyle(ColorUtil.getColor(productivity));
            list.add((new TranslatableComponent("productivebees.information.attribute.productivity", productivity_value)).withStyle(ChatFormatting.DARK_GRAY));

            int tolerance = tag.getInt("bee_weather_tolerance");
            Component tolerance_value = new TranslatableComponent(BeeAttributes.keyMap.get(BeeAttributes.WEATHER_TOLERANCE).get(tolerance)).withStyle(ColorUtil.getColor(tolerance));
            list.add((new TranslatableComponent("productivebees.information.attribute.weather_tolerance", tolerance_value)).withStyle(ChatFormatting.DARK_GRAY));

            int behavior = tag.getInt("bee_behavior");
            Component behavior_value = new TranslatableComponent(BeeAttributes.keyMap.get(BeeAttributes.BEHAVIOR).get(behavior)).withStyle(ColorUtil.getColor(behavior));
            list.add((new TranslatableComponent("productivebees.information.attribute.behavior", behavior_value)).withStyle(ChatFormatting.DARK_GRAY));

            int endurance = tag.getInt("bee_endurance");
            Component endurance_value = new TranslatableComponent(BeeAttributes.keyMap.get(BeeAttributes.ENDURANCE).get(endurance)).withStyle(ColorUtil.getColor(endurance));
            list.add((new TranslatableComponent("productivebees.information.attribute.endurance", endurance_value)).withStyle(ChatFormatting.DARK_GRAY));

            int temper = tag.getInt("bee_temper");
            Component temper_value = new TranslatableComponent(BeeAttributes.keyMap.get(BeeAttributes.TEMPER).get(temper)).withStyle(ColorUtil.getColor(temper));
            list.add((new TranslatableComponent("productivebees.information.attribute.temper", temper_value)).withStyle(ChatFormatting.DARK_GRAY));

            if (tag.contains("HivePos")) {
                BlockPos hivePos = NbtUtils.readBlockPos(tag.getCompound("HivePos"));
                list.add(new TextComponent("Home position: " + hivePos.getX() + ", " + hivePos.getY() + ", " + hivePos.getZ()));
            }
        } else {
            list.add((new TextComponent("Mod: " + tag.getString("mod"))).withStyle(ChatFormatting.DARK_AQUA));
        }

        return list;
    }

    public static class IdentifierInventory implements Container
    {
        private List<String> identifiers = new ArrayList<>();

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
            super(bee1, "");
            this.state = state;
        }

        public BlockState getState() {
            return state;
        }
    }
}
