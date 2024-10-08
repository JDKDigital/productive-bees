package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

public class MasonBee extends SolitaryBee
{
    public MasonBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        setAttributeValue(GeneAttribute.TEMPER, GeneValue.TEMPER_PASSIVE);
    }
}
