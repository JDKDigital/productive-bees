package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.CanvasExpansionBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class CanvasExpansionBox extends ExpansionBox
{
    public CanvasExpansionBox(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CanvasExpansionBoxBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof CanvasExpansionBoxBlockEntity canvasExpansionBoxBlockEntity) {
            int color = 0;
            if (stack.getItem() instanceof DyeItem) {
                DyeColor dyeColor = DyeColor.getColor(stack);
                if (dyeColor != null) {
                    color = dyeColor.getTextureDiffuseColor();
                }
            }

            if (color != 0) {
                canvasExpansionBoxBlockEntity.setColor(color);
                if (!pLevel.isClientSide() && !pPlayer.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

}
