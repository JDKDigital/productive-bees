package cy.jdkdigital.productivebees.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class JarBlockItem extends BlockItem
{
    public JarBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container == null || container.getSlots() == 0) {
            consumer.accept(Component.translatable("productivebees.information.jar.fill_tip"));
            return;
        }
        ItemStack cageStack = container.getStackInSlot(0);
        if (!BeeCage.isFilled(cageStack)) {
            consumer.accept(Component.translatable("productivebees.information.jar.fill_tip"));
            return;
        }
        var data = cageStack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }
        var tag = data.copyTag();
        consumer.accept(Component.translatable("productivebees.information.jar.bee", tag.getString("name").orElse("")));
    }
}
