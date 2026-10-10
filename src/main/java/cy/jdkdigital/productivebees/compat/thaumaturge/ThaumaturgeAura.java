package cy.jdkdigital.productivebees.compat.thaumaturge;

import com.leclowndu93150.thaumaturge.api.aura.AuraHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class ThaumaturgeAura
{
    public static float cleanseFlux(ServerLevel level, BlockPos pos, float amount) {
        return AuraHelper.drainFlux(level, pos, amount, false);
    }
}
