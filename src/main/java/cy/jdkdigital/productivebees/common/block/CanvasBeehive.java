package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CanvasBeehive extends AdvancedBeehive
{
    public CanvasBeehive(Properties properties, Supplier<BlockEntityType<AdvancedBeehiveBlockEntity>> blockEntitySupplier) {
        super(properties, blockEntitySupplier);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CanvasBeehiveBlockEntity(this, pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            ItemStack usedItem = player.getItemInHand(hand);
            if (usedItem.getItem() instanceof DyeItem dye && level.getBlockEntity(pos) instanceof CanvasBeehiveBlockEntity canvasBeehiveBlockEntity) {
                canvasBeehiveBlockEntity.setColor(floatColorToInt(dye.getDyeColor().getTextureDiffuseColors()));
                if (!player.isCreative()) {
                    usedItem.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    public static int floatColorToInt(float[] in) {
        return FastColor.ARGB32.color(0xFF, (int) (in[0] * 255f), (int) (in[1] * 255f), (int) (in[2] * 255f));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        String style = stack.getItem().getDescriptionId().replace("block.productivebees.advanced_", "").replace("_canvas_beehive", "");
        style = style.substring(0, 1).toUpperCase() + style.substring(1);
        tooltip.add(Component.translatable("productivebees.information.canvas.style", Component.literal(style).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
    }
}
