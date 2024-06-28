package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.CanvasExpansionBoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            if (pStack.getItem() instanceof DyeItem dye && pLevel.getBlockEntity(pPos) instanceof CanvasExpansionBoxBlockEntity canvasExpansionBoxBlockEntity) {
                canvasExpansionBoxBlockEntity.setColor(dye.getDyeColor().getTextureDiffuseColor());
                if (!pPlayer.isCreative()) {
                    pStack.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTootipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
        String style = pStack.getItem().getDescriptionId().replace("block.productivebees.expansion_box_", "").replace("_canvas", "");
        style = style.substring(0, 1).toUpperCase() + style.substring(1);
        pTootipComponents.add(Component.translatable("productivebees.information.canvas.style", Component.literal(style).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
    }
}
