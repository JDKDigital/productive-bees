package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ExpirableBee;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.HashMap;

public class QuartzBeeEntity extends ProductiveBeeEntity implements ExpirableBee
{
    public boolean hasHadNest = false;

    public QuartzBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.CRYSTALLINE_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.NETHER_QUARTZ_NESTS);
        beeAttributes.put(BeeAttributes.EFFECTS, new BeeEffect(new HashMap<Effect, Integer>() {{
            put(Effects.RESISTANCE, 600);
        }}));
    }

    @Override
    public void setHasHadNest(boolean hadNest) {
        this.hasHadNest = hadNest;
    }

    @Override
    public boolean getHasHadNest() {
        return hasHadNest;
    }
}
