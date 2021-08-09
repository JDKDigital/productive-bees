package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesHwylaPlugin implements IWailaPlugin
{
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider((tooltip, accessor, config) -> {
            if (!(accessor.getBlockEntity() instanceof SolitaryNestBlockEntity tileEntity)) {
                return;
            }

            List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> bees = tileEntity.getBeeList();
            if (!bees.isEmpty()) {
                tooltip.add(new TranslatableComponent("productivebees.top.solitary.bee", bees.get(0).localizedName));
            } else {
                int cooldown = tileEntity.getNestTickCooldown();
                if (cooldown > 0) {
                    tooltip.add(new TranslatableComponent("productivebees.top.solitary.repopulation_countdown", Math.round(cooldown / 20f) + "s"));
                } else {
                    tooltip.add(new TranslatableComponent("productivebees.top.solitary.repopulation_countdown_inactive"));
                    if (tileEntity.canRepopulate()) {
                        tooltip.add(new TranslatableComponent("productivebees.top.solitary.can_repopulate_true"));
                    } else {
                        tooltip.add(new TranslatableComponent("productivebees.top.solitary.can_repopulate_false"));
                    }
                }
            }
        }, TooltipPosition.BODY, SolitaryNest.class);
    }
}
