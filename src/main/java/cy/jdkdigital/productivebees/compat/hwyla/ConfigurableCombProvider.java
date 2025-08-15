package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.CombBlockBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class ConfigurableCombProvider implements IBlockComponentProvider {
    static final ConfigurableCombProvider INSTANCE = new ConfigurableCombProvider();
    public static final ResourceLocation UID = new ResourceLocation(ProductiveBees.MODID, "configurable_comb");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof CombBlockBlockEntity tileEntity)) {
            return;
        }
        tooltip.clear();
        tooltip.add(getName(tileEntity.getCombType()));

    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private Component getName(String type) {
        if (type != null && !type.isEmpty()) {

            String name = Component.translatable("entity.productivebees." + type.split(":")[1] + "_bee").getString();
            return Component.translatable("block.productivebees.comb_configurable", name.replace(" Bee", "")).withStyle(ChatFormatting.WHITE);

        }
        return Component.translatable("block.productivebees.comb_configurable").withStyle(ChatFormatting.WHITE);
    }
}
