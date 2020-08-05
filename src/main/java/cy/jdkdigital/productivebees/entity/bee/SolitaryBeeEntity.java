package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SolitaryBeeEntity extends ProductiveBeeEntity
{
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

    @Nonnull
    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(0.85F);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);

        tag.putBoolean("hasHadNest", hasHadNest);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

        hasHadNest = tag.contains("hasHadNest") && tag.getBoolean("hasHadNest");
    }
}
