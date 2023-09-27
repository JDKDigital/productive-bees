package cy.jdkdigital.productivebees.compat.sussy;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class SussyATM
{
    static LocationPredicate IN_ANCIENT_CITY = LocationPredicate.inStructure(BuiltinStructures.ANCIENT_CITY);
    static LocationPredicate IN_BASTION = LocationPredicate.inStructure(BuiltinStructures.BASTION_REMNANT);

    public static boolean isBlockValid(ServerLevel level, BlockPos pos) {
        ResourceLocation block = ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock());
        return block.equals(new ResourceLocation("allthemodium:suspicious_soul_sand")) || block.equals(new ResourceLocation("allthemodium:suspicious_clay"));
    }

    public static List<ResourceLocation> getLootTables(ServerLevel level, BlockPos pos) {
        List<ResourceLocation> possibleTables = new ArrayList<>();
        if (IN_ANCIENT_CITY.matches(level, pos.getX(), pos.getY(), pos.getZ())) {
            possibleTables.add(new ResourceLocation("allthemodium:arch"));
        } else if (IN_BASTION.matches(level, pos.getX(), pos.getY(), pos.getZ())) {
            possibleTables.add(new ResourceLocation("allthemodium:arch2"));
        }
        return possibleTables;
    }
}
