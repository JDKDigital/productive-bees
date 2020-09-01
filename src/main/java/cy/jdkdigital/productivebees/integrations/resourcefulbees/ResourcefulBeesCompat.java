package cy.jdkdigital.productivebees.integrations.resourcefulbees;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ResourcefulBeesCompat
{
    static final public String MODID = "resourcefulbees";
    public static final String NBT_ROOT = "ResourcefulBees";
    public static final String NBT_COLOR = "Color";
    public static final String NBT_BEE_TYPE = "BeeType";

    public static ItemStack getHoneyComb(String type) {
        Item combItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "resourceful_honeycomb_block"));
        if (combItem != null) {
            ItemStack itemStack = new ItemStack(combItem);
            itemStack.setTag(createHoneycombItemTag(type));
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    private static CompoundNBT createHoneycombItemTag(String beeType) {
        CompoundNBT rootTag = new CompoundNBT();
        CompoundNBT childTag = new CompoundNBT();

        childTag.putString(NBT_BEE_TYPE, beeType);
        childTag.putString(NBT_COLOR, "#000000");

        rootTag.put(NBT_ROOT, childTag);

        return rootTag;
    }
}
