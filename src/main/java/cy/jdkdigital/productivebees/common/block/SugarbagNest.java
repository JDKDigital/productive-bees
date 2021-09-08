package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.SugarbagNestTileEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SugarbagNest extends BeehiveBlock
{
    public SugarbagNest(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return new SugarbagNestTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);
        int i = state.getValue(HONEY_LEVEL);
        if (i >= 5) {
            if (itemstack.getItem() == Items.SHEARS) {
                worldIn.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                popResource(worldIn, pos, new ItemStack(ModItems.SUGARBAG_HONEYCOMB.get(), 3));
                itemstack.hurtAndBreak(1, player, (entity) -> {
                    entity.broadcastBreakEvent(handIn);
                });
                this.resetHoneyLevel(worldIn, state, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
