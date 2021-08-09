package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SolitaryBee extends ProductiveBee
{
    public SolitaryBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() || poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
        beeAttributes.put(BeeAttributes.TYPE, "solitary");
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.removeGoal(this.followParentGoal);
    }

    @Override
    public Tag<Block> getNestingTag() {
        return ModTags.SOLITARY_OVERWORLD_NESTS;
    }
}
