package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientRenderer;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@JeiPlugin
public class ProduciveBeesJeiPlugin implements IModPlugin {

    private static final ResourceLocation pluginId = new ResourceLocation(ProductiveBees.MODID, ProductiveBees.MODID);
    public static final ResourceLocation CATEGORY_ADVANCED_BEEHIVE_UID = new ResourceLocation(ProductiveBees.MODID, "advanced_beehive");
    public static final ResourceLocation CATEGORY_BEE_BREEDING_UID = new ResourceLocation(ProductiveBees.MODID, "bee_breeding");
    public static final ResourceLocation CATEGORY_CENTRIFUGE_UID = new ResourceLocation(ProductiveBees.MODID, "bee_breeding");

    public static final IIngredientType<ProduciveBeesJeiPlugin.BeeIngredient> BEE_INGREDIENT = () -> ProduciveBeesJeiPlugin.BeeIngredient.class;

    public ProduciveBeesJeiPlugin() {
        BeeIngredientHelper.createList();
    }

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
        registration.addRecipeCategories(new CentrifugeRecipeCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(BEE_INGREDIENT, new ArrayList<>(BeeIngredientHelper.getOrCreateList().values()), new BeeIngredientHelper(), new BeeIngredientRenderer());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(BeeBreedingRecipeMaker.getRecipes(), CATEGORY_BEE_BREEDING_UID);

        for(Map.Entry<String, BeeIngredient> entry: BeeIngredientHelper.getOrCreateList().entrySet()) {
            registration.addIngredientInfo(entry.getValue(), BEE_INGREDIENT, "productivebees.ingredient.description." + (entry.getKey().replace("productivebees:", "")));
        }

        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        Map<ResourceLocation, IRecipe<IInventory>> advancedBeehiveRecipesMap = recipeManager.getRecipes(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE);
        registration.addRecipes(advancedBeehiveRecipesMap.values(), CATEGORY_ADVANCED_BEEHIVE_UID);

        Map<ResourceLocation, IRecipe<IInventory>> centrifugerecipesMap = recipeManager.getRecipes(CentrifugeRecipe.CENTRIFUGE);
        registration.addRecipes(centrifugerecipesMap.values(), CATEGORY_CENTRIFUGE_UID);
    }

    public static class BeeIngredient {
        private EntityType<BeeEntity> bee;
        private int renderType = 0;

        public BeeIngredient(EntityType<BeeEntity> bee, int renderType) {
            this.bee = bee;
            this.renderType = renderType;
        }

        public EntityType<BeeEntity> getBeeType() {
            return bee;
        }

        public int getRenderType() {
            return renderType;
        }

        public static ProduciveBeesJeiPlugin.BeeIngredient read(PacketBuffer buffer) {
            String beeName = buffer.readString();

            return new BeeIngredient((EntityType<BeeEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeName)), buffer.readInt());
        }

        public final void write(PacketBuffer buffer) {
            buffer.writeString("" + this.bee.getRegistryName());
            buffer.writeInt(this.renderType);
        }
    }
}
