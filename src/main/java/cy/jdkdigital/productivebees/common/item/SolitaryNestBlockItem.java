package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
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

public class SolitaryNestBlockItem extends BlockItem
{
    public SolitaryNestBlockItem(Block block, Item.Properties properties) {
        // Hide vanilla Bees tooltip (renders count/MAX_OCCUPANTS=3); we emit N/1 ourselves.
        super(block, properties.component(DataComponents.TOOLTIP_DISPLAY,
                TooltipDisplay.DEFAULT.withHidden(DataComponents.BEES, true)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);

        Bees bees = stack.get(DataComponents.BEES);
        if (bees == null || bees.bees().isEmpty()) {
            consumer.accept(Component.translatable("productivebees.hive.tooltip.empty"));
        } else {
            for (BeehiveBlockEntity.Occupant occupant : bees.bees()) {
                CompoundTag tag = occupant.entityData().copyTagWithoutId();
                tag.getString("type").ifPresentOrElse(
                        type -> consumer.accept(Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(Identifier.parse(type)) + "_bee").withStyle(ChatFormatting.GREEN)),
                        () -> consumer.accept(Component.translatable(occupant.entityData().type().getDescriptionId()).withStyle(ChatFormatting.GREEN))
                );
            }
        }

        TypedEntityData<BlockEntityType<?>> beData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (beData == null) {
            return;
        }
        int spawnCount = beData.copyTagWithoutId().getIntOr("spawnCount", 0);
        if (spawnCount >= ProductiveBeesConfig.BEES.cuckooSpawnCount.get()) {
            consumer.accept(Component.translatable("productivebees.hive.tooltip.nest_inactive").withStyle(ChatFormatting.BOLD));
        }
    }
}
