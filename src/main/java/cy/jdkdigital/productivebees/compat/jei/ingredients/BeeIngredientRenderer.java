package cy.jdkdigital.productivebees.compat.jei.ingredients;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class BeeIngredientRenderer implements IIngredientRenderer<BeeIngredient>
{
    private static final Map<BeeIngredient, List<Component>> TOOLTIP_CACHE = new WeakHashMap<>();
    private static final Map<BeeIngredient, ItemStack> CAGE_CACHE = new WeakHashMap<>();

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, BeeIngredient ingredient) {
        int x = (int) guiGraphics.pose().m20();
        int y = (int) guiGraphics.pose().m21();
        if (ProductiveBeesConfig.CLIENT.simpleBeeIngredientRender.get()) {
            ItemStack cage = getCageStack(ingredient);
            if (!cage.isEmpty()) {
                guiGraphics.item(cage, x, y);
                return;
            }
        }
        BeeRenderer.render(guiGraphics, x, y, ingredient, Minecraft.getInstance());
    }

    public static ItemStack getCageStack(BeeIngredient ingredient) {
        ItemStack cached = CAGE_CACHE.get(ingredient);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        ItemStack built = buildCageStack(ingredient);
        if (!built.isEmpty()) {
            CAGE_CACHE.put(ingredient, built);
        }
        return built;
    }

    private static ItemStack buildCageStack(BeeIngredient ingredient) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return ItemStack.EMPTY;
        }
        Entity entity = ingredient.getCachedEntity(level);
        if (!(entity instanceof Bee bee)) {
            return ItemStack.EMPTY;
        }
        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(bee, cage);
        return cage;
    }

    @Nonnull
    @Override
    public List<Component> getTooltip(BeeIngredient beeIngredient, TooltipFlag iTooltipFlag) {
        return TOOLTIP_CACHE.computeIfAbsent(beeIngredient, BeeIngredientRenderer::buildTooltip);
    }

    private static List<Component> buildTooltip(BeeIngredient beeIngredient) {
        Component label = BeeRegistries.lookup(beeIngredient.getBeeType()) != null
                ? Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeIngredient.getBeeType()) + "_bee")
                : beeIngredient.getBeeEntity().getDescription();
        Component id = Component.literal(beeIngredient.getBeeType().toString()).withStyle(ChatFormatting.DARK_GRAY);
        return List.of(label, id);
    }

}
