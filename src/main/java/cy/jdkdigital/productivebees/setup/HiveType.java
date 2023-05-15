package cy.jdkdigital.productivebees.setup;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

public record HiveType(boolean hasTexture, String primary, String secondary, Ingredient planks)
{
    public HiveType(Ingredient planks) {
        this(true, "", "", planks);
    }

    public HiveType() {
        this(true, "", "", Ingredient.of(ItemTags.PLANKS));
    }
}
