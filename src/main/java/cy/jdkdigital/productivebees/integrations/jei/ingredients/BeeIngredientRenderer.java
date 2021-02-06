package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeeIngredientRenderer implements IIngredientRenderer<BeeIngredient>
{
    @Override
    public void render(int xPosition, int yPosition, @Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world != null) {
            BeeRenderer.render(xPosition, yPosition, beeIngredient, minecraft);
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltip(BeeIngredient beeIngredient, ITooltipFlag iTooltipFlag) {
        List<String> list = new ArrayList<>();
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
        if (nbt != null) {
            list.add(new TranslationTextComponent("entity.productivebees.bee_configurable", nbt.getString("name")).getFormattedText());
        } else {
            list.add(beeIngredient.getBeeEntity().getName().getFormattedText());
        }
        list.add(TextFormatting.DARK_GRAY + "" + beeIngredient.getBeeType());
        return list;
    }
}
