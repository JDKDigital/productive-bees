package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class SolitaryNestProvider implements IComponentProvider, IServerDataProvider<BlockEntity>
{
    static final SolitaryNestProvider INSTANCE = new SolitaryNestProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SolitaryNestBlockEntity tileEntity)) {
            return;
        }

        tileEntity.loadPacketNBT(accessor.getServerData());

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
    }

    public void appendServerData(CompoundTag tag, ServerPlayer player, Level world, BlockEntity te, boolean showDetails) {
        tag.getAllKeys().clear();
        if (te instanceof SolitaryNestBlockEntity nest) {
            nest.savePacketNBT(tag);
        }
    }
}
