package cy.jdkdigital.productivebees.util;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.integrations.resourcefulbees.ResourcefulBeesCompat;
import cy.jdkdigital.productivebees.item.WoodChip;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import cy.jdkdigital.productivebees.recipe.BeeConversionRecipe;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BeeHelper
{
    public static final Random rand = new Random();

    public static BeeEntity itemInteract(BeeEntity entity, ItemStack itemStack, ServerWorld world, CompoundNBT nbt, PlayerEntity player, Hand hand, Direction direction) {
        BlockPos pos = entity.getPosition();

        // Conversion recipes
        BeeEntity bee = null;
        List<BeeConversionRecipe> recipes = world.getRecipeManager().getRecipes(BeeConversionRecipe.BEE_CONVERSION, new IdentifierInventory(entity, itemStack.getItem().getRegistryName() + ""), world);

        if (!recipes.isEmpty()) {
            BeeConversionRecipe recipe = recipes.get(rand.nextInt(recipes.size()));
            bee = recipe.result.get().getBeeEntity().create(world);
            if (bee instanceof ConfigurableBeeEntity) {
                ((ConfigurableBeeEntity) bee).setBeeType(recipe.result.get().getBeeType().toString());
            }
        }

        if (bee != null) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }

            return BeeHelper.prepareBeeSpawn(bee, world, nbt, player, pos, direction, entity.getGrowingAge());
        }
        return null;
    }

    public static BeeEntity prepareBeeSpawn(EntityType<? extends BeeEntity> beeType, ServerWorld world, @Nullable CompoundNBT nbt, @Nullable PlayerEntity player, BlockPos pos, Direction direction, int age) {
        BeeEntity bee = beeType.create(world, nbt, null, player, pos, SpawnReason.CONVERSION, true, true);
        return prepareBeeSpawn(bee, world, nbt, player, pos, direction, age);
    }

    public static BeeEntity prepareBeeSpawn(BeeEntity bee, ServerWorld world, @Nullable CompoundNBT nbt, @Nullable PlayerEntity player, BlockPos pos, Direction direction, int age) {
        if (bee != null) {
            double x = (double) pos.getX() + (double) direction.getXOffset();
            double y = (double) pos.getY() + 0.5D - (double) (bee.getHeight() / 2.0F);
            double z = (double) pos.getZ() + (double) direction.getZOffset();
            bee.setLocationAndAngles(x, y, z, bee.rotationYaw, bee.rotationPitch);

            if (age > 0) {
                bee.setGrowingAge(age);
            }

            return bee;
        }
        return null;
    }

    public static BeeEntity getBreedingResult(ProductiveBeeEntity beeEntity, AgeableEntity targetEntity, ServerWorld world) {
        // Get breeding recipes
        List<BeeBreedingRecipe> recipes = new ArrayList<>();

        Map<ResourceLocation, IRecipe<IInventory>> allRecipes = world.getRecipeManager().getRecipes(BeeBreedingRecipe.BEE_BREEDING);
        IInventory beeInv = new IdentifierInventory(beeEntity, (BeeEntity) targetEntity);
        ProductiveBees.LOGGER.info("inv: " + beeInv);
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
            BeeBreedingRecipe recipe = (BeeBreedingRecipe) entry.getValue();
            if (recipe.matches(beeInv, world)) {
                recipes.add(recipe);
            }
        }

        if (!recipes.isEmpty()) {
            BeeBreedingRecipe recipe = recipes.get(rand.nextInt(recipes.size()));
            List<Lazy<BeeIngredient>> possibleOffspring = recipe.offspring;
            if (possibleOffspring != null && possibleOffspring.size() > 0) {
                BeeIngredient beeIngredient = possibleOffspring.get(rand.nextInt(possibleOffspring.size())).get();
                BeeEntity newBee = beeIngredient.getBeeEntity().create(world);
                if (newBee instanceof ConfigurableBeeEntity) {
                    ((ConfigurableBeeEntity) newBee).setBeeType(beeIngredient.getBeeType().toString());
                }
                return newBee;
            }
        }

        // Check if bee is configurable
        if (beeEntity instanceof ConfigurableBeeEntity) {
            ResourceLocation type = new ResourceLocation(((ConfigurableBeeEntity) beeEntity).getBeeType());
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(type);
            if (nbt != null) {
                ConfigurableBeeEntity newBee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                newBee.setBeeType(type.toString());
                return newBee;
            }
        }

        // If no specific recipe exist for the target bee or the bees are the same type, create a child like the parent
        return (BeeEntity) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeEntity.getEntityString())).create(world);
    }

    public static List<ItemStack> getBeeProduce(World world, BeeEntity beeEntity) {
        AdvancedBeehiveRecipe matchedRecipe = null;
        BlockPos flowerPos = beeEntity.getFlowerPos();

        String beeId = beeEntity.getEntityString();
        if (beeEntity instanceof ConfigurableBeeEntity) {
            beeId = ((ConfigurableBeeEntity) beeEntity).getBeeType();
        }

//        ProductiveBees.LOGGER.info("datamanager: " + beeEntity.getDataManager().getAll());

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
                int count = MathHelper.nextInt(rand, MathHelper.floor(bounds.get(0).getInt()), MathHelper.floor(bounds.get(1).getInt()));
                ItemStack stack = itemStack.copy();
                stack.setCount(count);
                outputList.add(stack);
            });
        }
        else if (beeId.equals("productivebees:lumber_bee")) {
            if (flowerPos != null) {
                BlockState flowerBlock = world.getBlockState(flowerPos);

                if (flowerBlock.getBlock().isIn(BlockTags.LOGS)) {
                    ItemStack woodChip = WoodChip.getStack(flowerBlock.getBlock(), world.rand.nextInt(6) + 1);
                    outputList.add(woodChip);
                }
            }
        }
        else if (beeId.equals("productivebees:dye_bee")) {
            if (flowerPos != null) {
                BlockState flowerBlock = world.getBlockState(flowerPos);
                Item flowerItem = Item.getItemFromBlock(flowerBlock.getBlock());

                Map<ResourceLocation, IRecipe<CraftingInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.CRAFTING);
                Optional<IRecipe<CraftingInventory>> flowerRecipe = recipes.values().stream().flatMap((craftingRecipe) -> {
                    AtomicBoolean hasMatchingItem = new AtomicBoolean(false);
                    for (Ingredient ingredient : craftingRecipe.getIngredients()) {
                        ItemStack[] stacks = ingredient.getMatchingStacks();
                        if (stacks.length > 0 && stacks[0].getItem().equals(flowerItem)) {
                            hasMatchingItem.set(true);
                            break;
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

    public static void setOffspringAttributes(ProductiveBeeEntity newBee, ProductiveBeeEntity productiveBeeEntity, AgeableEntity targetEntity) {
        Map<BeeAttribute<?>, Object> attributeMapParent1 = productiveBeeEntity.getBeeAttributes();
        Map<BeeAttribute<?>, Object> attributeMapParent2 = new HashMap<>();
        if (targetEntity instanceof ProductiveBeeEntity) {
            attributeMapParent2 = ((ProductiveBeeEntity) targetEntity).getBeeAttributes();
        }
        else {
            // Default bee attributes
            attributeMapParent2.put(BeeAttributes.PRODUCTIVITY, 0);
            attributeMapParent2.put(BeeAttributes.TEMPER, 1);
            attributeMapParent2.put(BeeAttributes.BEHAVIOR, 0);
            attributeMapParent2.put(BeeAttributes.WEATHER_TOLERANCE, 0);
        }

        Map<BeeAttribute<?>, Object> attributeMapChild = newBee.getBeeAttributes();

        int parentProductivity = MathHelper.nextInt(rand, (int) attributeMapParent1.get(BeeAttributes.PRODUCTIVITY), (int) attributeMapParent2.get(BeeAttributes.PRODUCTIVITY));
        attributeMapChild.put(BeeAttributes.PRODUCTIVITY, Math.max((int) attributeMapChild.get(BeeAttributes.PRODUCTIVITY), parentProductivity));

        int parentTemper = MathHelper.nextInt(rand, (int) attributeMapParent1.get(BeeAttributes.TEMPER), (int) attributeMapParent2.get(BeeAttributes.TEMPER));
        attributeMapChild.put(BeeAttributes.TEMPER, Math.max((int) attributeMapChild.get(BeeAttributes.TEMPER), parentTemper));

        int parentBehavior = MathHelper.nextInt(rand, (int) attributeMapParent1.get(BeeAttributes.BEHAVIOR), (int) attributeMapParent2.get(BeeAttributes.BEHAVIOR));
        attributeMapChild.put(BeeAttributes.BEHAVIOR, Math.max((int) attributeMapChild.get(BeeAttributes.BEHAVIOR), parentBehavior));

        int parentWeatherTolerance = MathHelper.nextInt(rand, (int) attributeMapParent1.get(BeeAttributes.WEATHER_TOLERANCE), (int) attributeMapParent2.get(BeeAttributes.WEATHER_TOLERANCE));
        attributeMapChild.put(BeeAttributes.WEATHER_TOLERANCE, Math.max((int) attributeMapChild.get(BeeAttributes.WEATHER_TOLERANCE), parentWeatherTolerance));
    }

    public static BeeEntity convertToConfigurable(BeeEntity entity) {
        if (entity instanceof ProductiveBeeEntity && !(entity instanceof ConfigurableBeeEntity) && !(entity instanceof SolitaryBeeEntity) ) {
            String name = ProductiveBees.MODID + ":" + ((ProductiveBeeEntity) entity).getBeeName();
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
