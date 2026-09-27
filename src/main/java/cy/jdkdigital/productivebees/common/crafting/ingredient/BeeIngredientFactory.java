package cy.jdkdigital.productivebees.common.crafting.ingredient;

import com.mojang.serialization.DataResult;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeeIngredientFactory
{
    private static final Map<String, BeeIngredient> ingredientList = new ConcurrentHashMap<>();
    private static volatile boolean inhabitantsLoaded = false;
    private static volatile boolean configurableBeesStale = false;
    private static volatile int configurableBeeIngredientCount = 0; // counter to see if list needs to be recalculated

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
            Set<String> visibleBees = BeeRegistries.all()
                    .map(h -> h.key().identifier().toString())
                    .collect(Collectors.toSet());
            for (Map.Entry<String, BeeIngredient> entry : getOrCreateList().entrySet()) {
                String key = entry.getKey();
                String beeId = key.replace("productivebees:", "");
                if (beeId.equals("configurable_bee") || beeId.equals("villager")) {
                    continue;
                }
                Identifier beeType = entry.getValue().getBeeType();
                // Hide configurable bees whose flowerTag is empty (their host mod isn't loaded);
                // non-configurable bees (custom EntityType subclasses) are always visible.
                if (entry.getValue().isConfigurable() && !visibleBees.contains(beeType.toString())) {
                    continue;
                }
                list.put(key, entry.getValue());
            }
        } else {
            list = getOrCreateList();
        }
        return list;
    }

    public static DataResult<Supplier<BeeIngredient>> read(String name) {
        return DataResult.success(getIngredient(name));
    }

    public static Supplier<BeeIngredient> getIngredient(Identifier name) {
        return getIngredient(name.toString());
    }

    @Nullable
    public static Supplier<BeeIngredient> getIngredient(String name) {
        return () -> getOrCreateList().get(name);
    }

    public static Map<String, BeeIngredient> getOrCreateList() {
        if (!inhabitantsLoaded) {
            // Add all beehive inhabitors, entity type check must be done before using the entry
            try {
                boolean foundInhabitants = false;
                for (EntityType<?> registryObject : BuiltInRegistries.ENTITY_TYPE) {
                    if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(registryObject).is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                        if (registryObject.equals(ModEntities.CONFIGURABLE_BEE.get())) {
                            continue;
                        }
                        EntityType<? extends Bee> bee = (EntityType<? extends Bee>) registryObject;
                        addBee(BuiltInRegistries.ENTITY_TYPE.getKey(bee).toString(), new BeeIngredient(bee));
                        foundInhabitants = true;
                    }
                }
                inhabitantsLoaded = foundInhabitants;
            } catch (IllegalStateException e) {
                // Tag not ready
                ProductiveBees.LOGGER.warn("Failed to create bee ingredient list for beehive inhabitors");
            }
        }

        // allRegistered ignores hidden bees so recipe ingredients still resolve.
        int registrySize = BeeRegistries.registeredSize();
        if ((configurableBeesStale || configurableBeeIngredientCount != registrySize) && ModEntities.CONFIGURABLE_BEE.isBound()) {
            EntityType<ConfigurableBee> bee = ModEntities.CONFIGURABLE_BEE.get();
            Set<String> present = new HashSet<>();
            BeeRegistries.allRegistered().forEach(holder -> {
                Identifier beeType = holder.unwrapKey().orElseThrow().identifier();
                BeeIngredient ingredient = new BeeIngredient(bee, beeType, true);
                addBee(beeType.toString(), ingredient);
                present.add(beeType.toString());
                // Alias under the path's last segment so simple-name recipe references resolve.
                String path = beeType.getPath();
                int slash = path.lastIndexOf('/');
                if (slash >= 0) {
                    String alias = beeType.getNamespace() + ":" + path.substring(slash + 1);
                    addBee(alias, ingredient);
                    present.add(alias);
                }
            });
            if (!present.isEmpty()) {
                ingredientList.entrySet().removeIf(entry -> entry.getValue().isConfigurable() && !present.contains(entry.getKey()));
                configurableBeesStale = false;
                configurableBeeIngredientCount = registrySize;
            }
        }

        return ingredientList;
    }

    public static void addBee(String name, BeeIngredient bee) {
        ingredientList.put(name, bee);
    }

    public static void invalidate() {
        inhabitantsLoaded = false;
        configurableBeesStale = true;
        configurableBeeIngredientCount = 0;
        BeeIngredient.clearEntityCache();
    }
}
