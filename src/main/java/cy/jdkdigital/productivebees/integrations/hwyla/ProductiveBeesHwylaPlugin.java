package cy.jdkdigital.productivebees.integrations.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.common.tileentity.SolitaryNestTileEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
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

                TileEntity tile = accessor.getTileEntity();
                if (tile instanceof SolitaryNestTileEntity) {
                    List<AdvancedBeehiveTileEntityAbstract.Inhabitant> bees = ((SolitaryNestTileEntity) tile).getBeeList();
                    if (!bees.isEmpty()) {
                        tooltip.add(new TranslationTextComponent("productivebees.top.solitary.bee", bees.get(0).localizedName));
//                    } else {
//                        int cooldown = ((SolitaryNestTileEntity) tile).getNestTickCooldown();
//                        if (cooldown > 0) {
//                            tooltip.add(new TranslationTextComponent("productivebees.top.solitary.repopulation_countdown", Math.round(cooldown / 20f) + "s"));
//                        } else {
//                            tooltip.add(new TranslationTextComponent("productivebees.top.solitary.repopulation_countdown_inactive"));
//                            if (((SolitaryNestTileEntity) tile).canRepopulate()) {
//                                tooltip.add(new TranslationTextComponent("productivebees.top.solitary.can_repopulate_true"));
//                            } else {
//                                tooltip.add(new TranslationTextComponent("productivebees.top.solitary.can_repopulate_false"));
//                            }
//                        }
                    }
                }
            }
        }, TooltipPosition.BODY, Block.class);
    }
}