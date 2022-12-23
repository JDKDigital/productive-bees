package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.IncubationRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.util.BeeCreator;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.NBTIngredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IncubationRecipeCategory implements IRecipeCategory<IncubationRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public IncubationRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/incubator.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.INCUBATOR.get()));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_INCUBATION_UID;
    }

    @Nonnull
    @Override
    public Class<? extends IncubationRecipe> getRecipeClass() {
        return IncubationRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.incubation");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IncubationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 9)
                .addItemStacks(Arrays.stream(recipe.input.getItems()).toList())
                .setSlotName("input");
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 27)
                .addItemStacks(Arrays.stream(recipe.catalyst.getItems()).toList())
                .setSlotName("catalyst");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 9)
                .addItemStacks(Arrays.stream(recipe.result.getItems()).toList())
                .setSlotName("result");
    }

    public static List<IncubationRecipe> getRecipes(Map<String, BeeIngredient> beeList) {
        List<IncubationRecipe> recipes = new ArrayList<>();

        // babee to adult incubation
        Bee bee = EntityType.BEE.create(ProductiveBees.proxy.getWorld());
        Bee baBee = EntityType.BEE.create(ProductiveBees.proxy.getWorld());
        if (bee != null && baBee != null) {
            ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
            ItemStack babeeCage = cage.copy();

            baBee.setAge(-24000);
            BeeCage.captureEntity(bee, cage);
            BeeCage.captureEntity(baBee, babeeCage);
            ItemStack treats = new ItemStack(ModItems.HONEY_TREAT.get(), ProductiveBeesConfig.GENERAL.incubatorTreatUse.get());
            recipes.add(new IncubationRecipe(new ResourceLocation(ProductiveBees.MODID, "cage_incubation"), NBTIngredient.of(babeeCage), Ingredient.of(treats), NBTIngredient.of(cage)));
        }

        // Spawn egg incubation
        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            NBTIngredient spawnEgg = NBTIngredient.of(BeeCreator.getSpawnEgg(entry.getKey()));
            NBTIngredient treat = NBTIngredient.of(HoneyTreat.getTypeStack(entry.getKey(), 100));
            recipes.add(new IncubationRecipe(new ResourceLocation(entry.getKey() + "_incubation"), Ingredient.of(Items.EGG), treat, spawnEgg));
        }

        return recipes;
    }
}
