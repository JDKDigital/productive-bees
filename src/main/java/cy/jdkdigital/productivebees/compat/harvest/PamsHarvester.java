package cy.jdkdigital.productivebees.compat.harvest;

import com.mojang.authlib.GameProfile;
import com.pam.pamhc2trees.blocks.BlockPamFruit;
import com.pam.pamhc2trees.blocks.BlockPamLogFruit;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.FarmerBee;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayerFactory;

public class PamsHarvester
{
    public static boolean isCropValid(ProductiveBee bee, BlockPos pos) {
        if (pos != null && bee.level().isLoaded(pos)) {
            Block block = bee.level().getBlockState(pos).getBlock();
            return block instanceof BlockPamFruit || block instanceof BlockPamLogFruit;
        }
        return false;
    }

    public static void harvestBlock(ProductiveBee bee, BlockPos pos) {
        Player fakePlayer = FakePlayerFactory.get((ServerLevel) bee.level(), new GameProfile(FarmerBee.FARMER_BEE_UUID, "farmer_bee"));
        ForgeHooks.onRightClickBlock(fakePlayer, InteractionHand.MAIN_HAND, pos, new BlockHitResult(bee.getEyePosition(), bee.getMotionDirection(), pos, true));
    }
}
