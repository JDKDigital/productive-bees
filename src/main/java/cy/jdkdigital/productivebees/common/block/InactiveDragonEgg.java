package cy.jdkdigital.productivebees.common.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class InactiveDragonEgg extends DragonEggBlock
{
    public InactiveDragonEgg(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide && pStack.getItem() == Items.DRAGON_BREATH) {
            BlockPos posUp = pPos.above(2);
            for (int i = 0; i < 42; ++i) {
                double rnd = pLevel.random.nextDouble();
                float xSpeed = (pLevel.random.nextFloat() - 0.5F) * 0.2F;
                float ySpeed = (pLevel.random.nextFloat() - 0.5F) * 0.2F;
                float zSpeed = (pLevel.random.nextFloat() - 0.5F) * 0.2F;
                double x = Mth.lerp(rnd, posUp.getX(), pPos.getX()) + (pLevel.random.nextDouble() - 0.5D) + 0.5D;
                double y = Mth.lerp(rnd, posUp.getY(), pPos.getY()) + pLevel.random.nextDouble() - 0.5D;
                double z = Mth.lerp(rnd, posUp.getZ(), pPos.getZ()) + (pLevel.random.nextDouble() - 0.5D) + 0.5D;
                pLevel.addParticle(ParticleTypes.PORTAL, x, y, z, xSpeed, ySpeed, zSpeed);
            }

            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) pPlayer, pPos, pStack);

            // Transform to real dragon egg
            pLevel.setBlockAndUpdate(pPos, Blocks.DRAGON_EGG.defaultBlockState());

            if (!pPlayer.isCreative()) {
                pStack.shrink(1);
            }

            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }
}
