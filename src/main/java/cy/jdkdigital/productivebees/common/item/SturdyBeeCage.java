package cy.jdkdigital.productivebees.common.item;

import net.minecraft.item.ItemUseContext;

public class SturdyBeeCage extends BeeCage
{
    public SturdyBeeCage(Properties properties) {
        super(properties);
    }

    @Override
    protected void postItemUse(ItemUseContext context) {
        context.getItemInHand().setTag(null);
    }
}
