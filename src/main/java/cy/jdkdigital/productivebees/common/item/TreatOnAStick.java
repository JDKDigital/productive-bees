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
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (world.isRemote) {
            return ActionResult.resultPass(itemStack);
        } else {
            if (player.isPassenger() && player.getRidingEntity() instanceof BumbleBeeEntity) {
                BumbleBeeEntity bumbleBee = (BumbleBeeEntity) player.getRidingEntity();
                if (bumbleBee.boost()) {
                    itemStack.damageItem(7, player, (entity) -> {
                        entity.sendBreakAnimation(hand);
                    });
                    if (itemStack.isEmpty()) {
                        ItemStack rodStack = new ItemStack(Items.FISHING_ROD);
                        rodStack.setTag(itemStack.getTag());
                        return ActionResult.resultSuccess(rodStack);
                    }

                    return ActionResult.resultSuccess(itemStack);
                }
            }

            player.addStat(Stats.ITEM_USED.get(this));
            return ActionResult.resultPass(itemStack);
        }
    }
}