package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class JarProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>
{
    public static final ResourceLocation UID = new ResourceLocation(ProductiveBees.MODID, "jar");

    static final JarProvider INSTANCE = new JarProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof JarBlockEntity tileEntity)) {
            return;
        }

        tileEntity.loadPacketNBT(accessor.getServerData());

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (!handler.getStackInSlot(0).isEmpty()) {
                ItemStack cage = handler.getStackInSlot(0);
                if (cage.getItem() instanceof BeeCage && BeeCage.isFilled(cage)) {
                    Entity bee = tileEntity.getCachedEntity(cage);
                    if (bee instanceof Bee) {
                        tooltip.add(Component.translatable("productivebees.top.jar.bee", bee.getDisplayName()));
                    }
                }
            }
        });
    }

    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        tag.getAllKeys().clear();
        if (blockAccessor.getBlockEntity() instanceof JarBlockEntity jarBlockEntity) {
            jarBlockEntity.savePacketNBT(tag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
