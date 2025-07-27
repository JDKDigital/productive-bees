package cy.jdkdigital.productivebees.ai;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BeeAggressiveGoal extends Goal
{
    private final Bee bee;

    public BeeAggressiveGoal(Bee bee) {
        this.bee = bee;
    }

    @Override
    public void tick() {
        List<Player> players = bee.level().getEntitiesOfClass(Player.class, new AABB(bee.blockPosition()).inflate(10, 5, 10));
        if (!players.isEmpty()) {
            bee.setTarget(players.getFirst());
        }
    }

    @Override
    public boolean canUse() {
        var attributes = bee.getData(ProductiveBees.ATTRIBUTE_HANDLER);
        return !bee.isAngry() && !bee.hasStung() && attributes.getAttributeValue(GeneAttribute.TEMPER).equals(GeneValue.TEMPER_AGGRESSIVE);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }
}
