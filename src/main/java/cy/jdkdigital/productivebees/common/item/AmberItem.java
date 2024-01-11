package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.client.render.item.AmberItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class AmberItem extends BlockItem
{
    public AmberItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("EntityData")) {
            return Component.translatable("productivebees.amber.name.contained_entity", Component.literal(tag.getCompound("BlockEntityTag").getCompound("EntityData").getString("name")));
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        list.add(Component.translatable("productivebees.amber.tooltip.heating").withStyle(ChatFormatting.DARK_RED));
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("EntityData")) {
            list.add(Component.translatable("productivebees.amber.tooltip.contained_entity", Component.literal(tag.getCompound("BlockEntityTag").getCompound("EntityData").getString("name")).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.WHITE));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions()
        {
            final BlockEntityWithoutLevelRenderer myRenderer = new AmberItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return myRenderer;
            }
        });
    }
}
