package cy.jdkdigital.productivebees.common.recipe;

import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public record BeeFloweringRecipe(Identifier id, TagKey<Block> blockTag, TagKey<Item> itemTag, Block block, TagKey<Fluid> fluidTag, Fluid fluid, ItemStack item, BeeIngredient bee) {
    public static BeeFloweringRecipe createBlock(Identifier id, TagKey<Block> blockTag, TagKey<Item> itemTag, BeeIngredient bee) {
        return new BeeFloweringRecipe(id, blockTag, itemTag, null, null, null, null, bee);
    }

    public static BeeFloweringRecipe createBlock(Identifier id, Block block, BeeIngredient bee) {
        return new BeeFloweringRecipe(id, null, null, block, null, null, null, bee);
    }

    public static BeeFloweringRecipe createItem(Identifier id, ItemStack item, BeeIngredient bee) {
        return new BeeFloweringRecipe(id, null, null, null, null, null, item, bee);
    }

    public static BeeFloweringRecipe createFluid(Identifier id, TagKey<Fluid> fluidTag, BeeIngredient bee) {
        return new BeeFloweringRecipe(id, null, null, null, fluidTag, null, null, bee);
    }

    public static BeeFloweringRecipe createFluid(Identifier id, Fluid fluid, BeeIngredient bee) {
        return new BeeFloweringRecipe(id, null, null, null, null, fluid, null, bee);
    }
}