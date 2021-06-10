package cy.jdkdigital.productivebees.common.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class InactiveDragonEgg extends DragonEggBlock
{
    public InactiveDragonEgg(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!world.isClientSide && heldItem.getItem() == Items.DRAGON_BREATH) {
            BlockPos posUp = pos.above(2);
            for (int i = 0; i < 42; ++i) {
                double rnd = world.random.nextDouble();
                float xSpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                float ySpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                float zSpeed = (world.random.nextFloat() - 0.5F) * 0.2F;
                double x = MathHelper.lerp(rnd, posUp.getX(), pos.getX()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                double y = MathHelper.lerp(rnd, posUp.getY(), pos.getY()) + world.random.nextDouble() - 0.5D;
                double z = MathHelper.lerp(rnd, posUp.getZ(), pos.getZ()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                world.addParticle(ParticleTypes.PORTAL, x, y, z, xSpeed, ySpeed, zSpeed);
            }

            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, pos, heldItem);

            // Transform to real dragon egg
            world.setBlockAndUpdate(pos, Blocks.DRAGON_EGG.defaultBlockState());

            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
