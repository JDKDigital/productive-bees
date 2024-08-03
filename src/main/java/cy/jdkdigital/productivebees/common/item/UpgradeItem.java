package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.item.AbstractUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeItem extends AbstractUpgradeItem
{
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.add(Component.translatable("productivebees.information.upgrade.warning").withStyle(ChatFormatting.RED));

        if (pStack.getItem().equals(ModItems.UPGRADE_FILTER.get())) {
            return;
        }

        String upgradeType = BuiltInRegistries.ITEM.getKey(pStack.getItem()).getPath();

        double value = switch (upgradeType) {
            case "upgrade_breeding" -> ProductiveBeesConfig.UPGRADES.breedingChance.get();
            case "upgrade_time" -> ProductiveBeesConfig.UPGRADES.timeBonus.get();
            case "upgrade_productivity" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier.get();
            case "upgrade_productivity_2" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier2.get();
            case "upgrade_productivity_3" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier3.get();
            case "upgrade_productivity_4" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier4.get();
            default -> 0.0F;
        };

        pTooltipComponents.add(Component.translatable("productivebees.information.upgrade." + upgradeType, (int) (value * 100)).withStyle(ChatFormatting.GOLD));
        pTooltipComponents.add(Component.translatable("productivebees.information.upgrade.install_help").withStyle(ChatFormatting.GREEN));
    }
}
