package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class AmberItem extends BlockItem
{
    public AmberItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (pStack.has(DataComponents.ENTITY_DATA)) {
            CompoundTag tag = pStack.get(DataComponents.ENTITY_DATA).copyTagWithoutId();
            return Component.translatable("productivebees.amber.name.contained_entity", Component.literal(tag.getString("name").orElse("")));
        }
        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltipDisplay, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.accept(Component.translatable("productivebees.amber.tooltip.heating").withStyle(ChatFormatting.DARK_RED));
        if (pStack.has(DataComponents.ENTITY_DATA)) {
            CompoundTag tag = pStack.get(DataComponents.ENTITY_DATA).copyTagWithoutId();
            pTooltipComponents.accept(Component.translatable("productivebees.amber.tooltip.contained_entity", Component.literal(tag.getString("name").orElse("")).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
        }
    }

    public static ItemStack getFakeAmberItem(EntityType<?> entityType) {
        ItemStack stack = new ItemStack(ModBlocks.AMBER.get());
        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("name", Component.translatable(entityType.getDescriptionId()).getString());
        entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
        stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(entityType, entityTag));
        return stack;
    }
}
