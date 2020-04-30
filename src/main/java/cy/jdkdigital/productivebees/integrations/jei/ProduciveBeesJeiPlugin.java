package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientRenderer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@JeiPlugin
public class ProduciveBeesJeiPlugin implements IModPlugin {

    private static final ResourceLocation pluginId = new ResourceLocation(ProductiveBees.MODID, ProductiveBees.MODID);
    public static final ResourceLocation CATEGORY_ADVANCED_BEEHIVE_UID = new ResourceLocation(ProductiveBees.MODID, "advanced_beehive");
    public static final ResourceLocation CATEGORY_BEE_BREEDING_UID = new ResourceLocation(ProductiveBees.MODID, "bee_breeding");

    public static final IIngredientType<ProduciveBeesJeiPlugin.BeeIngredient> BEE_INGREDIENT = () -> ProduciveBeesJeiPlugin.BeeIngredient.class;

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_OAK_BEEHIVE.get()), new ResourceLocation(ProductiveBees.MODID,"advanced_beehive"));
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
        ProductiveBees.LOGGER.info(BeeIngredientHelper.createList().values());
        registration.register(BEE_INGREDIENT, new ArrayList<>(BeeIngredientHelper.createList().values()), new BeeIngredientHelper(), new BeeIngredientRenderer());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AdvancedBeehiveRecipeMaker.getRecipes(), CATEGORY_ADVANCED_BEEHIVE_UID);
        registration.addRecipes(BeeBreedingRecipeMaker.getRecipes(), CATEGORY_BEE_BREEDING_UID);
    }

    public static class BeeIngredient {
        private EntityType<BeeEntity> bee;

        public BeeIngredient(EntityType<BeeEntity> bee) {
            this.bee = bee;
        }

        public EntityType<BeeEntity> getBeeType() {
            return bee;
        }

        public static ProduciveBeesJeiPlugin.BeeIngredient read(PacketBuffer buffer) {
            String beeName = buffer.readString();

            return new BeeIngredient((EntityType<BeeEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeName)));
        }

        public final void write(PacketBuffer buffer) {
            buffer.writeString("" + this.bee.getRegistryName());
        }
    }
}
