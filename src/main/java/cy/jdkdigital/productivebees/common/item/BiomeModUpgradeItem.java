package cy.jdkdigital.productivebees.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BiomeModUpgradeItem extends UpgradeItem
{
    private static final String KEY = "productivebees_biome_mod";

    public BiomeModUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            CompoundNBT data = tag.getCompound(KEY);
            if (data.contains("biome")) {
                String biome = data.getString("biome");
                tooltip.add(new StringTextComponent(biome).withStyle(TextFormatting.GOLD));
            }
            else {
                tooltip.add(new TranslationTextComponent("productivebees.information.upgrade.unconfigured").withStyle(TextFormatting.WHITE));
            }
        }
    }
}
