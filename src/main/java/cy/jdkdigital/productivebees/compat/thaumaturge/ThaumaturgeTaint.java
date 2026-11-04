package cy.jdkdigital.productivebees.compat.thaumaturge;

import com.leclowndu93150.thaumaturge.api.taint.ITaintBlock;
import com.leclowndu93150.thaumaturge.api.taint.TaintApi;
import com.leclowndu93150.thaumaturge.content.taint.ecology.TaintBiomeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class ThaumaturgeTaint
{
    private static final int SAMPLE_RADIUS = 4;
    private static final int SAMPLE_COUNT = 4;

    public static boolean cleanseTaint(ServerLevel level, BlockPos pos, float pressure) {
        TaintApi.cleanEcologicalPressure(level, pos, pressure);
        boolean changed = TaintBiomeManager.restoreColumn(level, pos);
        RandomSource random = level.getRandom();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            cursor.set(
                    pos.getX() - SAMPLE_RADIUS + random.nextInt(SAMPLE_RADIUS * 2 + 1),
                    pos.getY() - SAMPLE_RADIUS + random.nextInt(SAMPLE_RADIUS * 2 + 1),
                    pos.getZ() - SAMPLE_RADIUS + random.nextInt(SAMPLE_RADIUS * 2 + 1));
            if (!level.isLoaded(cursor)) {
                continue;
            }
            changed |= TaintBiomeManager.restoreColumn(level, cursor);
            BlockState state = level.getBlockState(cursor);
            if (state.getBlock() instanceof ITaintBlock taintBlock) {
                taintBlock.decay(level, cursor.immutable(), state);
                changed = true;
            }
        }
        return changed;
    }
}
