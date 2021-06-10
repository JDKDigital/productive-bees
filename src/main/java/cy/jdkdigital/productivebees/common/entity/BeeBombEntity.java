package cy.jdkdigital.productivebees.common.entity;

import cy.jdkdigital.productivebees.common.item.BeeBomb;
import cy.jdkdigital.productivebees.common.item.BeeBombAngry;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.List;

public class BeeBombEntity extends ProjectileItemEntity
{
    public BeeBombEntity(EntityType<? extends ProjectileItemEntity> entity, World world) {
        super(entity, world);
    }

    public BeeBombEntity(World world, LivingEntity entity) {
        super(ModEntities.BEE_BOMB.get(), entity, world);
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return ModItems.BEE_BOMB.get();
    }

    @Override
    protected void onHit(@Nonnull RayTraceResult result) {
        if (!this.level.isClientSide) {
            BlockPos blockPos = null;
            Entity entity = null;
            if (result.getType() == RayTraceResult.Type.BLOCK) {
                blockPos = ((BlockRayTraceResult) result).getBlockPos();
            } else if (result.getType() == RayTraceResult.Type.ENTITY) {
                entity = ((EntityRayTraceResult) result).getEntity();
                blockPos = entity.blockPosition();
            }

            // Release list of bees near landing location
            ItemStack bomb = getItem();

            boolean isAngry = bomb.getItem() instanceof BeeBombAngry;

            if (blockPos != null) {
                blockPos = blockPos.above();

                ListNBT bees = BeeBomb.getBees(bomb);
                if (!(entity instanceof PlayerEntity)) {
                    List<PlayerEntity> players = level.getEntitiesOfClass(PlayerEntity.class, (new AxisAlignedBB(blockPos).expandTowards(2.0D, 2.0D, 2.0D)));
                    if (players.size() > 0) {
                        entity = players.iterator().next();
                    }
                }
                for (INBT bee : bees) {
                    BeeEntity beeEntity = BeeCage.getEntityFromStack((CompoundNBT) bee, level, true);
                    if (beeEntity != null) {
                        beeEntity.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
                        if (isAngry) {
                            if (entity instanceof PlayerEntity) {
                                beeEntity.setPersistentAngerTarget(entity.getUUID());
                            } else {
                                beeEntity.setRemainingPersistentAngerTime(400 + this.random.nextInt(400));
                            }
                        }

                        level.addFreshEntity(beeEntity);
                    }
                }
            }
            this.remove();
        }
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
