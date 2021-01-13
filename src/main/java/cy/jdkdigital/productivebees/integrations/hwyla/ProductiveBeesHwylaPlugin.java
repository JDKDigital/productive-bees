package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.SolitaryNestTileEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesHwylaPlugin implements IWailaPlugin
{
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerSolitaryNest.INSTANCE, TooltipPosition.BODY, SolitaryNestTileEntity.class);

        registrar.registerComponentProvider(new IComponentProvider()
        {
            @Override
            public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
                BlockState state = accessor.getBlockState();

                if (state.has(BeehiveBlock.HONEY_LEVEL)) {
                    int honeyLevel = state.get(BeehiveBlock.HONEY_LEVEL);
                    tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.honey_level", honeyLevel));
                }
            }
        }, TooltipPosition.BODY, Block.class);
    }
}