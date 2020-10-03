package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class UpgradeItem extends Item
{
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, world, tooltip, flagIn);

        String upgradeType = stack.getItem().getRegistryName().getPath();

        double value = 0.0F;
        switch (upgradeType) {
            case "upgrade_productivity":
                value = ProductiveBeesConfig.UPGRADES.productivityMultiplier.get();
                break;
            case "upgrade_breeding":
                value = ProductiveBeesConfig.UPGRADES.breedingChance.get();
                break;
            case "upgrade_time":
                value = ProductiveBeesConfig.UPGRADES.timeBonus.get();
                break;
            case "upgrade_comb_block":
                value = ProductiveBeesConfig.UPGRADES.combBlockTimeodifier.get();
                break;
        }

        tooltip.add(new TranslationTextComponent("productivebees.information.upgrade." + upgradeType, (int) (value * 100)).applyTextStyle(TextFormatting.GOLD));
    }
}
