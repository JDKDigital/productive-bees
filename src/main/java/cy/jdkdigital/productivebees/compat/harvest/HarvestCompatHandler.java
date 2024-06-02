package cy.jdkdigital.productivebees.compat.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.concurrent.atomic.AtomicBoolean;

public class HarvestCompatHandler
{
    public static boolean isCropValid(Level level, BlockPos pos) {
        boolean isValid = MinecraftHarvester.isCropValid(level, pos);
        if (!isValid && ModList.get().isLoaded("pamhc2trees")) {
            isValid = PamsHarvester.isCropValid(level, pos);
        }
        return isValid;
    }

    public static void harvestBlock(Level level, BlockPos pos) {
        AtomicBoolean hasHarvested = new AtomicBoolean(false);
        if (MinecraftHarvester.isCropValid(level, pos)) {
            MinecraftHarvester.harvestBlock(level, pos);
            hasHarvested.set(true);
        }
        if (!hasHarvested.get() && ModList.get().isLoaded("pamhc2trees")) {
            if (PamsHarvester.isCropValid(level, pos)) {
                PamsHarvester.harvestBlock(level, pos);
                hasHarvested.set(true);
            }
        }
    }
}
