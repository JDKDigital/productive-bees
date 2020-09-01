package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SolitaryBeeEntity extends ProductiveBeeEntity
{
    private BlockPos birthNest;
    public boolean hasHadNest = false;
    private int ticksWithoutNest = 12000;

    public SolitaryBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() || poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
        beeAttributes.put(BeeAttributes.TYPE, "solitary");
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.SOLITARY_OVERWORLD_NESTS);
    }

    @Override
    public void tick() {
        super.tick();

        // Kill off the bee if it hasn't found a nest within 10 minutes
        if (!hasHadNest && --ticksWithoutNest < 0) {
            setHasStung(true);
        }
    }

    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.removeGoal(this.followParentGoal);
        this.goalSelector.addGoal(3, new HomesickGoal());
    }

    @Nonnull
    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(0.85F);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);

        tag.putBoolean("hasHadNest", hasHadNest);
        if (birthNest != null) {
            tag.put("birthNest", NBTUtil.writeBlockPos(birthNest));
        }
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

        hasHadNest = tag.contains("hasHadNest") && tag.getBoolean("hasHadNest");
        if (tag.contains("birthNest")) {
            birthNest = NBTUtil.readBlockPos(tag.getCompound("birthNest"));
        }
    }

    public void setBirthNest(BlockPos birthNest) {
        this.birthNest = birthNest;
    }

    public class HomesickGoal extends Goal {

        @Override
        public boolean shouldExecute() {
            if (SolitaryBeeEntity.this.hasHadNest || SolitaryBeeEntity.this.birthNest == null) {
                return false;
            }
            Vec3d vec3d = new Vec3d(SolitaryBeeEntity.this.birthNest);
            double distanceToHome = vec3d.distanceTo(SolitaryBeeEntity.this.getPositionVec());

            return distanceToHome >= 25;
        }

        public void tick() {
            if (SolitaryBeeEntity.this.birthNest != null) {
                if (SolitaryBeeEntity.this.navigator.noPath()) {
                    Vec3d vec3d = new Vec3d(SolitaryBeeEntity.this.birthNest);
                    SolitaryBeeEntity.this.navigator.tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }
        }
    }
}
