package cy.jdkdigital.productivebees.compat.curios;

import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosCompat
{
    public static boolean isWearingBeeHelmet(LivingEntity player) {
        return CuriosApi.getCuriosInventory(player).map(iCuriosItemHandler -> {
            return iCuriosItemHandler.findFirstCurio(ModItems.BEE_NEST_DIAMOND_HELMET.get()).isPresent();
        }).orElse(false);
    }

    public static void registerRenderers() {
        CuriosRendererRegistry.register(ModItems.BEE_NEST_DIAMOND_HELMET.get(), BeeHelmetRenderer::new);
    }
}
