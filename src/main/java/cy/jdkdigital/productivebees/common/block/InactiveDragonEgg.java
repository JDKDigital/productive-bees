package cy.jdkdigital.productivebees.common.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!world.isClientSide && heldItem.getItem() == Items.DRAGON_BREATH) {
            BlockPos posUp = pos.above(2);
            for (int i = 0; i < 42; ++i) {
                double rnd = world.random.nextDouble();
                float xSpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                float ySpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                float zSpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                double x = Mth.lerp(rnd, posUp.getX(), pos.getX()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                double y = Mth.lerp(rnd, posUp.getY(), pos.getY()) + world.random.nextDouble() - 0.5D;
                double z = Mth.lerp(rnd, posUp.getZ(), pos.getZ()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                world.addParticle(ParticleTypes.PORTAL, x, y, z, xSpeed, ySpeed, zSpeed);
            }

            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, heldItem);

            // Transform to real dragon egg
            world.setBlockAndUpdate(pos, Blocks.DRAGON_EGG.defaultBlockState());

            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
