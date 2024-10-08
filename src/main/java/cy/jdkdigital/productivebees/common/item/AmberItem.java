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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class AmberItem extends BlockItem
{
    public AmberItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (pStack.has(DataComponents.ENTITY_DATA)) {
            CompoundTag tag = pStack.get(DataComponents.ENTITY_DATA).copyTag();
            return Component.translatable("productivebees.amber.name.contained_entity", Component.literal(tag.getString("name")));
        }
        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.add(Component.translatable("productivebees.amber.tooltip.heating").withStyle(ChatFormatting.DARK_RED));
        if (pStack.has(DataComponents.ENTITY_DATA)) {
            CompoundTag tag = pStack.get(DataComponents.ENTITY_DATA).copyTag();
            pTooltipComponents.add(Component.translatable("productivebees.amber.tooltip.contained_entity", Component.literal(tag.getString("name")).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
        }
    }

    public static ItemStack getFakeAmberItem(EntityType<?> entityType) {
        ItemStack stack = new ItemStack(ModBlocks.AMBER.get());
        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("name", Component.translatable(entityType.getDescriptionId()).getString());
        entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
        stack.set(DataComponents.ENTITY_DATA, CustomData.of(entityTag));
        return stack;
    }
}
