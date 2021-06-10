package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public class GeneBottle extends Item
{
    private static final String GENES_KEY = "productivebees_genes";

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
        CompoundNBT nbt = new CompoundNBT();
        if (target.hasCustomName()) {
            nbt.putString("name", target.getCustomName().getString());
        }
        else {
            nbt.putString("name", target.getName().getString());
        }
        target.saveWithoutId(nbt);

        if (nbt.getString("type").isEmpty()) {
            nbt.putString("type", "" + target.getEncodeId());
        }

        stack.getOrCreateTag().put(GENES_KEY, nbt);
    }

    @Nullable
    public static CompoundNBT getGenes(ItemStack stack) {
        if (!getGenesTag(stack).isEmpty()) {
            return getGenesTag(stack);
        }
        return null;
    }

    public static CompoundNBT getGenesTag(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? (CompoundNBT) tag.get(GENES_KEY) : new CompoundNBT();
    }

//    @Override
//    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
//        if (this.allowdedIn(group)) {
//            if () {
//                items.add(getStack(block));
//            }
//        }
//    }
}
