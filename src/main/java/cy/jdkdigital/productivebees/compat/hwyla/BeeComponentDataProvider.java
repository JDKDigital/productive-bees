package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Client component provider for bee tooltips. The matching server-side data collector is
 * {@link BeeServerDataProvider}. Jade 26.1 requires the two responsibilities split across classes.
 */
public class BeeComponentDataProvider implements IEntityComponentProvider
{
    public static final Identifier UID = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee");
    public static final BeeComponentDataProvider INSTANCE = new BeeComponentDataProvider();

    public BeeComponentDataProvider() {
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (config.get(ProductiveBeesWailaPlugin.BEE_ATTRIBUTES)) {
            List<Component> list = new ArrayList<>();
            BeeHelper.populateBeeInfoFromTag(accessor.getServerData(), list, true);
            for (Component component : list) {
                tooltip.add(component);
            }
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
