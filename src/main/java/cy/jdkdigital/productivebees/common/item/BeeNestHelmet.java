package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class BeeNestHelmet extends Item
{
    public BeeNestHelmet(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltipDisplay, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.accept(Component.translatable("productivebees.information.bee_helmet.info1").withStyle(ChatFormatting.DARK_PURPLE));
        pTooltipComponents.accept(Component.translatable("productivebees.information.bee_helmet.info2").withStyle(ChatFormatting.LIGHT_PURPLE));
        pTooltipComponents.accept(Component.translatable("productivebees.information.bee_helmet.info3", 100 * ProductiveBeesConfig.BEES.kamikazBeeChance.get()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
