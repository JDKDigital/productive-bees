package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class CanvasBeehiveProvider implements IBlockComponentProvider
{
    public static final ResourceLocation UID = new ResourceLocation(ProductiveBees.MODID, "canvas_beehive");

    static final CanvasBeehiveProvider INSTANCE = new CanvasBeehiveProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof CanvasBeehiveBlockEntity tileEntity)) {
            return;
        }

        String style = accessor.getBlockState().getBlock().getDescriptionId().replace("block.productivebees.advanced_", "").replace("_canvas_beehive", "");
        style = style.substring(0, 1).toUpperCase() + style.substring(1);
        tooltip.add(Component.translatable("productivebees.information.canvas.style", Component.literal(style).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
