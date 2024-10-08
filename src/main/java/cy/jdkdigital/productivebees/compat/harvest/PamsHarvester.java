package cy.jdkdigital.productivebees.compat.harvest;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivebees.common.entity.bee.hive.FarmerBee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class PamsHarvester
{
    public static boolean isCropValid(Level level, BlockPos pos) {
//        if (pos != null && bee.level().isLoaded(pos)) {
//            Block block = bee.level().getBlockState(pos).getBlock();
//            return block instanceof BlockPamFruit || block instanceof BlockPamLogFruit;
//        }
        return false;
    }

    public static void harvestBlock(Level level, BlockPos pos) {
        Player fakePlayer = FakePlayerFactory.get((ServerLevel) level, new GameProfile(FarmerBee.FARMER_BEE_UUID, "farmer_bee"));
        CommonHooks.onRightClickBlock(fakePlayer, InteractionHand.MAIN_HAND, pos, new BlockHitResult(Vec3.ZERO, Direction.DOWN, pos, true));
    }
}
