package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class HoneyGeneratorItem extends BlockItem
{
    public HoneyGeneratorItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.add(Component.translatable("productivebees.information.upgrade.valid_blocks.list_item_content", ProductiveBeesConfig.GENERAL.generatorPowerGen).withStyle(ChatFormatting.GRAY));
    }
}
