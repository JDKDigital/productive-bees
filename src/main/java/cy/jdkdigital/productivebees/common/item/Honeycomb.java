package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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

    public int getColor(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return getColor();
    }

    @Nonnull
    @Override
    public ITextComponent getName(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                String name = new TranslationTextComponent("entity.productivebees." + ProductiveBeeEntity.getBeeName(tag.getString("type")) + "_bee").getString();
                return new TranslationTextComponent("item.productivebees.honeycomb_configurable", name.replace(" Bee", ""));
            }
        }
        return super.getName(stack);
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (!this.equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
            super.fillItemCategory(group, items);
        } else if (group == ItemGroup.TAB_SEARCH) {
            for (Map.Entry<String, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
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
