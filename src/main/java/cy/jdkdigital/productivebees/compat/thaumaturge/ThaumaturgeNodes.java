package cy.jdkdigital.productivebees.compat.thaumaturge;

import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.aura.node.BlockEntityNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

public class ThaumaturgeNodes
{
    public static boolean restoreNode(ServerLevel level, BlockPos pos, int amount) {
        if (!(level.getBlockEntity(pos) instanceof BlockEntityNode node)) {
            return false;
        }
        List<AspectInstance> depleted = new ArrayList<>();
        for (AspectInstance instance : node.getAspectsBase().entries()) {
            if (node.amountOf(instance.aspect()) < instance.amount()) {
                depleted.add(instance);
            }
        }
        if (depleted.isEmpty()) {
            return false;
        }
        AspectInstance target = depleted.get(level.getRandom().nextInt(depleted.size()));
        return node.fill(target.aspect(), amount) < amount;
    }
}
