package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class CombBlockItem extends BlockItem
{
    public CombBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static int getColor(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return new TranslationTextComponent("block.productivebees.comb_configurable", nbt.getString("name").replace(" Bee", ""));
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
