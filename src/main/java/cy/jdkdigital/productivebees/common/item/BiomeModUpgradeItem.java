package cy.jdkdigital.productivebees.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BiomeModUpgradeItem extends UpgradeItem
{
    private static final String KEY = "productivebees_biome_mod";

    public BiomeModUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            CompoundTag data = tag.getCompound(KEY);
            if (data.contains("biome")) {
                String biome = data.getString("biome");
                tooltip.add(Component.literal(biome).withStyle(ChatFormatting.GOLD));
            }
            else {
                tooltip.add(Component.translatable("productivebees.information.upgrade.unconfigured").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
