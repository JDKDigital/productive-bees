package cy.jdkdigital.productivebees.common.entity;

import cy.jdkdigital.productivebees.common.item.BeeBomb;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import java.util.List;

public class BeeBombEntity extends ThrowableItemProjectile
{
    public BeeBombEntity(EntityType<? extends ThrowableItemProjectile> entity, Level world) {
        super(entity, world);
    }

    public BeeBombEntity(Level world, LivingEntity entity) {
        super(ModEntities.BEE_BOMB.get(), entity, world);
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return ModItems.BEE_BOMB.get();
    }

    @Override
    protected void onHit(@Nonnull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            BlockPos blockPos = null;
            Entity entity = null;
            if (result.getType() == HitResult.Type.BLOCK) {
                blockPos = ((BlockHitResult) result).getBlockPos();
            } else if (result.getType() == HitResult.Type.ENTITY) {
                entity = ((EntityHitResult) result).getEntity();
                blockPos = entity.blockPosition();
            }

            // Release list of bees near landing location
            ItemStack bomb = getItem();

            boolean isAngry = bomb.getItem() instanceof BeeBomb beeBomb && beeBomb.isAngry();

            if (blockPos != null) {
                blockPos = blockPos.above();

                ListTag bees = BeeBomb.getBees(bomb);
                if (!(entity instanceof Player)) {
                    List<Player> players = level().getEntitiesOfClass(Player.class, (new AABB(blockPos).inflate(5.0D, 2.0D, 5.0D)));
                    if (players.size() > 0) {
                        entity = players.iterator().next();
                    }
                }

                // TODO 1.21 reimplement
//                for (Tag bee : bees) {
//                    Bee beeEntity = BeeCage.getEntityFromStack(bee, level(), true);
//                    if (beeEntity != null) {
//                        beeEntity.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
//                        if (isAngry) {
//                            if (entity instanceof Player player) {
//                                beeEntity.setPersistentAngerTarget(entity.getUUID());
//                                beeEntity.setTarget(player);
//                            } else {
//                                beeEntity.setRemainingPersistentAngerTime(400 + this.random.nextInt(400));
//                            }
//                        }
//
//                        level().addFreshEntity(beeEntity);
//                    }
//                }
            }
            this.discard();
        }
    }

    //    @Nonnull
//    @Override
//    public Packet<ClientGamePacketListener> getAddEntityPacket() {
//        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
//    }
}
