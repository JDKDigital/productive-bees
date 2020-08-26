package cy.jdkdigital.productivebees.integrations.hwyla;

import mcp.mobius.waila.api.*;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

@WailaPlugin
public class ProductiveBeesHwylaPlugin implements IWailaPlugin
{
    @Override
    public void register(IRegistrar registrar) {
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