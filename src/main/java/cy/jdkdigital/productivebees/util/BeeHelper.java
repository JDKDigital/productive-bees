package cy.jdkdigital.productivebees.util;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.SkeletalBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.item.WoodChip;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import cy.jdkdigital.productivebees.recipe.BeeConversionRecipe;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.tileentity.InventoryHandlerHelper;
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
import net.minecraft.item.Items;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BeeHelper
{
    private static final Random rand = new Random();

    public static BeeEntity itemInteract(BeeEntity entity, ItemStack itemStack, World world, CompoundNBT nbt, PlayerEntity player, Hand hand, Direction direction) {
        BlockPos pos = entity.getPosition();

        // Conversion recipes
        EntityType<BeeEntity> bee = null;
        List<BeeConversionRecipe> recipes = world.getRecipeManager().getRecipes(BeeConversionRecipe.BEE_CONVERSION, new IdentifierInventory(entity.getEntityString(), itemStack.getItem().getRegistryName() + ""), world);
        ProductiveBees.LOGGER.info("Convrsion recipes: " + recipes);
        if (!recipes.isEmpty()) {
            BeeConversionRecipe recipe = recipes.get(rand.nextInt(recipes.size()));
            bee = recipe.result.getBeeType();
        }

        if (bee != null) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            return BeeHelper.prepareBeeSpawn(bee, world, nbt, player, pos, direction, entity.getGrowingAge());
        }
        return null;
    }

    public static BeeEntity prepareBeeSpawn(EntityType<BeeEntity> beeType, World world, @Nullable CompoundNBT nbt, @Nullable PlayerEntity player, BlockPos pos, Direction direction, int age) {
        BeeEntity bee = beeType.create(world, nbt, null, player, pos, SpawnReason.CONVERSION, true, true);

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

    public static ResourceLocation getBreedingResult(ProductiveBeeEntity beeEntity, AgeableEntity targetEntity, World world) {
        // Only breed Productive Bees, breeding with other bees will give a vanilla bee for now
        if (!(targetEntity instanceof ProductiveBeeEntity)) {
            return new ResourceLocation("minecraft:bee");
        }

        if (!beeEntity.getBeeType().equals(((ProductiveBeeEntity) targetEntity).getBeeType())) {
            // Get breeding recipes
            List<BeeBreedingRecipe> recipes = world.getRecipeManager().getRecipes(BeeBreedingRecipe.BEE_BREEDING, new IdentifierInventory(beeEntity.getBeeType(), ((ProductiveBeeEntity) targetEntity).getBeeType()), world);
            // If the two bees are the same type, or no breeding rules exist, create a new of that type
            if (!recipes.isEmpty()) {
                BeeBreedingRecipe recipe = recipes.get(rand.nextInt(recipes.size()));
                List<BeeIngredient> possibleOffspring = recipe.offspring;
                if (possibleOffspring != null && possibleOffspring.size() > 0) {
                    return possibleOffspring.get(rand.nextInt(possibleOffspring.size())).getBeeType().getRegistryName();
                }
            }
        }

        // If no specific rules for the target bee exist or the bees are the same type, create a child like the parent
        return new ResourceLocation(ProductiveBees.MODID, beeEntity.getBeeType() + "_bee");
    }

    public static List<ItemStack> getBeeProduce(World world, String beeId, BlockPos flowerPos) {
        AdvancedBeehiveRecipe recipe = world.getRecipeManager().getRecipe(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE, new IdentifierInventory(beeId), world).orElse(null);
        List<ItemStack> outputList = Lists.newArrayList(ItemStack.EMPTY);
        if (recipe != null) {
            recipe.output.forEach((itemStack, bounds) -> {
                int count = MathHelper.nextInt(rand, MathHelper.floor(bounds.get(0).getInt()), MathHelper.floor(bounds.get(1).getInt()));
                outputList.add(new ItemStack(itemStack.getItem(), count));
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

    public static class IdentifierInventory implements IInventory
    {
        private List<String> identifiers = new ArrayList<>();

        public IdentifierInventory(String identifier) {
            this.identifiers.add(identifier);
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
