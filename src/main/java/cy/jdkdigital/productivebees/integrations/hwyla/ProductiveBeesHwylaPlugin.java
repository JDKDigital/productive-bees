package cy.jdkdigital.productivebees.integrations.hwyla;
//
//import cy.jdkdigital.productivebees.init.ModTags;
//import mcp.mobius.waila.api.*;
//import net.minecraft.block.BeehiveBlock;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.TranslationTextComponent;
//
//import java.util.List;
//
//@WailaPlugin
//public class ProductiveBeesHwylaPlugin implements IWailaPlugin
//{
//    @Override
//    public void register(IRegistrar registrar) {
//        registrar.registerComponentProvider(new IComponentProvider()
//        {
//            @Override
//            public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
//                BlockState state = accessor.getBlockState();
////                Block block = accessor.getBlock();
//                if (state.has(BeehiveBlock.HONEY_LEVEL) && !state.isIn(ModTags.SOLITARY_NESTS)) {
//                    int honeyLevel = state.get(BeehiveBlock.HONEY_LEVEL);
//                    tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.honey_level", honeyLevel));
////                }
////                else if (state.isIn(ModTags.SOLITARY_NESTS)) {
//                }
//            }
//        }, TooltipPosition.BODY, Block.class);
//    }
//}