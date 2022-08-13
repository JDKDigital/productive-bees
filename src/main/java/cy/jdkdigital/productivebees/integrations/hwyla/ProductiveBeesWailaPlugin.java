package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.util.BeeHelper;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesWailaPlugin implements IWailaPlugin
{
    public static final ResourceLocation BEE_ATTRIBUTES = new ResourceLocation(ProductiveBees.MODID, "bee_attributes");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(SolitaryNestProvider.INSTANCE, TooltipPosition.BODY, SolitaryNest.class);
        registrar.registerBlockDataProvider(SolitaryNestProvider.INSTANCE, SolitaryNestBlockEntity.class);
        registrar.registerEntityDataProvider(ProductiveBeeProvider.INSTANCE, ProductiveBee.class);
        registrar.addConfig(BEE_ATTRIBUTES, true);
    }
}
