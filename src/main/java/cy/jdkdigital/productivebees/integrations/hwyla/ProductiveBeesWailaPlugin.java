package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesWailaPlugin implements IWailaPlugin
{
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(SolitaryNestProvider.INSTANCE, TooltipPosition.BODY, SolitaryNest.class);
        registrar.registerBlockDataProvider(SolitaryNestProvider.INSTANCE, SolitaryNestBlockEntity.class);
    }
}
