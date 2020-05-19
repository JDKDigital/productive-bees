package cy.jdkdigital.productivebees.util;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BeeHelper
{
    private static final Random rand = new Random();

    public static BeeEntity itemInteract(BeeEntity entity, ItemStack itemStack, World world, CompoundNBT nbt, PlayerEntity player, Hand hand, Direction direction) {
        BlockPos pos = entity.getPosition();

        EntityType<BeeEntity> bee = null;
        if (ProductiveBeesConfig.GENERAL.enableItemConverting.get()) {

            if (itemStack.getItem() == Items.REDSTONE) {
                if (!entity.getEntityString().equals("productivebees:redstone_bee")) {
                    bee = ModEntities.REDSTONE_BEE.get();
                }
            }
            else if (itemStack.getItem() == Items.EMERALD) {
                if (!entity.getEntityString().equals("productivebees:emerald_bee")) {
                    bee = ModEntities.EMERALD_BEE.get();
                }
            }
            else if (itemStack.getItem() == Items.LAPIS_LAZULI) {
                if (!entity.getEntityString().equals("productivebees:lapis_bee")) {
                    bee = ModEntities.LAPIS_BEE.get();
                }
            }
            else if (itemStack.getItem() == Items.DIAMOND) {
                if (!entity.getEntityString().equals("productivebees:diamond_bee")) {
                    bee = ModEntities.DIAMOND_BEE.get();
                }
            }
            else if (itemStack.getItem() == Items.IRON_INGOT) {
                if (!entity.getEntityString().equals("productivebees:iron_bee")) {
                    bee = ModEntities.IRON_BEE.get();
                }
            }
            else if (itemStack.getItem() == Items.GOLD_INGOT) {
                if (!entity.getEntityString().equals("productivebees:gold_bee")) {
                    bee = ModEntities.GOLD_BEE.get();
                }
            }
            else if (itemStack.getItem() == Items.HONEYCOMB) {
                if (!entity.getEntityString().equals("minecraft:bee")) {
                    bee = EntityType.BEE;
                }
            }
        }
        if (itemStack.getItem() == Items.TNT) {
            if (entity.getEntityString().equals("minecraft:bee")) {
                bee = ModEntities.CREEPER_BEE.get();
            }
        }
        else if (itemStack.getItem() == Items.WITHER_ROSE) {
            if (entity.getEntityString().equals("productivebees:skeletal_bee")) {
                bee = ModEntities.WITHER_BEE.get();
            }
        }

        if (bee != null) {
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

        // Get breeding recipe
        BeeBreedingRecipe recipe = world.getRecipeManager().getRecipe(BeeBreedingRecipe.BEE_BREEDING, new BeeInventory(beeEntity.getBeeType()), world).orElse(null);

        // If the two bees are the same type, or no breeding rules exist, create a new of that type
        if (recipe == null || beeEntity.getBeeType().equals(((ProductiveBeeEntity) targetEntity).getBeeType())) {
            return new ResourceLocation(ProductiveBees.MODID, beeEntity.getBeeType() + "_bee");
        }

        List<BeeIngredient> possibleOffspring = recipe.offspring;
        if (possibleOffspring != null && possibleOffspring.size() > 0) {
            return possibleOffspring.get(rand.nextInt(possibleOffspring.size())).getBeeType().getRegistryName();
        }

        // If no specific rules for the target bee exist, create a child of same type
        return new ResourceLocation(ProductiveBees.MODID, beeEntity.getBeeType() + "_bee");
    }

    public static List<ItemStack> getBeeProduce(World world, String beeId) {
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : world.getRecipeManager().getRecipes(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE).entrySet()) {
            AdvancedBeehiveRecipe recipe = (AdvancedBeehiveRecipe) entry.getValue();
            if (beeId.equals(recipe.ingredient.getBeeType().getRegistryName().toString())) {
                List<ItemStack> outputList = new ArrayList<>();
                recipe.output.forEach((itemStack, bounds) -> {
                    int count = MathHelper.nextInt(rand, MathHelper.floor(bounds.get(0).getInt()), MathHelper.floor(bounds.get(1).getInt()));
                    itemStack.setCount(count);
                    outputList.add(itemStack);
                });
                return outputList;
            }
        }

        return Lists.newArrayList(ItemStack.EMPTY);
    }

    public static class BeeInventory implements IInventory
    {
        private String beeName;

        public BeeInventory(String beeName) {
            this.beeName = beeName;
        }

        public String getBeeName() {
            return this.beeName;
        }

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return beeName == null;
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
            this.beeName = null;
        }
    }
}
