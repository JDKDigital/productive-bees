package cy.jdkdigital.productivebees.compat.harvest;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.core.BlockPos;
import net.neoforged.fml.ModList;

import java.util.concurrent.atomic.AtomicBoolean;

public class HarvestCompatHandler
{
    public static boolean isCropValid(ProductiveBee bee, BlockPos pos) {
        boolean isValid = MinecraftHarvester.isCropValid(bee, pos);
        if (!isValid && ModList.get().isLoaded("pamhc2trees")) {
            isValid = PamsHarvester.isCropValid(bee, pos);
        }
        return isValid;
    }

    public static void harvestBlock(ProductiveBee bee, BlockPos pos) {
        AtomicBoolean hasHarvested = new AtomicBoolean(false);
        if (MinecraftHarvester.isCropValid(bee, pos)) {
            MinecraftHarvester.harvestBlock(bee, pos);
            hasHarvested.set(true);
        }
        if (!hasHarvested.get() && ModList.get().isLoaded("pamhc2trees")) {
            if (PamsHarvester.isCropValid(bee, pos)) {
                PamsHarvester.harvestBlock(bee, pos);
                hasHarvested.set(true);
            }
        }
    }
}
