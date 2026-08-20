package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

public class AdvancedBeehiveBlockItem extends BlockItem
{
    public AdvancedBeehiveBlockItem(Block block, Item.Properties properties) {
        super(block, properties.component(DataComponents.TOOLTIP_DISPLAY,
                TooltipDisplay.DEFAULT.withHidden(DataComponents.BEES, true)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);

        TypedEntityData<BlockEntityType<?>> beData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (beData != null) {
            CompoundTag stateNBT = beData.copyTagWithoutId();
            stateNBT.getString("honey_level").ifPresent(honeyLevel ->
                consumer.accept(Component.translatable("productivebees.hive.tooltip.honey_level", honeyLevel).withStyle(ChatFormatting.GOLD))
            );
        }

        Bees bees = stack.get(DataComponents.BEES);
        if (bees == null) {
            return;
        }
        if (bees.bees().isEmpty()) {
            consumer.accept(Component.translatable("productivebees.hive.tooltip.empty"));
            return;
        }
        consumer.accept(Component.translatable("productivebees.hive.tooltip.bees").withStyle(ChatFormatting.BOLD));
        for (BeehiveBlockEntity.Occupant occupant : bees.bees()) {
            CompoundTag tag = occupant.entityData().copyTagWithoutId();
            tag.getString("type").ifPresentOrElse(
                type -> consumer.accept(Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(Identifier.parse(type)) + "_bee").withStyle(ChatFormatting.GREEN)),
                () -> consumer.accept(Component.translatable(occupant.entityData().type().getDescriptionId()).withStyle(ChatFormatting.GREEN))
            );
        }
    }
}
