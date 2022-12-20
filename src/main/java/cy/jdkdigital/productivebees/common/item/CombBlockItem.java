package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.Map;

public class CombBlockItem extends BlockItem
{
    public CombBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                String name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(tag.getString("type")) + "_bee").getString();
                return Component.translatable("block.productivebees.comb_configurable", name.replace(" Bee", ""));
            }
        }
        return super.getName(stack);
    }
}
