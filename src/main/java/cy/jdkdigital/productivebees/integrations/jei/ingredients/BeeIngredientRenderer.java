package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeeIngredientRenderer implements IIngredientRenderer<BeeIngredient>
{
    @Override
    public void render(@Nonnull MatrixStack matrixStack, int xPosition, int yPosition, @Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            BeeRenderer.render(matrixStack, xPosition, yPosition, beeIngredient, minecraft);
        }
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltip(BeeIngredient beeIngredient, ITooltipFlag iTooltipFlag) {
        List<ITextComponent> list = new ArrayList<>();
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
        if (nbt != null) {
            list.add(new TranslationTextComponent("entity.productivebees.bee_configurable", nbt.getString("name")));
        }
        else {
            list.add(beeIngredient.getBeeEntity().getDescription());
        }
        list.add(new StringTextComponent(beeIngredient.getBeeType().toString()).withStyle(TextFormatting.DARK_GRAY));
        return list;
    }
}
