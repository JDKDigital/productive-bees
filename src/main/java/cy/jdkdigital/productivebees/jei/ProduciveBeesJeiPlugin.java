package cy.jdkdigital.productivebees.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collections;

@JeiPlugin
public class ProduciveBeesJeiPlugin implements IModPlugin {

    private static final ResourceLocation pluginId = new ResourceLocation(ProductiveBees.MODID, ProductiveBees.MODID);

    public static final IIngredientType<ProduciveBeesJeiPlugin.BeeIngredient> BEE_INGREDIENT = () -> ProduciveBeesJeiPlugin.BeeIngredient.class;

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new AdvancedBeehiveRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeBreedingRecipeCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(BEE_INGREDIENT, BeeIngredientHelper.createList(), new BeeIngredientHelper(), new BeeIngredientRenderer());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(Collections.emptyList(), ModBlocks.ADVANCED_OAK_BEEHIVE.get().getRegistryName());
    }

    static class BeeIngredient {
        private EntityType<BeeEntity> bee;

        public BeeIngredient(EntityType<BeeEntity> bee) {
            this.bee = bee;
        }

        public EntityType<BeeEntity> getBeeType() {
            return bee;
        }
    }
}
