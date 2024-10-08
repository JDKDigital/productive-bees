package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GeneBottle extends Item
{
    public GeneBottle(Properties properties) {
        super(properties);
    }

    public static ItemStack getStack(Entity target) {
        return getStack(target, 1);
    }

    public static ItemStack getStack(Entity target, int count) {
        ItemStack result = new ItemStack(ModItems.GENE_BOTTLE.get(), count);
        setGenes(result, target);
        return result;
    }

    public static void setGenes(ItemStack stack, Entity target) {
        CompoundTag nbt = new CompoundTag();
        var name = target.getName().getString();
        if (target.hasCustomName()) {
            name = target.getCustomName().getString();
        }

        var type = target.getEncodeId();
        if (target instanceof ProductiveBee pBee) {
            type = pBee.getBeeType().toString();
        }

        var geneList = new ArrayList<GeneGroup>();
        var data = target.getData(ProductiveBees.ATTRIBUTE_HANDLER);
        for (GeneAttribute attribute: GeneAttribute.values()) {
            if (!attribute.equals(GeneAttribute.TYPE)) {
                geneList.add(new GeneGroup(attribute, data.getAttributeValue(attribute).getSerializedName(), target.level().random.nextInt(40) + 15));
            }
        }

        int typePurity = ProductiveBeesConfig.BEE_ATTRIBUTES.typeGenePurity.get();
        geneList.add(new GeneGroup(GeneAttribute.TYPE, type, target.level().random.nextInt(Math.max(0, typePurity - 5)) + 10));
        stack.set(ModDataComponents.GENE_GROUP_LIST, geneList);
        stack.set(ModDataComponents.BEE_NAME, name);
    }

    @Nullable
    public static List<GeneGroup> getGenes(ItemStack stack) {
        if (stack.has(ModDataComponents.GENE_GROUP_LIST)) {
            return stack.get(ModDataComponents.GENE_GROUP_LIST);
        }
        return null;
    }
}
