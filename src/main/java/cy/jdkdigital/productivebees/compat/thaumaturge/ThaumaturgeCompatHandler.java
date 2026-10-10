package cy.jdkdigital.productivebees.compat.thaumaturge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.ModList;

public class ThaumaturgeCompatHandler
{
    public static final String MODID = "thaumaturge";

    public static boolean restoreNode(ServerLevel level, BlockPos pos, int amount) {
        return ModList.get().isLoaded(MODID) && ThaumaturgeNodes.restoreNode(level, pos, amount);
    }

    public static float cleanseFlux(ServerLevel level, BlockPos pos, float amount) {
        return ModList.get().isLoaded(MODID) ? ThaumaturgeAura.cleanseFlux(level, pos, amount) : 0f;
    }
}
