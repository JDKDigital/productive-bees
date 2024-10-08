package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class Honeycomb extends HoneycombItem
{
    private final int color;

    public Honeycomb(Properties properties, String colorCode) {
        super(properties);
        this.color = TextColor.parseColor(colorCode).result().get().getValue();
    }

    public int getColor() {
        return color;
    }

    public int getColor(ItemStack stack, int tintIndex) {
        var type = stack.get(ModDataComponents.BEE_TYPE);
        if (type != null) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(type);
            if (nbt != null) {
                return tintIndex == 0 ? nbt.getInt("primaryColor") : nbt.getInt("tertiaryColor");
            }
        }
        return getColor();
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        var type = stack.get(ModDataComponents.BEE_TYPE);
        if (type != null) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(type);
            if (nbt != null) {
                String name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(type) + "_bee").getString();
                return Component.translatable("item.productivebees.honeycomb_configurable", name.replace(" Bee", ""));
            }
        }
        return super.getName(stack);
    }
}
