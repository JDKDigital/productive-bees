package cy.jdkdigital.productivebees.compat.harvest;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HarvestCompatHandler
{
    static Map<String, HarvestCompat> harvesters = new HashMap<>() {{
        put("minecraft", new MinecraftHarvester());
        put("pamhc2trees", new PamsHarvester());
    }};

    public static boolean isCropValid(ProductiveBee bee, BlockPos pos) {
        return harvesters.values().stream().anyMatch(harvestCompat -> harvestCompat.isCropValid(bee, pos));
    }

    public static void harvestBlock(ProductiveBee bee, BlockPos pos) {
        AtomicBoolean hasHarvested = new AtomicBoolean(false);
        harvesters.forEach((modId, harvestCompat) -> {
            if (!hasHarvested.get() && (modId.equals("minecraft") || ModList.get().isLoaded(modId)) && harvestCompat.isCropValid(bee, pos)) {
                harvestCompat.harvestBlock(bee, pos);
                hasHarvested.set(true);
            }
        });
    }

    public interface HarvestCompat {
        boolean isCropValid(ProductiveBee bee, BlockPos pos);
        void harvestBlock(ProductiveBee bee, BlockPos pos);
    }
}
