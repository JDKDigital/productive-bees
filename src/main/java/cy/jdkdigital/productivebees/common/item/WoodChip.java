package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WoodChip extends Item
{
    protected static final String KEY = "productivebees_woodtype";

    public WoodChip(Properties properties) {
        super(properties);
    }

    public static ItemStack getStack(Block block) {
        return getStack(block, 1);
    }

    public static ItemStack getStack(Block block, int count) {
        return getStack(block.getRegistryName().toString(), count);
    }

    public static ItemStack getStack(String blockName, int count) {
        ItemStack result = new ItemStack(ModItems.WOOD_CHIP.get(), count);
        setBlock(result, blockName);
        return result;
    }

    public static void setBlock(ItemStack stack, String blockName) {
        stack.getOrCreateTag().putString(KEY, blockName);
    }

    @Nullable
    public static Block getBlock(ItemStack stack) {
        if (!getBlockType(stack).isEmpty()) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(getBlockType(stack)));
        }
        return null;
    }

    public static String getBlockType(ItemStack stack) {
        return stack.getOrCreateTag().getString(KEY);
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
                BlockTags.LOGS.getAllElements().forEach(block -> {
                    if (block.getRegistryName() != null && block.getRegistryName().getPath().contains("log") &&  !block.getRegistryName().getPath().contains("stripped")) {
                        items.add(getStack(block));
                    }
                });
            } catch (IllegalStateException ise) {
                // tag not initialized yet
            }
        }
    }
}
