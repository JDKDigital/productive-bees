package cy.jdkdigital.productivebees.common.item;

import net.minecraft.world.item.context.UseOnContext;

public class SturdyBeeCage extends BeeCage
{
    public SturdyBeeCage(Properties properties) {
        super(properties);
    }

    @Override
    protected void postItemUse(UseOnContext context) {
        context.getItemInHand().setTag(null);
    }
}
