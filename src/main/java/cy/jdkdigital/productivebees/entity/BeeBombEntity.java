package cy.jdkdigital.productivebees.entity;

import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.item.BeeBomb;
import cy.jdkdigital.productivebees.item.BeeBombAngry;
import cy.jdkdigital.productivebees.item.BeeCage;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

    @OnlyIn(Dist.CLIENT)
    public BeeBombEntity(World world, double x, double y, double z) {
        super(ModEntities.BEE_BOMB.get(), x, y, z, world);
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return ModItems.BEE_BOMB.get();
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult result) {
        if (!this.world.isRemote) {
            BlockPos blockPos = null;
            Entity entity = null;
            if (result.getType() == RayTraceResult.Type.BLOCK) {
                blockPos = ((BlockRayTraceResult) result).getPos();
            }
            else if (result.getType() == RayTraceResult.Type.ENTITY) {
                entity = ((EntityRayTraceResult) result).getEntity();
                blockPos = entity.getPosition();
            }

            // Release list of bees near landing location
            ItemStack bomb = getItem();

            boolean isAngry = bomb.getItem() instanceof BeeBombAngry;

            blockPos = blockPos.up();

            ListNBT bees = BeeBomb.getBees(bomb);
            if (!(entity instanceof PlayerEntity)) {
                List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(blockPos).grow(2.0D, 2.0D, 2.0D)));
                if (players.size() > 0) {
                    entity = players.iterator().next();
                }
            }
            for (INBT bee : bees) {
                Entity beeEntity = BeeCage.getEntityFromStack((CompoundNBT) bee, world, true);
                if (beeEntity != null) {
                    beeEntity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                    if (isAngry && beeEntity instanceof BeeEntity) {
                        if (entity instanceof PlayerEntity) {
                            ((BeeEntity) beeEntity).setBeeAttacker(entity);
                        }
                        else {
                            ((BeeEntity) beeEntity).setAnger(400 + this.rand.nextInt(400));
                        }
                    }

                    world.addEntity(beeEntity);
                }
            }
            this.remove();
        }
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
