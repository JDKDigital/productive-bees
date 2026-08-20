package cy.jdkdigital.productivebees.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class SimpleTooltipBlockItem extends BlockItem
{
    private final List<Component> lines;

    public SimpleTooltipBlockItem(Block block, Item.Properties properties, List<Component> lines) {
        super(block, properties);
        this.lines = lines;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);
        lines.forEach(consumer);
    }
}
