package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.awt.*;

public class Honeycomb extends Item
{
    private final int color;

    public Honeycomb(Properties properties, String colorCode) {
        super(properties);
        this.color = Color.decode(colorCode).getRGB();
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(new ResourceLocation(tag.getString("type")));
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return getColor();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(new ResourceLocation(tag.getString("type")));
            if (nbt != null) {
                return new TranslationTextComponent("item.productivebees.honeycomb_configurable", nbt.getString("name"));
            }
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (!this.equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
            super.fillItemGroup(group, items);
        }
    }
}
