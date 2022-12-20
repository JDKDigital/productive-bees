package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public class Honeycomb extends Item
{
    private final int color;

    public Honeycomb(Properties properties, String colorCode) {
        super(properties);
        this.color = TextColor.parseColor(colorCode).getValue();
    }

    public int getColor() {
        return color;
    }

    public int getColor(ItemStack stack, int tintIndex) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return tintIndex == 0 ? nbt.getInt("primaryColor") : nbt.getInt("tertiaryColor");
            }
        }
        return getColor();
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                String name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(tag.getString("type")) + "_bee").getString();
                return Component.translatable("item.productivebees.honeycomb_configurable", name.replace(" Bee", ""));
            }
        }
        return super.getName(stack);
    }
}
