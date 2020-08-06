package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Gene extends Item
{
    private static final String ATTRIBUTE_KEY = "productivebees_gene_attribute";
    private static final String VALUE_KEY = "productivebees_gene_value";
    private static final String PURITY_KEY = "productivebees_gene_purity";

    public Gene(Properties properties) {
        super(properties);
    }

    public static ItemStack getStack(BeeAttribute<?> attribute, int value) {
        return getStack(attribute, value, 1);
    }

    public static ItemStack getStack(BeeAttribute<?> attribute, int value, int count) {
        return getStack(attribute.toString(), value, count);
    }

    public static ItemStack getStack(String attributeId, int value, int count) {
        ItemStack result = new ItemStack(ModItems.GENE.get(), count);
        setAttribute(result, attributeId, value);
        return result;
    }

    public static void setAttribute(ItemStack stack, String attributeId, int value) {
        stack.getOrCreateTag().putString(ATTRIBUTE_KEY, attributeId);
        stack.getOrCreateTag().putInt(VALUE_KEY, value);
        stack.getOrCreateTag().putInt(PURITY_KEY, ProductiveBees.rand.nextInt(40) + 15);
    }

    public static BeeAttribute<?> getAttribute(ItemStack stack) {
        String name = getAttributeName(stack);
        return BeeAttributes.getAttributeByName(name);
    }

    public static String getAttributeName(ItemStack stack) {
        return stack.getOrCreateTag().getString(ATTRIBUTE_KEY);
    }

    public static Integer getValue(ItemStack stack) {
        return stack.getOrCreateTag().getInt(VALUE_KEY);
    }

    public static Integer getPurity(ItemStack stack) {
        return stack.getOrCreateTag().getInt(PURITY_KEY);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);

        Integer value = getValue(stack);

        BeeAttribute<?> attribute = getAttribute(stack);

        if (attribute != null) {
            ITextComponent translated_value = new TranslationTextComponent(BeeAttributes.keyMap.get(attribute).get(value)).applyTextStyle(BeeCage.getColor(value));
            list.add((new TranslationTextComponent("productivebees.information.attribute." + getAttributeName(stack), translated_value)).applyTextStyle(TextFormatting.DARK_GRAY).appendText(" (" + getPurity(stack) + "%)"));
        }
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(getStack(BeeAttributes.PRODUCTIVITY, 0, 1));
            items.add(getStack(BeeAttributes.PRODUCTIVITY, 1, 1));
            items.add(getStack(BeeAttributes.PRODUCTIVITY, 2, 1));
            items.add(getStack(BeeAttributes.PRODUCTIVITY, 3, 1));
            items.add(getStack(BeeAttributes.WEATHER_TOLERANCE, 0, 1));
            items.add(getStack(BeeAttributes.WEATHER_TOLERANCE, 1, 1));
            items.add(getStack(BeeAttributes.WEATHER_TOLERANCE, 2, 1));
            items.add(getStack(BeeAttributes.BEHAVIOR, 0, 1));
            items.add(getStack(BeeAttributes.BEHAVIOR, 1, 1));
            items.add(getStack(BeeAttributes.BEHAVIOR, 2, 1));
            items.add(getStack(BeeAttributes.TEMPER, 0, 1));
            items.add(getStack(BeeAttributes.TEMPER, 1, 1));
            items.add(getStack(BeeAttributes.TEMPER, 2, 1));
            items.add(getStack(BeeAttributes.ENDURANCE, 0, 1));
            items.add(getStack(BeeAttributes.ENDURANCE, 1, 1));
            items.add(getStack(BeeAttributes.ENDURANCE, 2, 1));
            items.add(getStack(BeeAttributes.ENDURANCE, 3, 1));
        }
    }
}
