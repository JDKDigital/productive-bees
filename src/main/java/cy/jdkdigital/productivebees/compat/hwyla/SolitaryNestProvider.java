package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class SolitaryNestProvider implements IBlockComponentProvider
{
    public static final Identifier UID = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "solitary_nest");

    static final SolitaryNestProvider INSTANCE = new SolitaryNestProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var serverData = accessor.getServerData();
        if (serverData.contains("inhabitantName")) {
            tooltip.add(Component.translatable("productivebees.top.solitary.bee", serverData.getString("inhabitantName").orElse("")));
        } else {
            int cooldown = serverData.getInt("nestTickCooldown").orElse(0);
            if (cooldown > 0) {
                tooltip.add(Component.translatable("productivebees.top.solitary.repopulation_countdown", Math.round(cooldown / 20f) + "s"));
            } else {
                tooltip.add(Component.translatable("productivebees.top.solitary.repopulation_countdown_inactive"));
                if (serverData.getBoolean("canRepopulate").orElse(false)) {
                    tooltip.add(Component.translatable("productivebees.top.solitary.can_repopulate_true"));
                } else {
                    tooltip.add(Component.translatable("productivebees.top.solitary.can_repopulate_false"));
                }
            }
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
