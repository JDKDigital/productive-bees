package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.init.ModItemGroups;
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
import java.util.Map;

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
        } else {
            for (Map.Entry<ResourceLocation, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                String beeType = entry.getKey().toString();

                // Add spawn egg item
                ItemStack egg = new ItemStack(ModItems.CONFIGURABLE_SPAWN_EGG.get());
                ModItemGroups.ModItemGroup.setTag(beeType, egg);

                items.add(egg);

                // Add comb item
                ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                ModItemGroups.ModItemGroup.setTag(beeType, comb);

                items.add(comb);

                // Add comb block
                ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
                ModItemGroups.ModItemGroup.setTag(beeType, combBlock);

                items.add(combBlock);
            }
        }
    }
}
