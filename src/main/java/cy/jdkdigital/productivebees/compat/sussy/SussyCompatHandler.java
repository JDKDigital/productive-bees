package cy.jdkdigital.productivebees.compat.sussy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class SussyCompatHandler
{
    public static List<ResourceKey<LootTable>> getLootTables(ServerLevel level, BlockPos pos) {
        List<ResourceKey<LootTable>> possibleTables = new ArrayList<>();
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
