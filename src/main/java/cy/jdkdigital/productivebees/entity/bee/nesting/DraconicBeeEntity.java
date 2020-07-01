package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;

public class DraconicBeeEntity extends ProductiveBeeEntity
{
    public int breathCollectionCooldown = 600;

    public DraconicBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == PointOfInterestType.BEEHIVE || poiType == ModPointOfInterestTypes.DRACONIC_NEST.get();
        beeAttributes.put(BeeAttributes.TEMPER, 2);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.DRACONIC_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.DRACONIC_NESTS);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            if (--breathCollectionCooldown <= 0) {
                breathCollectionCooldown = 600;
                if (this.world.func_234923_W_() == World.field_234920_i_) {
                    this.setHasNectar(true);
                }
            }
        }
    }
}
