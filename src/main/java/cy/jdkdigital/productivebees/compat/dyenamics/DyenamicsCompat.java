package cy.jdkdigital.productivebees.compat.dyenamics;

import cy.jdkdigital.dyenamics.common.item.DyenamicDyeItem;
import net.minecraft.world.item.ItemStack;

public class DyenamicsCompat
{
    public static boolean isDye(ItemStack pStack) {
        return pStack.getItem() instanceof DyenamicDyeItem;
    }

    public static int getColor(ItemStack pStack) {
        return pStack.getItem() instanceof DyenamicDyeItem dye ? dye.getDyeColor().getColorValue() : 0;
    }
}
