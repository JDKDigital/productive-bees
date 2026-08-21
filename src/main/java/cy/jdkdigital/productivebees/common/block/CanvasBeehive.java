package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class CanvasBeehive extends AdvancedBeehive
{
    public CanvasBeehive(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CanvasBeehiveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ModBlockEntityTypes.CANVAS_ADVANCED_HIVE.get(), AdvancedBeehiveBlockEntity::tick);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof CanvasBeehiveBlockEntity canvasBeehiveBlockEntity) {
            int color = 0;
            if (stack.getItem() instanceof DyeItem) {
                DyeColor dyeColor = DyeColor.getColor(stack);
                if (dyeColor != null) {
                    color = dyeColor.getTextureDiffuseColor();
                }
            }

            if (color != 0) {
                canvasBeehiveBlockEntity.setColor(color);
                if (!pLevel.isClientSide() && !pPlayer.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

}
