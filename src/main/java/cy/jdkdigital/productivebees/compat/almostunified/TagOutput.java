package cy.jdkdigital.productivebees.compat.almostunified;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class TagOutput
{
    @Nullable
    public static Item getPreferredItemForTag(TagKey<Item> tag) {
//        if (ModList.get().isLoaded("almostunified")) {
//            return Adapter.getPreferredItemForTag(tag);
//        }
        return null;
    }

    private static class Adapter {
//        @Nullable
//        public static Item getPreferredItemForTag(Ingredient ingredient) {
//            return AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(ingredient);
//        }
    }
}
