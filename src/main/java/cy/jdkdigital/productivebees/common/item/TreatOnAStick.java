package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.solitary.BumbleBee;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class TreatOnAStick extends Item
{
    public TreatOnAStick(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return InteractionResultHolder.pass(itemStack);
        } else {
            if (player.isPassenger() && player.getControllingPassenger() instanceof BumbleBee) {
                BumbleBee bumbleBee = (BumbleBee) player.getControllingPassenger();
                if (bumbleBee.boost()) {
                    itemStack.hurtAndBreak(7, player, (entity) -> {
                        entity.broadcastBreakEvent(hand);
                    });
                    if (itemStack.isEmpty()) {
                        ItemStack rodStack = new ItemStack(Items.FISHING_ROD);
                        rodStack.setTag(itemStack.getTag());
                        return InteractionResultHolder.success(rodStack);
                    }

                    return InteractionResultHolder.success(itemStack);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.pass(itemStack);
        }
    }
}