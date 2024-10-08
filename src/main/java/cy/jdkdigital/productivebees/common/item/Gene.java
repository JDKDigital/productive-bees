package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.ColorUtil;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class Gene extends Item
{
    public static float color(ItemStack itemStack) {
        return switch (Gene.getAttribute(itemStack)) {
            case TYPE -> 0.0F;
            case PRODUCTIVITY -> 0.1F;
            case ENDURANCE -> 0.2F;
            case TEMPER -> 0.3F;
            case BEHAVIOR -> 0.4F;
            case WEATHER_TOLERANCE -> 0.5F;
        };
    }

    public Gene(Properties properties) {
        super(properties);
    }

    public static ItemStack getStack(GeneAttribute attribute, String value) {
        return getStack(attribute, value, 1);
    }

    public static ItemStack getStack(GeneAttribute attribute, String value, int count) {
        return getStack(attribute, value, count, ProductiveBees.random.nextInt(40) + 15);
    }

    public static ItemStack getStack(String type, int purity) {
        return getStack(GeneAttribute.TYPE, type, 1, purity);
    }

    public static ItemStack getStack(GeneAttribute attribute, GeneValue value, int count, int purity) {
        return getStack(attribute, value.getSerializedName(), count, purity);
    }

    public static ItemStack getStack(GeneAttribute attribute, String value, int count, int purity) {
        ItemStack result = new ItemStack(ModItems.GENE.get(), count);
        setGenes(result, attribute, value, purity);
        return result;
    }

    public static ItemStack getStack(GeneGroup geneGroup, int count) {
        ItemStack result = new ItemStack(ModItems.GENE.get(), count);
        setGenes(result, geneGroup);
        return result;
    }

    public static void setGenes(ItemStack stack, GeneAttribute attribute, String value, int purity) {
        stack.set(ModDataComponents.GENE_GROUP, new GeneGroup(attribute, value, purity));
    }

    public static void setGenes(ItemStack stack, GeneGroup geneGroup) {
        stack.set(ModDataComponents.GENE_GROUP, geneGroup);
    }

    public static GeneGroup getGenes(ItemStack stack) {
        return stack.has(ModDataComponents.GENE_GROUP) ? stack.get(ModDataComponents.GENE_GROUP) : null;
    }

    public static GeneAttribute getAttribute(ItemStack stack) {
        return stack.has(ModDataComponents.GENE_GROUP) ? stack.get(ModDataComponents.GENE_GROUP).attribute() : GeneAttribute.TYPE;
    }

    public static String getValue(ItemStack stack) {
        return stack.has(ModDataComponents.GENE_GROUP) ? stack.get(ModDataComponents.GENE_GROUP).value() : "";
    }

    public static Integer getPurity(ItemStack stack) {
        return stack.has(ModDataComponents.GENE_GROUP) ? stack.get(ModDataComponents.GENE_GROUP).purity() : 0;
    }
//
//    public static void setPurity(ItemStack stack, int purity) {
//        stack.set(ModDataComponents.GENE_PURITY, purity);
//    }

    public static GeneGroup getGene(ItemStack geneStack) {
        return geneStack.get(ModDataComponents.GENE_GROUP);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        GeneAttribute attribute = getAttribute(pStack);
        String value = getValue(pStack);

        if (attribute != null && GeneValue.byName(value) != null) {
            Component translatedValue = Component.translatable("productivebees.information.attribute." + value).withStyle(ColorUtil.getAttributeColor(GeneValue.byName(value)));
            pTooltipComponents.add((Component.translatable("productivebees.information.attribute." + attribute.getSerializedName(), translatedValue)).withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(" (" + getPurity(pStack) + "%)")));
        } else {
            pTooltipComponents.add(Component.translatable("productivebees.information.attribute.type", value).withStyle(ChatFormatting.GOLD).append(Component.literal(" (" + getPurity(pStack) + "%)")));
        }
    }
}
