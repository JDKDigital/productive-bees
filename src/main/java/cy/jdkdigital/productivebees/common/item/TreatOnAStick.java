package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.solitary.BumbleBeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TreatOnAStick extends Item
{
    public TreatOnAStick(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return ActionResult.pass(itemStack);
        } else {
            if (player.isPassenger() && player.getControllingPassenger() instanceof BumbleBeeEntity) {
                BumbleBeeEntity bumbleBee = (BumbleBeeEntity) player.getControllingPassenger();
                if (bumbleBee.boost()) {
                    itemStack.hurtAndBreak(7, player, (entity) -> {
                        entity.broadcastBreakEvent(hand);
                    });
                    if (itemStack.isEmpty()) {
                        ItemStack rodStack = new ItemStack(Items.FISHING_ROD);
                        rodStack.setTag(itemStack.getTag());
                        return ActionResult.success(rodStack);
                    }

                    return ActionResult.success(itemStack);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return ActionResult.pass(itemStack);
        }
    }
}