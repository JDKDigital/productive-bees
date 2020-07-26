package cy.jdkdigital.productivebees.datagen.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItemGroups;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider implements IConditionBuilder
{
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        addCentrifugeRecipe(consumer);
        addBottlerRecipe(consumer);

    }

    private void addBottlerRecipe(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.CENTRIFUGE.get())
                .patternLine("ISI")
                .patternLine("IGI")
                .patternLine("III")
                .key('I', Items.IRON_INGOT)
                .key('G', ItemTags.getCollection().getOrCreate(new ResourceLocation("forge:glass")))
                .key('S', Items.SMOOTH_STONE_SLAB)
                .setGroup(ModItemGroups.PRODUCTIVE_BEES.getTabLabel())
                .addCriterion("items", InventoryChangeTrigger.Instance.forItems(Items.SMOOTH_STONE_SLAB, Items.IRON_INGOT))
                .build(consumer);
    }

    private void addCentrifugeRecipe(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.CENTRIFUGE.get())
            .patternLine("I I")
            .patternLine("IGI")
            .patternLine("III")
            .key('I', Items.IRON_INGOT)
            .key('G', Items.GRINDSTONE)
            .setGroup(ModItemGroups.PRODUCTIVE_BEES.getTabLabel())
            .addCriterion("items", InventoryChangeTrigger.Instance.forItems(Items.GRINDSTONE, Items.IRON_INGOT))
            .build(consumer);

        ConditionalRecipe.builder().addCondition(
            and(
                not(modLoaded("mekanism")),
                not(modLoaded("silents_mekanism"))
            )
        ).addRecipe(
            ShapedRecipeBuilder.shapedRecipe(ModBlocks.POWERED_CENTRIFUGE.get())
                .patternLine("RCR")
                .key('R', Items.REDSTONE)
                .key('C', ModBlocks.CENTRIFUGE.get())
                .setGroup(ModItemGroups.PRODUCTIVE_BEES.getTabLabel())
                .addCriterion("items", InventoryChangeTrigger.Instance.forItems(ModBlocks.CENTRIFUGE.get(), Items.REDSTONE))
                ::build
        )
        .build(consumer, new ResourceLocation(ProductiveBees.MODID, "powered_centrifuge"));

        Item basicCircuit = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mekanism", "basic_control_circuit"));
        ConditionalRecipe.builder().addCondition(
            modLoaded("mekanism")
        )
        .addRecipe(
            ShapedRecipeBuilder.shapedRecipe(ModBlocks.POWERED_CENTRIFUGE.get())
                .patternLine("RBR")
                .patternLine("ICI")
                .patternLine("RBR")
                .key('R', Items.REDSTONE)
                .key('I', Items.IRON_INGOT)
                .key('B', basicCircuit)
                .key('C', ModBlocks.CENTRIFUGE.get())
                .setGroup(ModItemGroups.PRODUCTIVE_BEES.getTabLabel())
                .addCriterion("items", InventoryChangeTrigger.Instance.forItems(ModBlocks.CENTRIFUGE.get(), basicCircuit))
                ::build
        )
        .build(consumer, new ResourceLocation(ProductiveBees.MODID, "powered_centrifuge_mekanism"));

        Item machineFrame = ForgeRegistries.ITEMS.getValue(new ResourceLocation("silents_mechanisms", "alloy_machine_frame"));
        ConditionalRecipe.builder().addCondition(
            modLoaded("silents_mechanisms")
        )
        .addRecipe(
            ShapedRecipeBuilder.shapedRecipe(ModBlocks.POWERED_CENTRIFUGE.get())
                .patternLine("ICI")
                .patternLine("RBR")
                .key('R', Items.REDSTONE)
                .key('I', ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "ingots/bismuth_brass")))
                .key('B', machineFrame)
                .key('C', ModBlocks.CENTRIFUGE.get())
                .setGroup(ModItemGroups.PRODUCTIVE_BEES.getTabLabel())
                .addCriterion("items", InventoryChangeTrigger.Instance.forItems(ModBlocks.CENTRIFUGE.get(), machineFrame))
                ::build
        )
        .build(consumer, new ResourceLocation(ProductiveBees.MODID, "powered_centrifuge_silents_mechanisms"));
    }
}