package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class BeeIngredientFactory
{
    private static Map<String, BeeIngredient> ingredientList = new HashMap<>();

    public static Map<String, BeeIngredient> getOrCreateList() {
        if (ingredientList.size() > 0) {
            return ingredientList;
        }
        return createList();
    }

    public static Map<String, BeeIngredient> createList() {
        ingredientList.clear();

        // Add vanilla bee as ingredient
        ingredientList.put(EntityType.BEE.getRegistryName() + "", new BeeIngredient(EntityType.BEE, 0));

        // Add hive bees
        for (RegistryObject<EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
            EntityType<BeeEntity> bee = (EntityType<BeeEntity>) registryObject.get();
            ingredientList.put(bee.getRegistryName() + "", new BeeIngredient(bee, 0));
        }

        // Add solitary bees
        for (RegistryObject<EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
            EntityType<BeeEntity> bee = (EntityType<BeeEntity>) registryObject.get();
            ingredientList.put(bee.getRegistryName() + "", new BeeIngredient(bee, 1));
        }

        return ingredientList;
    }
}
