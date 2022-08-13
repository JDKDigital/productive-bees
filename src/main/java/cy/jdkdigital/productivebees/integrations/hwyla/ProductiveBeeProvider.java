package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.util.BeeHelper;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ProductiveBeeProvider implements IEntityComponentProvider, IServerDataProvider<Entity>
{
    public static final ProductiveBeeProvider INSTANCE = new ProductiveBeeProvider();

    public ProductiveBeeProvider() {
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (config.get(ProductiveBeesWailaPlugin.BEE_ATTRIBUTES) && accessor.getServerData().contains("isProductiveBee")) {
            List<Component> list =  new ArrayList<>();
            BeeHelper.populateBeeInfoFromTag(accessor.getServerData(), list);
            for (Component component: list) {
                tooltip.add(component);
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, Entity entity, boolean showDetails) {
        if (entity instanceof ProductiveBee bee && showDetails) {
            bee.saveWithoutId(compoundTag);
            compoundTag.putBoolean("isProductiveBee", true);
            AdvancedBeehiveBlockEntityAbstract.removeIgnoredBeeTags(compoundTag);
        }
    }
}
