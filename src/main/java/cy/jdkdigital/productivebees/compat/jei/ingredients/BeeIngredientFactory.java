package cy.jdkdigital.productivebees.compat.jei.ingredients;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BeeIngredientFactory
{
    private static Map<String, BeeIngredient> ingredientList = new HashMap<>();
    private static int configurableBeeIngredientCount = 0; // counter to see if list needs to be recalculated

    public static String getIngredientKey(Bee bee) {
        String type = bee.getEncodeId();
        if (bee instanceof ProductiveBee) {
            type = ((ProductiveBee) bee).getBeeType();
        }
        return type;
    }

    public static Map<String, BeeIngredient> getOrCreateList(boolean removeDeprecated) {
        Map<String, BeeIngredient> list = new HashMap<>();
        if (removeDeprecated) {
            for (Map.Entry<String, BeeIngredient> entry : getOrCreateList().entrySet()) {
                String beeId = entry.getKey().replace("productivebees:", "");
                if (!beeId.equals("configurable_bee")) {
                    list.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            list = getOrCreateList();
        }
        return list;
    }

    public static Supplier<BeeIngredient> getIngredient(String name) {
        return () -> getOrCreateList().get(name);
    }

    public static Map<String, BeeIngredient> getOrCreateList() {
        if (ingredientList.isEmpty()) {
            // Add all beehive inhabitors, entity type check must be done before using the entry
            try {
                for (EntityType<?> registryObject : ForgeRegistries.ENTITY_TYPES.getValues()) {
                    if (registryObject.is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                        if (registryObject.equals(ModEntities.CONFIGURABLE_BEE.get())) {
                            continue;
                        }
                        EntityType<? extends Bee> bee = (EntityType<? extends Bee>) registryObject;
                        addBee(ForgeRegistries.ENTITY_TYPES.getKey(bee).toString(), new BeeIngredient(bee));
                    }
                }
            } catch (IllegalStateException e) {
                // Tag not ready
            }
        }

        // Add configured bees
        if (configurableBeeIngredientCount != BeeReloadListener.INSTANCE.getData().size()) {
            configurableBeeIngredientCount = 0;
            for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                String beeType = entry.getKey();
                EntityType<ConfigurableBee> bee = ModEntities.CONFIGURABLE_BEE.get();
                addBee(beeType, new BeeIngredient(bee, new ResourceLocation(beeType), true));
                configurableBeeIngredientCount++;
            }
        }

        return ingredientList;
    }

    public static void addBee(String name, BeeIngredient bee) {
        ingredientList.put(name, bee);
    }

    public static Map<String, BeeIngredient> getRBeesIngredients() {
        Map<String, BeeIngredient> list = new HashMap<>(getOrCreateList());
        list.entrySet().removeIf(entry -> !entry.getKey().contains("resourcefulbees"));
        return list;
    }
}
