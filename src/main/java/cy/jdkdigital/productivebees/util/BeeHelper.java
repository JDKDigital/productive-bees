package cy.jdkdigital.productivebees.util;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
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

import javax.annotation.Nullable;
import java.util.*;

public class BeeHelper
{
    private static final Random rand = new Random();

    public static Map<String, Map<String, List<String>>> breedingMap = new HashMap<String, Map<String, List<String>>>()
    {{
        put("ashy_mining", new HashMap<String, List<String>>()
        {{
            put("quartz", Arrays.asList("iron"));
        }});
        put("blazing", new HashMap<String, List<String>>()
        {{
            put("leafcutter", Arrays.asList("coal"));
        }});
        put("blue_banded", new HashMap<String, List<String>>()
        {{
            put("redstone", Arrays.asList("lapis"));
        }});
        put("chocolate_mining", new HashMap<String, List<String>>()
        {{
            put("glowing", Arrays.asList("redstone"));
        }});
        put("coal", new HashMap<String, List<String>>()
        {{
            put("iron", Arrays.asList("steel"));
            put("ender", Arrays.asList("tungsten"));
        }});
        put("copper", new HashMap<String, List<String>>()
        {{
            put("zinc", Arrays.asList("brass"));
            put("tin", Arrays.asList("bronze"));
        }});
        put("creeper", new HashMap<String, List<String>>()
        {{
            put("iron", Arrays.asList("radioactive"));
        }});
        put("diamond", new HashMap<String, List<String>>()
        {{
            put("slimy", Arrays.asList("emerald"));
        }});
        put("ender", new HashMap<String, List<String>>()
        {{
            put("lapis", Arrays.asList("diamond"));
            put("coal", Arrays.asList("tungsten"));
            put("gold", Arrays.asList("platinum"));
        }});
        put("glowing", new HashMap<String, List<String>>()
        {{
            put("chocolate_mining", Arrays.asList("redstone"));
        }});
        put("gold", new HashMap<String, List<String>>()
        {{
            put("ender", Arrays.asList("platinum"));
        }});
        put("iron", new HashMap<String, List<String>>()
        {{
            put("reed", Arrays.asList("copper"));
            put("coal", Arrays.asList("steel"));
            put("nickel", Arrays.asList("invar"));
            put("creeper", Arrays.asList("radioactive"));
        }});
        put("lapis", new HashMap<String, List<String>>()
        {{
            put("ender", Arrays.asList("diamond"));
        }});
        put("leafcutter", new HashMap<String, List<String>>()
        {{
            put("mason", Arrays.asList("spidey"));
            put("blazing", Arrays.asList("coal"));
        }});
        put("magmatic", new HashMap<String, List<String>>()
        {{
            put("nomad", Arrays.asList("blazing"));
        }});
        put("mason", new HashMap<String, List<String>>()
        {{
            put("leafcutter", Arrays.asList("spidey"));
            put("quartz", Arrays.asList("gold"));
        }});
        put("nickel", new HashMap<String, List<String>>()
        {{
            put("iron", Arrays.asList("invar"));
        }});
        put("nomad", new HashMap<String, List<String>>()
        {{
            put("magmatic", Arrays.asList("blazing"));
        }});
        put("slimy", new HashMap<String, List<String>>()
        {{
            put("diamond", Arrays.asList("diamond"));
        }});
        put("tin", new HashMap<String, List<String>>()
        {{
            put("copper", Arrays.asList("bronze"));
        }});
        put("quartz", new HashMap<String, List<String>>()
        {{
            put("mason", Arrays.asList("gold"));
            put("ashy_mining", Arrays.asList("iron"));
        }});
        put("redstone", new HashMap<String, List<String>>()
        {{
            put("blue_banded", Arrays.asList("lapis"));
        }});
        put("reed", new HashMap<String, List<String>>()
        {{
            put("iron", Arrays.asList("copper"));
        }});
        put("zinc", new HashMap<String, List<String>>()
        {{
            put("copper", Arrays.asList("brass"));
        }});
    }};

    public static BeeEntity itemInteract(BeeEntity entity, ItemStack itemStack, World world, CompoundNBT nbt, PlayerEntity player, Hand hand, Direction direction) {
        BlockPos pos = entity.getPosition();

        EntityType<BeeEntity> bee = null;
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
        else if (itemStack.getItem() == Items.TNT) {
            if (!entity.getEntityString().equals("productivebees:creeper_bee")) {
                bee = ModEntities.CREEPER_BEE.get();
            }
        }
        else if (itemStack.getItem() == Items.WITHER_ROSE) {
            if (!entity.getEntityString().equals("productivebees:wither_bee")) {
                bee = ModEntities.WITHER_BEE.get();
            }
        }
        else if (itemStack.getItem() == Items.HONEYCOMB) {
            if (!entity.getEntityString().equals("minecraft:bee")) {
                bee = EntityType.BEE;
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

    public static ResourceLocation getBreedingResult(ProductiveBeeEntity beeEntity, AgeableEntity targetEntity) {
        // Only breed Productive Bees, breeding with other bees will give a vanilla bee for now
        if (!(targetEntity instanceof ProductiveBeeEntity)) {
            return new ResourceLocation("minecraft:bee");
        }

        // Get breeding rules
        Map<String, List<String>> res = breedingMap.get(beeEntity.getBeeType());

        // If the two bees are the same type, or no breeding rules exist, create a new of that type
        if (res == null || beeEntity.getBeeType().equals(((ProductiveBeeEntity) targetEntity).getBeeType())) {
            return new ResourceLocation(ProductiveBees.MODID, beeEntity.getBeeType() + "_bee");
        }

        String babyType = null;
        List<String> possibleBreedings = res.get(((ProductiveBeeEntity) targetEntity).getBeeType());
        if (possibleBreedings != null && possibleBreedings.size() > 0) {
            babyType = possibleBreedings.get(0);
        }

        // If no specific rules for the target bee exist, create a child of same type
        if (babyType == null) {
            babyType = beeEntity.getBeeType();
        }

        return new ResourceLocation(ProductiveBees.MODID, babyType + "_bee");
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
}
