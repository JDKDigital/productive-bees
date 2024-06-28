package cy.jdkdigital.productivebees.setup;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public record HiveType(boolean hasTexture, String primary, String style, Ingredient planks, ICustomIngredient customPlank)
{
    public HiveType(Ingredient planks) {
        this(true, "", "", planks, null);
    }

    public HiveType() {
        this(true, "", "", Ingredient.of(ItemTags.PLANKS), null);
    }
}
