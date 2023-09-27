package cy.jdkdigital.productivebees.compat.sussy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.ArrayList;
import java.util.List;

public class SussyMinecraft
{
    public static boolean isBlockValid(ServerLevel level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block.equals(Blocks.SUSPICIOUS_SAND) || block.equals(Blocks.SUSPICIOUS_GRAVEL);
    }

    public static List<ResourceLocation> getLootTables(ServerLevel level, BlockPos pos) {
        var biome = level.getBiome(pos);
        List<ResourceLocation> possibleTables = new ArrayList<>();
        if (biome.is(BiomeTags.HAS_TRAIL_RUINS)) {
            if (level.getRandom().nextInt(100) < 10) {
                possibleTables.add(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE);
            } else {
                possibleTables.add(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON);
            }
        }
        if (biome.is(BiomeTags.HAS_DESERT_PYRAMID)) {
            if (level.getRandom().nextInt(100) < 40) {
                possibleTables.add(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY);
            } else {
                possibleTables.add(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY);
            }
        }
        if (biome.is(BiomeTags.HAS_OCEAN_RUIN_WARM)) {
            possibleTables.add(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY);
        }
        if (biome.is(BiomeTags.HAS_OCEAN_RUIN_COLD)) {
            possibleTables.add(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY);
        }

        return possibleTables;
    }
}
