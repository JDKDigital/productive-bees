package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivelib.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SpawnEgg extends SpawnEggItem
{
    public SpawnEgg(Item.Properties properties) {
        super(properties);
    }

    public int getColor(int tintIndex, ItemStack stack) {
        var data = stack.get(DataComponents.ENTITY_DATA);
        if (data != null) {
            BeeData beeData = BeeRegistries.lookup(Identifier.parse(data.getUnsafe().getString("type").orElse("")));
            if (beeData != null) {
                return tintIndex == 0 ? beeData.primaryColor() : beeData.secondaryColor();
            }
        }
        return 0xFFFFFF;
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.ENTITY_DATA);
        if (data != null) {
            var beeType = Identifier.parse(data.getUnsafe().getString("type").orElse(""));
            BeeData beeData = BeeRegistries.lookup(beeType);
            if (beeData != null) {
                String name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeType) + "_bee").getString();
                return Component.translatable("item.productivebees.spawn_egg_configurable", name);
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        if (stack.has(DataComponents.ENTITY_DATA)) {
            var beeType = Identifier.parse(stack.get(DataComponents.ENTITY_DATA).getUnsafe().getString("type").orElse(""));
            BeeData beeData = BeeRegistries.lookup(beeType);
            if (beeData != null) {
                tooltipComponents.accept(Component.literal(
                        LangUtil.capName(BeeData.groupFor(BeeRegistries.resolveId(beeType))))
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
