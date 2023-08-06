package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Gene extends Item
{
    public static final String ATTRIBUTE_KEY = "productivebees_gene_attribute";
    public static final String VALUE_KEY = "productivebees_gene_value";
    public static final String PURITY_KEY = "productivebees_gene_purity";

    public static float color(ItemStack itemStack) {
        return switch (getAttributeName(itemStack)) {
            case "productivity" -> 0.1F;
            case "endurance" -> 0.2F;
            case "temper" -> 0.3F;
            case "behavior" -> 0.4F;
            case "weather_tolerance" -> 0.5F;
            default -> 0.0F;
        };
    }

    public Gene(Properties properties) {
        super(properties);
    }

    public static ItemStack getStack(BeeAttribute<Integer> attribute, int value) {
        return getStack(attribute, value, 1);
    }

    public static ItemStack getStack(BeeAttribute<Integer> attribute, int value, int count) {
        return getStack(attribute, value, count, ProductiveBees.random.nextInt(40) + 15);
    }

    public static ItemStack getStack(BeeAttribute<Integer> attribute, int value, int count, int purity) {
        return getStack(attribute.toString(), value, count, purity);
    }

    public static ItemStack getStack(String type, int value) {
        return getStack(type, 0, 1, value);
    }

    public static ItemStack getStack(String attribute, int value, int count, int purity) {
        ItemStack result = new ItemStack(ModItems.GENE.get(), count);
        setAttribute(result, attribute, value, purity);
        return result;
    }

    public static void setAttribute(ItemStack stack, String attributeId, int value, int purity) {
        stack.getOrCreateTag().putString(ATTRIBUTE_KEY, attributeId);
        stack.getOrCreateTag().putInt(VALUE_KEY, value);
        stack.getOrCreateTag().putInt(PURITY_KEY, purity);
    }

    @Nullable
    public static BeeAttribute<Integer> getAttribute(ItemStack stack) {
        String name = getAttributeName(stack);
        return BeeAttributes.getAttributeByName(name);
    }

    public static String getAttributeName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getString(ATTRIBUTE_KEY) : "";
    }

    public static Integer getValue(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(VALUE_KEY) : 0;
    }

    public static Integer getPurity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(PURITY_KEY) : 0;
    }

    public static void setPurity(ItemStack stack, int purity) {
        stack.getOrCreateTag().putInt(PURITY_KEY, purity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        Integer value = getValue(stack);

        BeeAttribute<Integer> attribute = getAttribute(stack);

        if (attribute != null && BeeAttributes.keyMap.containsKey(attribute) && BeeAttributes.keyMap.get(attribute).containsKey(value)) {
            Component translatedValue = Component.translatable(BeeAttributes.keyMap.get(attribute).get(value)).withStyle(ColorUtil.getAttributeColor(value));
            list.add((Component.translatable("productivebees.information.attribute." + getAttributeName(stack), translatedValue)).withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(" (" + getPurity(stack) + "%)")));
        } else {
            String type = getAttributeName(stack);
            list.add(Component.translatable("productivebees.information.attribute.type", type).withStyle(ChatFormatting.GOLD).append(Component.literal(" (" + getPurity(stack) + "%)")));
        }
    }
}
