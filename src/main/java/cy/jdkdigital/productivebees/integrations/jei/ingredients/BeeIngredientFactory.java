package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.BlazingBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.SkeletalBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.WitherBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.ZombieBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.nesting.*;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeeIngredientFactory
{
    private static Map<String, BeeIngredient> ingredientList = new HashMap<>();

    public static Map<String, BeeIngredient> getOrCreateList(boolean removeDeprecated) {
        Map<String, BeeIngredient> list = new HashMap<>();
        if (removeDeprecated) {
            List<String> excludedBees = Arrays.asList(
                "configurable_bee",
                "aluminium_bee",
                "brass_bee",
                "bronze_bee",
                "copper_bee",
                "electrum_bee",
                "invar_bee",
                "lead_bee",
                "nickel_bee",
                "osmium_bee",
                "platinum_bee",
                "radioactive_bee",
                "silver_bee",
                "steel_bee",
                "tin_bee",
                "titanium_bee",
                "tungsten_bee",
                "zinc_bee",
                "amber_bee",
                "iron_bee",
                "gold_bee",
                "redstone_bee",
                "coal_bee",
                "lapis_bee",
                "emerald_bee",
                "diamond_bee",
                "wither_bee",
                "blazing_bee",
                "zombie_bee",
                "skeletal_bee",
                "ender_bee",
                "glowing_bee",
                "quartz_bee",
                "magmatic_bee",
                "draconic_bee",
                "slimy_bee",
                "ghostly_bee"
            );
            for (Map.Entry<String, BeeIngredient> entry : createList().entrySet()) {
                String beeId = entry.getKey().replace("productivebees:", "");
                if (!excludedBees.contains(beeId)) {
                    list.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
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
        for (Map.Entry<ResourceLocation, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            String beeType = entry.getKey().toString();
            EntityType<ConfigurableBeeEntity> bee = ModEntities.CONFIGURABLE_BEE.get();
            addBee(beeType, new BeeIngredient(bee, new ResourceLocation(beeType), 0, true));
        }

        return ingredientList;
    }

    public static void addBee(String name, BeeIngredient bee) {
        ingredientList.put(name, bee);
    }
}
