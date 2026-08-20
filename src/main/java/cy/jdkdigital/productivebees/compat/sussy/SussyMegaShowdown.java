package cy.jdkdigital.productivebees.compat.sussy;

import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

public class SussyMegaShowdown
{
    public static boolean isBlockValid(ServerLevel level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block.equals(Blocks.SUSPICIOUS_SAND) || block.equals(Blocks.SUSPICIOUS_GRAVEL);
    }

    public static List<ResourceKey<LootTable>> getLootTables(ServerLevel level, BlockPos pos) {

        LocationPredicate IN_ARCHAEOLOGICAL_SITE = LocationPredicate.Builder.inStructure(level.holderLookup(Registries.STRUCTURE).getOrThrow(ResourceKey.create(Registries.STRUCTURE, Identifier.parse("mega_showdown:archaeological_site")))).build();

        List<ResourceKey<LootTable>> possibleTables = new ArrayList<>();
        if (IN_ARCHAEOLOGICAL_SITE.matches(level, pos.getX(), pos.getY(), pos.getZ())) {
            possibleTables.add(ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse("mega_showdown:archaeological_site/archaeological_site")));
            possibleTables.add(ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse("mega_showdown:archaeological_site/archaeological_site_rare")));
        }
        return possibleTables;
    }
}
