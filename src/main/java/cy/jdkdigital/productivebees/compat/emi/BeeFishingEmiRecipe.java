package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeFishingRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.atomic.AtomicInteger;

public class BeeFishingEmiRecipe extends BasicEmiRecipe
{
    private final HolderSet<Biome> biomes;
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_fishing_recipe.png");

    public BeeFishingEmiRecipe(RecipeHolder<BeeFishingRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BEE_FISHING_CATEGORY, recipe.id(), 126, 110);

        this.inputs.add(EmiIngredient.of(Tags.Items.TOOLS_FISHING_ROD));
        this.outputs.add(BeeEmiStack.of(recipe.value().output.get()));

        this.biomes = recipe.value().biomes;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 126, 110, 0, 0);

        widgets.addSlot(this.outputs.get(0), 92, 16).drawBack(false);

        int xPos = 0;
        AtomicInteger yPos = new AtomicInteger(45);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            var biomeRegistry = minecraft.level.registryAccess().registryOrThrow(Registries.BIOME);
            for (Holder<Biome> biome : biomes) {
                var key = biomeRegistry.getKey(biome.value());
                if (key != null) {
                    widgets.addText(Component.translatable("biome.minecraft." + key.getPath()), xPos, yPos.get(), 0xFF000000, false);
                    yPos.addAndGet(minecraft.font.lineHeight + 2);
                }
            }
        }
    }
}
