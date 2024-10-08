package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class SolitaryNestProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>
{
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "solitary_nest");

    static final SolitaryNestProvider INSTANCE = new SolitaryNestProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SolitaryNestBlockEntity tileEntity)) {
            return;
        }

        tileEntity.loadPacketNBT(accessor.getServerData(), accessor.getLevel().registryAccess());

        if (accessor.getServerData().contains("inhabitantName")) {
            tooltip.add(Component.translatable("productivebees.top.solitary.bee", accessor.getServerData().getString("inhabitantName")));
        } else {
            int cooldown = tileEntity.getNestTickCooldown();
            if (cooldown > 0) {
                tooltip.add(Component.translatable("productivebees.top.solitary.repopulation_countdown", Math.round(cooldown / 20f) + "s"));
            } else {
                tooltip.add(Component.translatable("productivebees.top.solitary.repopulation_countdown_inactive"));
                if (accessor.getServerData().getBoolean("canRepopulate")) {
                    tooltip.add(Component.translatable("productivebees.top.solitary.can_repopulate_true"));
                } else {
                    tooltip.add(Component.translatable("productivebees.top.solitary.can_repopulate_false"));
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        tag.getAllKeys().clear();
        if (blockAccessor.getBlockEntity() instanceof SolitaryNestBlockEntity nest) {
            nest.savePacketNBT(tag, blockAccessor.getLevel().registryAccess());
            tag.putBoolean("canRepopulate", nest.canRepopulate());
            if (!nest.isEmpty()) {
                var type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(nest.stored.get(0).occupant.entityData().getUnsafe().getString("id")));
                tag.putString("inhabitantName", Component.translatable(type.getDescriptionId()).getString());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
