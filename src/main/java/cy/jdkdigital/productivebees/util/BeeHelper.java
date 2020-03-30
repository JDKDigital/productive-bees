package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BeeHelper {
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
}
