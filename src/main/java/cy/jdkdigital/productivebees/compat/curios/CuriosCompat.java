package cy.jdkdigital.productivebees.compat.curios;

import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosCompat
{
    public static boolean isWearingBeeHelmet(LivingEntity player) {
        return CuriosApi.getCuriosInventory(player).map(iCuriosItemHandler -> {
            return iCuriosItemHandler.findFirstCurio(ModItems.BEE_NEST_DIAMOND_HELMET.get()).isPresent();
        }).orElse(false);
    }

    public static void registerRenderers() {
    }
}
