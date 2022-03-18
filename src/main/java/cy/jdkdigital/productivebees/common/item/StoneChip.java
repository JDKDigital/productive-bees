package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.item.StoneChipRenderer;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

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
    public Component getName(@Nonnull ItemStack stack) {
        Block block = getBlock(stack);
        if (block != null) {
            return new TranslatableComponent(this.getDescriptionId() + ".named", new TranslatableComponent(block.getDescriptionId()));
        }
        return super.getName(stack);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            try {
                Registry.BLOCK.getTagOrEmpty(ModTags.QUARRY).forEach(blockHolder -> {
                    Block block = blockHolder.value();
                    if (block.getRegistryName() != null && !block.getRegistryName().getPath().contains("infested")) {
                        items.add(getStack(block));
                    }
                });
            } catch (IllegalStateException ise) {
                // tag not initialized yet
            }
        }
    }



    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties()
        {
            final BlockEntityWithoutLevelRenderer myRenderer = new StoneChipRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return myRenderer;
            }
        });
    }

    public static ShapelessRecipe getRecipe(Block b) {
        ItemStack chip = getStack(b);
        String[] id = b.getRegistryName().toString().split(":");
        NonNullList<Ingredient> list = NonNullList.create();
        for (int i = 0; i < 9; ++i) {
            Ingredient ingredient = Ingredient.of(chip);
            if (!ingredient.isEmpty()) {
                list.add(ingredient);
            }
        }
        return new ShapelessRecipe(new ResourceLocation(ProductiveBees.MODID, "stone_chip_" + id[1]), "", new ItemStack(b.asItem()), list);
    }
}
