package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.ITag;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SolitaryBeeEntity extends ProductiveBeeEntity
{
    public SolitaryBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() || poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
        beeAttributes.put(BeeAttributes.TYPE, "solitary");
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.removeGoal(this.followParentGoal);
    }

    @Nonnull
    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(0.85F);
    }

    @Override
    public ITag<Block> getNestingTag() {
        return ModTags.SOLITARY_OVERWORLD_NESTS;
    }
}
