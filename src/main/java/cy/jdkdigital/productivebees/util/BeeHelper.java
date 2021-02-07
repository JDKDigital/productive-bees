package cy.jdkdigital.productivebees.util;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.common.item.StoneChip;
import cy.jdkdigital.productivebees.common.item.WoodChip;
import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.integrations.resourcefulbees.ResourcefulBeesCompat;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import cy.jdkdigital.productivebees.recipe.BeeConversionRecipe;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BeeHelper
{
    public static BeeEntity itemInteract(BeeEntity entity, ItemStack itemStack, World world, CompoundNBT nbt, PlayerEntity player, Hand hand, Direction direction) {
        // Conversion recipes
        BeeEntity bee = null;

        if (!entity.isChild()) {
            IInventory beeInv = new IdentifierInventory(entity, itemStack.getItem().getRegistryName() + "");

            List<BeeConversionRecipe> recipes = new ArrayList<>();

            Map<ResourceLocation, IRecipe<IInventory>> allRecipes = world.getRecipeManager().getRecipes(BeeConversionRecipe.BEE_CONVERSION);
            for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
                BeeConversionRecipe recipe = (BeeConversionRecipe) entry.getValue();
                if (recipe.matches(beeInv, world)) {
                    recipes.add(recipe);
                }
            }

            if (!recipes.isEmpty()) {
                BeeConversionRecipe recipe = recipes.get(ProductiveBees.rand.nextInt(recipes.size()));
                if (ProductiveBees.rand.nextInt(100) < recipe.chance) {
                    bee = recipe.result.get().getBeeEntity().create(world);
                    if (bee instanceof ConfigurableBeeEntity) {
                        ((ConfigurableBeeEntity) bee).setBeeType(recipe.result.get().getBeeType().toString());
                        ((ConfigurableBeeEntity) bee).setAttributes();
                    }
                }
            }
        }

        if (bee != null) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            BeeHelper.prepareBeeSpawn(bee, entity.getPosition(), direction, entity.getGrowingAge());

            return bee;
        }
        return null;
    }

    public static void prepareBeeSpawn(BeeEntity bee, BlockPos pos, Direction direction, int age) {
        if (bee != null) {
            double x = (double) pos.getX() + (double) direction.getXOffset();
            double y = (double) pos.getY() + 0.5D - (double) (bee.getHeight() / 2.0F);
            double z = (double) pos.getZ() + (double) direction.getZOffset();
            bee.setLocationAndAngles(x, y, z, bee.rotationYaw, bee.rotationPitch);

            if (age > 0) {
                bee.setGrowingAge(age);
            }
        }
    }

    @Nullable
    public static BeeEntity getBreedingResult(BeeEntity beeEntity, AgeableEntity targetEntity, World world) {
        IInventory beeInv = new IdentifierInventory(beeEntity, (BeeEntity) targetEntity);

        // Get breeding recipes
        List<BeeBreedingRecipe> recipes = new ArrayList<>();

        Map<ResourceLocation, IRecipe<IInventory>> allRecipes = world.getRecipeManager().getRecipes(BeeBreedingRecipe.BEE_BREEDING);
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
            BeeBreedingRecipe recipe = (BeeBreedingRecipe) entry.getValue();
            if (recipe.matches(beeInv, world)) {
                recipes.add(recipe);
            }
        }

        if (!recipes.isEmpty()) {
            BeeBreedingRecipe recipe = recipes.get(ProductiveBees.rand.nextInt(recipes.size()));
            Map<Lazy<BeeIngredient>, Integer> possibleOffspring = recipe.offspring;
            if (possibleOffspring != null && possibleOffspring.size() > 0) {
                // Get weighted offspring chance
                int maxWeight = 0;
                for (Map.Entry<Lazy<BeeIngredient>, Integer> entry: possibleOffspring.entrySet()) {
                    maxWeight = maxWeight + entry.getValue();
                }

                BeeIngredient beeIngredient = null;

                int i = ProductiveBees.rand.nextInt(maxWeight);
                int currentWeight = 0;
                for (Map.Entry<Lazy<BeeIngredient>, Integer> entry: possibleOffspring.entrySet()) {
                    currentWeight = currentWeight + entry.getValue();
                    if (i < currentWeight) {
                        beeIngredient = entry.getKey().get();
                    }
                }

                if (beeIngredient != null) {
                    BeeEntity newBee = beeIngredient.getBeeEntity().create(world);
                    if (newBee instanceof ConfigurableBeeEntity) {
                        ((ConfigurableBeeEntity) newBee).setBeeType(beeIngredient.getBeeType().toString());
                        ((ConfigurableBeeEntity) newBee).setAttributes();
                    }
                    return newBee;
                }
            }
        }

        // Check if bee is configurable and make a new of same type
        if (beeEntity instanceof ConfigurableBeeEntity) {
            ResourceLocation type = new ResourceLocation(((ConfigurableBeeEntity) beeEntity).getBeeType());
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(type.toString());
            if (nbt != null) {
                ConfigurableBeeEntity newBee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                newBee.setBeeType(type.toString());
                newBee.setAttributes();
                return newBee;
            }
        }

        // If no specific recipe exist for the target bee or the bees are the same type, create a child like the parent
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(beeEntity.getEntityString());
        if (nbt != null && nbt.getBoolean("selfbreed")) {
            return (BeeEntity) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeEntity.getEntityString())).create(world);
        }
        return null;
    }

    public static List<ItemStack> getBeeProduce(World world, BeeEntity beeEntity, boolean hasCombBlockUpgrade) {
        AdvancedBeehiveRecipe matchedRecipe = null;
        BlockPos flowerPos = beeEntity.getFlowerPos();

        String beeId = beeEntity.getEntityString();
        if (beeEntity instanceof ConfigurableBeeEntity) {
            beeId = ((ConfigurableBeeEntity) beeEntity).getBeeType();
        }

        ResourceLocation id = new ResourceLocation(beeId);
        if (id.getNamespace().equals(ResourcefulBeesCompat.MODID)) {
            return Collections.singletonList(ResourcefulBeesCompat.getHoneyComb(id.getPath()));
        }

        Map<ResourceLocation, IRecipe<IInventory>> allRecipes = world.getRecipeManager().getRecipes(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE);
        IInventory beeInv = new IdentifierInventory(beeId);
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
            AdvancedBeehiveRecipe recipe = (AdvancedBeehiveRecipe) entry.getValue();
            if (recipe.matches(beeInv, world)) {
                matchedRecipe = recipe;
            }
        }

        List<ItemStack> outputList = Lists.newArrayList(ItemStack.EMPTY);
        if (matchedRecipe != null) {
            matchedRecipe.getRecipeOutputs().forEach((itemStack, bounds) -> {
                int count = MathHelper.nextInt(ProductiveBees.rand, MathHelper.floor(bounds.get(0).getInt()), MathHelper.floor(bounds.get(1).getInt()));
                ItemStack stack = itemStack.copy();
                stack.setCount(count);
                if (hasCombBlockUpgrade) {
                    stack = convertToCombBlock(stack);
                }
                outputList.add(stack);
            });
        }
        else if (beeId.equals("productivebees:lumber_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(world, flowerPos, BlockTags.LOGS);

                ItemStack woodChip;
                if (hasCombBlockUpgrade) {
                    woodChip = new ItemStack(flowerBlock.asItem());
                } else {
                    woodChip = WoodChip.getStack(flowerBlock, world.rand.nextInt(6) + 1);
                }
                outputList.add(woodChip);
            }
        }
        else if (beeId.equals("productivebees:quarry_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(world, flowerPos, ModTags.QUARRY);

                ItemStack stoneChip;
                if (hasCombBlockUpgrade) {
                    stoneChip = new ItemStack(flowerBlock.asItem());
                } else {
                    stoneChip = StoneChip.getStack(flowerBlock, world.rand.nextInt(6) + 1);
                }
                outputList.add(stoneChip);
            }
        }
        else if (beeId.equals("productivebees:dye_bee")) {
            if (flowerPos != null) {
                Block flowerBlock = getFloweringBlock(world, flowerPos, BlockTags.FLOWERS);
                Item flowerItem = flowerBlock.asItem();

                Map<ResourceLocation, IRecipe<CraftingInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.CRAFTING);
                Optional<IRecipe<CraftingInventory>> flowerRecipe = recipes.values().stream().flatMap((craftingRecipe) -> {
                    AtomicBoolean hasMatchingItem = new AtomicBoolean(false);
                    List<Ingredient> ingredients = craftingRecipe.getIngredients();
                    if (ingredients.size() == 1) {
                        Ingredient ingredient = ingredients.get(0);
                        ItemStack[] stacks = ingredient.getMatchingStacks();
                        if (stacks.length > 0 && stacks[0].getItem().equals(flowerItem)) {
                            hasMatchingItem.set(true);
                        }
                    }
                    return Util.streamOptional(hasMatchingItem.get() ? Optional.of(craftingRecipe) : Optional.empty());
                }).findFirst();

                flowerRecipe.ifPresent(craftingInventoryIRecipe -> {
                    ItemStack dye = new ItemStack(craftingInventoryIRecipe.getRecipeOutput().getItem(), 1);
                    outputList.add(dye);
                });
            }
        }

        return outputList;
    }

    private static ItemStack convertToCombBlock(ItemStack stack) {
        // Change to comb block
        ItemStack newStack = null;
        if (stack.getItem().equals(Items.HONEYCOMB)) {
            newStack = new ItemStack(Items.HONEYCOMB_BLOCK, stack.getCount());
        } else if (stack.getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
            newStack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get(), stack.getCount());
            newStack.setTag(stack.getTag());
        } else {
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

    private static Block getFloweringBlock(World world, BlockPos flowerPos, Tag<Block> tag) {
        BlockState flowerBlockState = world.getBlockState(flowerPos);
        Block flowerBlock = flowerBlockState.getBlock();

        if (flowerBlock instanceof Feeder) {
            TileEntity feederTile = world.getTileEntity(flowerPos);
            if (feederTile instanceof FeederTileEntity && ProductiveBeeEntity.isValidFeeder(feederTile, tag)) {
                return ((FeederTileEntity) feederTile).getRandomBlockFromInventory(tag);
            }
        }
        return flowerBlock;
    }

    public static void setOffspringAttributes(ProductiveBeeEntity newBee, ProductiveBeeEntity productiveBeeEntity, AgeableEntity targetEntity) {
        Map<BeeAttribute<?>, Object> attributeMapParent1 = productiveBeeEntity.getBeeAttributes();
        Map<BeeAttribute<?>, Object> attributeMapParent2 = new HashMap<>();
        if (targetEntity instanceof ProductiveBeeEntity) {
            attributeMapParent2 = ((ProductiveBeeEntity) targetEntity).getBeeAttributes();
        } else {
            // Default bee attributes
            attributeMapParent2.put(BeeAttributes.PRODUCTIVITY, 0);
            attributeMapParent2.put(BeeAttributes.ENDURANCE, 0);
            attributeMapParent2.put(BeeAttributes.TEMPER, 1);
            attributeMapParent2.put(BeeAttributes.BEHAVIOR, 0);
            attributeMapParent2.put(BeeAttributes.WEATHER_TOLERANCE, 0);
        }

        Map<BeeAttribute<?>, Object> attributeMapChild = newBee.getBeeAttributes();

        int parentProductivity = MathHelper.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.PRODUCTIVITY), (int) attributeMapParent2.get(BeeAttributes.PRODUCTIVITY));
        attributeMapChild.put(BeeAttributes.PRODUCTIVITY, Math.max((int) attributeMapChild.get(BeeAttributes.PRODUCTIVITY), parentProductivity));

        int parentEndurance = MathHelper.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.ENDURANCE), (int) attributeMapParent2.get(BeeAttributes.ENDURANCE));
        attributeMapChild.put(BeeAttributes.ENDURANCE, Math.max((int) attributeMapChild.get(BeeAttributes.ENDURANCE), parentEndurance));

        int parentTemper = MathHelper.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.TEMPER), (int) attributeMapParent2.get(BeeAttributes.TEMPER));
        attributeMapChild.put(BeeAttributes.TEMPER, Math.max((int) attributeMapChild.get(BeeAttributes.TEMPER), parentTemper));

        int parentBehavior = MathHelper.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.BEHAVIOR), (int) attributeMapParent2.get(BeeAttributes.BEHAVIOR));
        attributeMapChild.put(BeeAttributes.BEHAVIOR, Math.max((int) attributeMapChild.get(BeeAttributes.BEHAVIOR), parentBehavior));

        int parentWeatherTolerance = MathHelper.nextInt(ProductiveBees.rand, (int) attributeMapParent1.get(BeeAttributes.WEATHER_TOLERANCE), (int) attributeMapParent2.get(BeeAttributes.WEATHER_TOLERANCE));
        attributeMapChild.put(BeeAttributes.WEATHER_TOLERANCE, Math.max((int) attributeMapChild.get(BeeAttributes.WEATHER_TOLERANCE), parentWeatherTolerance));
    }

    public static BeeEntity convertToConfigurable(BeeEntity entity) {
        if (entity instanceof ProductiveBeeEntity && !(entity instanceof ConfigurableBeeEntity) && !(entity instanceof SolitaryBeeEntity) ) {
            String name = ProductiveBees.MODID + ":" + ((ProductiveBeeEntity) entity).getBeeName();
            if (name.equals("productivebees:wither")) {
                name = "productivebees:withered";
            } else if (name.equals("productivebees:quartz")) {
                name = "productivebees:crystalline";
            }
            BeeIngredient configuredBee = BeeIngredientFactory.getIngredient(name).get();
            if (configuredBee != null && configuredBee.isConfigurable()) {
                CompoundNBT tag = new CompoundNBT();
                entity.writeWithoutTypeId(tag);

                ConfigurableBeeEntity newEntity = (ConfigurableBeeEntity) configuredBee.getBeeEntity().create(entity.world);
                newEntity.read(tag);
                newEntity.setBeeType(name);
                return newEntity;
            }
        }
        return entity;
    }

    public static List<ITextComponent> populateBeeInfoFromTag(CompoundNBT tag, @Nullable List<ITextComponent> list) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(new TranslationTextComponent(tag.getInt("Age") < 0 ? "productivebees.information.age.child" : "productivebees.information.age.adult").applyTextStyle(TextFormatting.AQUA).applyTextStyle(TextFormatting.ITALIC));

        if (tag.getBoolean("isProductiveBee")) {
            float current = tag.getFloat("Health");
            float max = tag.contains("MaxHealth") ? tag.getFloat("MaxHealth") : 10.0f;
            list.add((new TranslationTextComponent("productivebees.information.attribute.health", current, max)).applyTextStyle(TextFormatting.DARK_GRAY));

            String type = tag.getString("bee_type");
            ITextComponent type_value = new TranslationTextComponent("productivebees.information.attribute.type." + type).applyTextStyle(ColorUtil.getColor(type));
            list.add((new TranslationTextComponent("productivebees.information.attribute.type", type_value)).applyTextStyle(TextFormatting.DARK_GRAY));

            int productivity = tag.getInt("bee_productivity");
            ITextComponent productivity_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.PRODUCTIVITY).get(productivity)).applyTextStyle(ColorUtil.getColor(productivity));
            list.add((new TranslationTextComponent("productivebees.information.attribute.productivity", productivity_value)).applyTextStyle(TextFormatting.DARK_GRAY));

            int tolerance = tag.getInt("bee_weather_tolerance");
            ITextComponent tolerance_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.WEATHER_TOLERANCE).get(tolerance)).applyTextStyle(ColorUtil.getColor(tolerance));
            list.add((new TranslationTextComponent("productivebees.information.attribute.weather_tolerance", tolerance_value)).applyTextStyle(TextFormatting.DARK_GRAY));

            int behavior = tag.getInt("bee_behavior");
            ITextComponent behavior_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.BEHAVIOR).get(behavior)).applyTextStyle(ColorUtil.getColor(behavior));
            list.add((new TranslationTextComponent("productivebees.information.attribute.behavior", behavior_value)).applyTextStyle(TextFormatting.DARK_GRAY));

            int endurance = tag.getInt("bee_endurance");
            ITextComponent endurance_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.ENDURANCE).get(endurance)).applyTextStyle(ColorUtil.getColor(endurance));
            list.add((new TranslationTextComponent("productivebees.information.attribute.endurance", endurance_value)).applyTextStyle(TextFormatting.DARK_GRAY));

            int temper = tag.getInt("bee_temper");
            ITextComponent temper_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.TEMPER).get(temper)).applyTextStyle(ColorUtil.getColor(temper));
            list.add((new TranslationTextComponent("productivebees.information.attribute.temper", temper_value)).applyTextStyle(TextFormatting.DARK_GRAY));

            if (tag.contains("HivePos")) {
                BlockPos hivePos = NBTUtil.readBlockPos(tag.getCompound("HivePos"));
                list.add(new StringTextComponent("Home position: " + hivePos.getX() + ", " + hivePos.getY() + ", " + hivePos.getZ()));
            }
        } else {
            list.add((new StringTextComponent("Mod: " + tag.getString("mod"))).applyTextStyle(TextFormatting.DARK_AQUA));
        }

        return list;
    }

    public static class IdentifierInventory implements IInventory
    {
        private List<String> identifiers = new ArrayList<>();

        public IdentifierInventory(String identifier) {
            this.identifiers.add(identifier);
        }

        public IdentifierInventory(BeeEntity bee1, BeeEntity bee2) {
            String identifier1 = bee1.getEntityString();
            if (bee1 instanceof ConfigurableBeeEntity) {
                identifier1 = ((ConfigurableBeeEntity) bee1).getBeeType();
            }
            String identifier2 = bee2.getEntityString();
            if (bee2 instanceof ConfigurableBeeEntity) {
                identifier2 = ((ConfigurableBeeEntity) bee2).getBeeType();
            }
            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public IdentifierInventory(BeeEntity bee1, String identifier2) {
            String identifier1 = bee1.getEntityString();
            if (bee1 instanceof ConfigurableBeeEntity) {
                identifier1 = ((ConfigurableBeeEntity) bee1).getBeeType();
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
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return identifiers.isEmpty();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int i) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack decrStackSize(int i, int i1) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack removeStackFromSlot(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setInventorySlotContents(int i, @Nonnull ItemStack itemStack) {

        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUsableByPlayer(@Nonnull PlayerEntity playerEntity) {
            return false;
        }

        @Override
        public void clear() {
            this.identifiers.clear();
        }
    }
}
