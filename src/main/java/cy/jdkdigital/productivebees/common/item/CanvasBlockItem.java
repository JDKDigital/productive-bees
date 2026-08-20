package cy.jdkdigital.productivebees.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class CanvasBlockItem extends BlockItem
{
    private final String prefix;
    private final String suffix;

    public CanvasBlockItem(Block block, Item.Properties properties, String prefix, String suffix) {
        super(block, properties);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);
        String style = stack.getItem().getDescriptionId().replace(prefix, "").replace(suffix, "");
        if (!style.isEmpty()) {
            style = style.substring(0, 1).toUpperCase() + style.substring(1);
        }
        consumer.accept(Component.translatable("productivebees.information.canvas.style", Component.literal(style).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
    }
}
