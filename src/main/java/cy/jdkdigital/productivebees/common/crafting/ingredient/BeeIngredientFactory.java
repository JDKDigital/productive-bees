package cy.jdkdigital.productivebees.common.crafting.ingredient;

import com.mojang.serialization.DataResult;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;

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
            type = ((ProductiveBee) bee).getBeeType().toString();
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

    public static DataResult<Supplier<BeeIngredient>> read(String name) {
        return DataResult.success(getIngredient(name));
    }

    public static Supplier<BeeIngredient> getIngredient(ResourceLocation name) {
        return getIngredient(name.toString());
    }

    public static Supplier<BeeIngredient> getIngredient(String name) {
        return () -> getOrCreateList().get(name);
    }

    public static Map<String, BeeIngredient> getOrCreateList() {
        if (ingredientList.isEmpty()) {
            // Add all beehive inhabitors, entity type check must be done before using the entry
            try {
                for (EntityType<?> registryObject : BuiltInRegistries.ENTITY_TYPE) {
                    if (registryObject.is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                        if (registryObject.equals(ModEntities.CONFIGURABLE_BEE.get())) {
                            continue;
                        }
                        EntityType<? extends Bee> bee = (EntityType<? extends Bee>) registryObject;
                        addBee(BuiltInRegistries.ENTITY_TYPE.getKey(bee).toString(), new BeeIngredient(bee));
                    }
                }
            } catch (IllegalStateException e) {
                // Tag not ready
                ProductiveBees.LOGGER.warn("Failed to create bee ingredient list for beehive inhabitors");
            }
        }

        // Add configured bees
        if (configurableBeeIngredientCount != BeeReloadListener.INSTANCE.getData().size()) {
            configurableBeeIngredientCount = 0;
            for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                ResourceLocation beeType = entry.getKey();
                EntityType<ConfigurableBee> bee = ModEntities.CONFIGURABLE_BEE.get();
                addBee(beeType.toString(), new BeeIngredient(bee, beeType, true));
                configurableBeeIngredientCount++;
            }
        }

        return ingredientList;
    }

    public static void addBee(String name, BeeIngredient bee) {
        ingredientList.put(name, bee);
    }
}
