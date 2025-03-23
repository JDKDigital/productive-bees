package cy.jdkdigital.productivebees.compat.minecolonies;

import com.minecolonies.api.IMinecoloniesAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MinecolonyCompat
{
    public static boolean canGrowAt(Level level, BlockPos pos) {
        return IMinecoloniesAPI.getInstance().getColonyManager().isCoordinateInAnyColony(level, pos);
    }
}
