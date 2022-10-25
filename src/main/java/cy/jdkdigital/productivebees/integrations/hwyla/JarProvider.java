package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

public class JarProvider implements IComponentProvider, IServerDataProvider<BlockEntity>
{
    static final JarProvider INSTANCE = new JarProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof JarBlockEntity tileEntity)) {
            return;
        }

        tileEntity.loadPacketNBT(accessor.getServerData());

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            if (!handler.getStackInSlot(0).isEmpty()) {
                ItemStack cage = handler.getStackInSlot(0);
                if (cage.getItem() instanceof BeeCage && BeeCage.isFilled(cage)) {
                    Entity bee = tileEntity.getCachedEntity(cage);
                    if (bee instanceof Bee) {
                        tooltip.add(new TranslatableComponent("productivebees.top.jar.bee", bee.getDisplayName()));
                    }
                }
            }
        });
    }

    public void appendServerData(CompoundTag tag, ServerPlayer player, Level world, BlockEntity te, boolean showDetails) {
        tag.getAllKeys().clear();
        if (te instanceof JarBlockEntity jarBlockEntity) {
            jarBlockEntity.savePacketNBT(tag);
        }
    }
}
