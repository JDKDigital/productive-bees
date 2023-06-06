package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.CanvasExpansionBoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class CanvasExpansionBoxProvider implements IBlockComponentProvider
{
    public static final ResourceLocation UID = new ResourceLocation(ProductiveBees.MODID, "canvas_beehive");

    static final CanvasExpansionBoxProvider INSTANCE = new CanvasExpansionBoxProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof CanvasExpansionBoxBlockEntity tileEntity)) {
            return;
        }

        String style = accessor.getBlockState().getBlock().getDescriptionId().replace("block.productivebees.expansion_box_", "").replace("_canvas", "");
        style = style.substring(0, 1).toUpperCase() + style.substring(1);
        tooltip.add(Component.translatable("productivebees.information.canvas.style", Component.literal(style).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
