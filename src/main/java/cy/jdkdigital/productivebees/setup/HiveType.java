package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.util.FakeIngredient;
import net.minecraft.world.level.ItemLike;

public record HiveType(boolean hasTexture, String primary, String style, ItemLike planks, FakeIngredient customPlank)
{
    public HiveType(ItemLike planks) {
        this(true, "", "", planks, null);
    }
}
