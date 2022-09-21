package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class SturdyBeeCage extends BeeCage
{
    public SturdyBeeCage(Properties properties) {
        super(properties);
    }

    @Override
    protected void postItemUse(UseOnContext context) {
        context.getItemInHand().setTag(null);
        // Shrink stack and add empty to player inv
        if (context.getPlayer() != null) {
            if (!context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
                ItemStack itemstack = new ItemStack(ModItems.STURDY_BEE_CAGE.get());
                if (!context.getPlayer().getInventory().add(itemstack)) {
                    context.getPlayer().drop(itemstack, false);
                }
            }
        }
    }
}
