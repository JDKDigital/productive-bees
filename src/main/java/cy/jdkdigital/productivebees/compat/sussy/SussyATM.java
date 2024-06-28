package cy.jdkdigital.productivebees.compat.sussy;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

public class SussyATM
{
    public static boolean isBlockValid(ServerLevel level, BlockPos pos) {
        ResourceLocation block = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock());
        return block.equals(ResourceLocation.parse("allthemodium:suspicious_soul_sand")) || block.equals(ResourceLocation.parse("allthemodium:suspicious_clay"));
    }

    public static List<ResourceKey<LootTable>> getLootTables(ServerLevel level, BlockPos pos) {
        LocationPredicate IN_ANCIENT_CITY = LocationPredicate.Builder.inStructure(level.holderLookup(Registries.STRUCTURE).getOrThrow(BuiltinStructures.ANCIENT_CITY)).build();
        LocationPredicate IN_BASTION = LocationPredicate.Builder.inStructure(level.holderLookup(Registries.STRUCTURE).getOrThrow(BuiltinStructures.BASTION_REMNANT)).build();

        List<ResourceKey<LootTable>> possibleTables = new ArrayList<>();
        if (IN_ANCIENT_CITY.matches(level, pos.getX(), pos.getY(), pos.getZ())) {
            possibleTables.add(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse("allthemodium:arch")));
        } else if (IN_BASTION.matches(level, pos.getX(), pos.getY(), pos.getZ())) {
            possibleTables.add(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse("allthemodium:arch2")));
        }
        return possibleTables;
    }
}
