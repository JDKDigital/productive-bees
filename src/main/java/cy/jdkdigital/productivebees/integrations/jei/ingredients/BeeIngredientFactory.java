package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BeeIngredientFactory
{
    private static Map<String, BeeIngredient> ingredientList = new HashMap<>();

    public static Map<String, BeeIngredient> getOrCreateList(boolean removeDeprecated) {
        Map<String, BeeIngredient> list = new HashMap<>();
        if (removeDeprecated) {
            for (Map.Entry<String, BeeIngredient> entry : createList().entrySet()) {
                String beeId = entry.getKey().replace("productivebees:", "");
                if (!beeId.equals("configurable_bee")) {
                    list.put(entry.getKey(), entry.getValue());
                }
            }
        }
        else {
            list = createList();
        }
        return list;
    }

    public static Map<String, BeeIngredient> getOrCreateList() {
        return createList();
    }

    public static Supplier<BeeIngredient> getIngredient(String name) {
        return () -> getOrCreateList().get(name);
    }

    public static Map<String, BeeIngredient> createList() {
        if (ingredientList.isEmpty()) {
            // Add vanilla bee as ingredient
            ingredientList.put(EntityType.BEE.getRegistryName() + "", new BeeIngredient(EntityType.BEE, 0));

            // Add hive bees
            for (RegistryObject<EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
                EntityType<? extends BeeEntity> bee = (EntityType<? extends BeeEntity>) registryObject.get();
                addBee(bee.getRegistryName().toString(), new BeeIngredient(bee, 0));
            }

            // Add solitary bees
            for (RegistryObject<EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
                EntityType<? extends BeeEntity> bee = (EntityType<? extends BeeEntity>) registryObject.get();
                addBee(bee.getRegistryName().toString(), new BeeIngredient(bee, 1));
            }
        }

        // Add configured bees
        for (Map.Entry<String, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            String beeType = entry.getKey();
            EntityType<ConfigurableBeeEntity> bee = ModEntities.CONFIGURABLE_BEE.get();
            addBee(beeType, new BeeIngredient(bee, new ResourceLocation(beeType), 0, true));
        }

        return ingredientList;
    }

    public static void addBee(String name, BeeIngredient bee) {
        ingredientList.put(name, bee);
    }
}
