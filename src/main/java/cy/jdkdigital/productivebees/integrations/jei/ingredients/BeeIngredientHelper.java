package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BeeIngredientHelper implements IIngredientHelper<ProduciveBeesJeiPlugin.BeeIngredient> {

    public static Map<String, ProduciveBeesJeiPlugin.BeeIngredient> ingredientList = new HashMap<>();

    public static Map<String, ProduciveBeesJeiPlugin.BeeIngredient> createList()
    {
        ingredientList.clear();

        // Add vanilla bee as ingredient
        ingredientList.put(EntityType.BEE.getRegistryName() + "", new ProduciveBeesJeiPlugin.BeeIngredient(EntityType.BEE, 0));

        // Add hive bees
        for(RegistryObject<EntityType<?>> registryObject: ModEntities.HIVE_BEES.getEntries()) {
            EntityType<BeeEntity> bee = (EntityType<BeeEntity>) registryObject.get();
            ingredientList.put(bee.getRegistryName() + "", new ProduciveBeesJeiPlugin.BeeIngredient(bee, 0));
        }

        // Add solitary bees
        for(RegistryObject<EntityType<?>> registryObject: ModEntities.SOLITARY_BEES.getEntries()) {
            EntityType<BeeEntity> bee = (EntityType<BeeEntity>) registryObject.get();
            ingredientList.put(bee.getRegistryName() + "", new ProduciveBeesJeiPlugin.BeeIngredient(bee, 1));
        }

        return ingredientList;
    }

    @Nullable
    @Override
    public ProduciveBeesJeiPlugin.BeeIngredient getMatch(Iterable<ProduciveBeesJeiPlugin.BeeIngredient> iterable, ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        for (ProduciveBeesJeiPlugin.BeeIngredient ingredient : iterable) {
            if (ingredient.getBeeType().getRegistryName() == beeIngredient.getBeeType().getRegistryName()) {
                return ingredient;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getDisplayName(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getName().getFormattedText();
    }

    @Nonnull
    @Override
    public String getUniqueId(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return "beeingredient:" + beeIngredient.getBeeType().getRegistryName();
    }

    @Nonnull
    @Override
    public String getWildcardId(@Nonnull ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return getUniqueId(beeIngredient);
    }

    @Nonnull
    @Override
    public String getModId(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getRegistryName().getNamespace();
    }

    @Nonnull
    @Override
    public String getResourceId(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return beeIngredient.getBeeType().getRegistryName().getPath();
    }

    @Nonnull
    @Override
    public ProduciveBeesJeiPlugin.BeeIngredient copyIngredient(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        return new ProduciveBeesJeiPlugin.BeeIngredient(beeIngredient.getBeeType(), beeIngredient.getRenderType());
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        if(beeIngredient == null) {
            return "beeingredient:null";
        }
        if(beeIngredient.getBeeType() == null) {
            return "beeingredient:bee:null";
        }
        return "beeingredient:" + beeIngredient.getBeeType().getRegistryName();
    }
}
