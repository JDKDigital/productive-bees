package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AdvancedBeehiveRecipeMaker {

    public static List<Object> getRecipes() {
        List<Object> recipes = new ArrayList<>();

        for(RegistryObject<EntityType<?>> registryObject: ModEntities.HIVE_BEES.getEntries()) {
            ProduciveBeesJeiPlugin.BeeIngredient ingredient = BeeIngredientHelper.ingredientList.get(registryObject.get().getRegistryName() + "");

//            List<ItemStack> outputs = AdvancedBeehiveTileEntity.getBeeProduce();
            List<ItemStack> outputs = new ArrayList<ItemStack>() {{
                add(new ItemStack(Items.HONEYCOMB));
            }};
            ResourceLocation regName = ingredient.getBeeType().getRegistryName();
            Double productionRate = ProductiveBeeEntity.getProductionRate(regName.toString());
            AdvancedBeehiveRecipe recipe = new AdvancedBeehiveRecipe(regName, ingredient, outputs, productionRate);
            recipes.add(recipe);
        }

        return recipes;
    }
}
