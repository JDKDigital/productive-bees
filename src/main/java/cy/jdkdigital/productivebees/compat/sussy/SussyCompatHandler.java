package cy.jdkdigital.productivebees.compat.sussy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SussyCompatHandler
{
    public static List<ResourceLocation> getLootTables(ServerLevel level, BlockPos pos) {
        List<ResourceLocation> possibleTables = new ArrayList<>();
        if (SussyMinecraft.isBlockValid(level, pos)) {
            possibleTables.addAll(SussyMinecraft.getLootTables(level, pos));
        }
        if (ModList.get().isLoaded("allthemodium")) {
            if (SussyATM.isBlockValid(level, pos)) {
                possibleTables.addAll(SussyATM.getLootTables(level, pos));
            }
        }
        return possibleTables;
    }
}
