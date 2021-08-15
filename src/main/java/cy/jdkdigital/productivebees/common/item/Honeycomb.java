package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Map;

public class Honeycomb extends Item
{
    private final int color;

    public Honeycomb(Properties properties, String colorCode) {
        super(properties);
        this.color = Color.decode(colorCode).getRGB();
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
                return new TranslatableComponent("item.productivebees.honeycomb_configurable", nbt.getString("name").replace(" Bee", ""));
            }
        }
        return super.getName(stack);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (!this.equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
            super.fillItemCategory(group, items);
        } else if (group == CreativeModeTab.TAB_SEARCH) {
            for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                String beeType = entry.getKey();
                if (entry.getValue().getBoolean("createComb")) {
                    ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    BeeCreator.setTag(beeType, comb);
                    items.add(comb);
                }
            }
        }
    }
}
