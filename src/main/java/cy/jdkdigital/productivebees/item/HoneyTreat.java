package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class HoneyTreat extends Item
{
    public HoneyTreat(Properties properties) {
        super(properties);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target.getEntityWorld().isRemote() || (!(target instanceof BeeEntity) || !target.isAlive())) {
            return false;
        }

        BeeEntity bee = (BeeEntity) target;

        // Stop agro
        bee.setRevengeTarget(null);
        // Allow entering hive
        bee.setStayOutOfHiveCountdown(0);
        // Heal
        bee.heal(bee.getMaxHealth());

        if (bee.isChild()) {
            bee.ageUp((int) ((float) (-bee.getGrowingAge() / 20) * 0.1F), true);
        }

        itemStack.shrink(1);

        BlockPos pos = target.getPosition();
        target.getEntityWorld().addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);

        // Improve temper
        if (bee instanceof ProductiveBeeEntity) {
            ProductiveBeeEntity productiveBee = (ProductiveBeeEntity) target;
            int temper = productiveBee.getAttributeValue(BeeAttributes.TEMPER);
            if (temper > 0) {
                if (player.world.rand.nextFloat() < 0.05F) {
                    productiveBee.getBeeAttributes().put(BeeAttributes.TEMPER, --temper);
                }
            }
        }

        return true;
    }
}
