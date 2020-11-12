package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class StoneChip extends WoodChip
{
    public StoneChip(Properties properties) {
        super(properties);
    }

    public static ItemStack getStack(Block block) {
        return getStack(block, 1);
    }

    public static ItemStack getStack(Block block, int count) {
        return getStack(block.getRegistryName().toString(), count);
    }

    public static ItemStack getStack(String blockName, int count) {
        ItemStack result = new ItemStack(ModItems.STONE_CHIP.get(), count);
        setBlock(result, blockName);
        return result;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        Block block = getBlock(stack);
        if (block != null) {
            return new TranslationTextComponent(this.getTranslationKey() + ".named", new TranslationTextComponent(block.getTranslationKey()));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            try {
                ModTags.QUARRY.getAllElements().forEach(block -> {
                    if (block.getRegistryName() != null && !block.getRegistryName().getPath().contains("infested")) {
                        items.add(getStack(block));
                    }
                });
            } catch (IllegalStateException ise) {
                // tag not initialized yet
            }
        }
    }
}
