package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EnderBeeEntity extends ProductiveBeeEntity
{
    private int teleportCooldown = 250;

    public EnderBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.END_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.END_NESTS);
    }

    @Override
    public void tick() {
        super.tick();
        this.teleportCooldown--;
    }

    @Override
    protected void updateAITasks() {
        // Teleport to active path
        if (this.teleportCooldown <= 0 && null != this.navigator.getPath()) {
            if (this.hasHive()) {
                TileEntity te = world.getTileEntity(this.getHivePos());
                if (te instanceof AdvancedBeehiveTileEntity) {
                    int antiTeleportUpgrades = ((AdvancedBeehiveTileEntity) te).getUpgradeCount(ModItems.UPGRADE_ANTI_TELEPORT.get());
                    if (antiTeleportUpgrades > 0) {
                        this.teleportCooldown = 10000;
                        super.updateAITasks();
                        return;
                    }
                }
            }
            this.teleportCooldown = 250;
            BlockPos pos = this.navigator.getPath().getTarget();
            teleportTo(pos.getX(), pos.getY(), pos.getZ());
        }

        super.updateAITasks();
    }

    private void teleportTo(double x, double y, double z) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

        while (blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(blockpos$mutable);
        if (blockstate.getMaterial().blocksMovement()) {
            EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                boolean teleported = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (teleported) {
                    this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 0.3F, 1.0F);
                    this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.2F, 1.0F);
                }
            }
        }
    }
}
