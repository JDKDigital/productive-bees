package cy.jdkdigital.productivebees.item;

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
    private static final String WOOD_KEY = "productivebees_woodtype";

    public WoodChip(Properties properties) {
        super(properties);

        // "parent": "builtin/entity",
    }

    public static ItemStack getStack(Block block) {
        return getStack(block, 1);
    }

    public static ItemStack getStack(Block block, int count) {
        return getStack(block.getRegistryName().toString(), count);
    }

    public static ItemStack getStack(String blockName, int count) {
        ItemStack result = new ItemStack(ModItems.WOOD_CHIP.get(), count);
        setWoodBlock(result, blockName);
        return result;
    }

    public static void setWoodBlock(ItemStack stack, String blockName) {
        stack.getOrCreateTag().putString(WOOD_KEY, blockName);
    }

    @Nullable
    public static Block getWoodBlock(ItemStack stack) {
        if (!getWoodType(stack).isEmpty()) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(getWoodType(stack)));
        }
        return null;
    }

    public static String getWoodType(ItemStack stack) {
        return stack.getOrCreateTag().getString(WOOD_KEY);
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        Block block = getWoodBlock(stack);
        if (block != null) {
            ITextComponent entityName = block.func_235333_g_(); // getNameTextComponent
            return new TranslationTextComponent(this.getTranslationKey() + ".named", entityName);
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            BlockTags.LOGS.func_230236_b_().forEach(block -> {
                if (block.getRegistryName() != null && block.getRegistryName().getPath().contains("log") &&  !block.getRegistryName().getPath().contains("stripped")) {
                    items.add(getStack(block));
                }
            });
        }
    }
}
