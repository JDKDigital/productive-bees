package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.common.tileentity.SolitaryNestTileEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class HUDHandlerSolitaryNest implements IComponentProvider
{
    static final HUDHandlerSolitaryNest INSTANCE = new HUDHandlerSolitaryNest();

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!(accessor.getTileEntity() instanceof SolitaryNestTileEntity)) {
            return;
        }
        SolitaryNestTileEntity tileEntity = (SolitaryNestTileEntity) accessor.getTileEntity();

        List<AdvancedBeehiveTileEntityAbstract.Inhabitant> bees = tileEntity.getBeeList();
        if (!bees.isEmpty()) {
            tooltip.add(new TranslationTextComponent("productivebees.top.solitary.bee", bees.get(0).localizedName));
        } else {
            int cooldown = tileEntity.getNestTickCooldown();
            if (cooldown > 0) {
                tooltip.add(new TranslationTextComponent("productivebees.top.solitary.repopulation_countdown", Math.round(cooldown / 20f) + "s"));
            } else {
                tooltip.add(new TranslationTextComponent("productivebees.top.solitary.repopulation_countdown_inactive"));
                if (tileEntity.canRepopulate()) {
                    tooltip.add(new TranslationTextComponent("productivebees.top.solitary.can_repopulate_true"));
                } else {
                    tooltip.add(new TranslationTextComponent("productivebees.top.solitary.can_repopulate_false"));
                }
            }
        }
    }
}
