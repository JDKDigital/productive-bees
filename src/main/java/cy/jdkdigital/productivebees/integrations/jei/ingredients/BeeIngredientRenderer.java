package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gui.AdvancedBeehiveScreen;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeeIngredientRenderer implements IIngredientRenderer<ProduciveBeesJeiPlugin.BeeIngredient> {

    @Override
    public void render(int xPosition, int yPosition, @Nullable ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();

        ResourceLocation resLocation = AdvancedBeehiveScreen.getBeeTexture(beeIngredient.getBeeType().getRegistryName(), ProductiveBees.proxy.getClientWorld());
        Minecraft.getInstance().getTextureManager().bindTexture(resLocation);
        float scale = (float) 1 / 128;
        int iconX = 14;
        int iconY = 14;
        int iconU = 20;
        int iconV = 20;

        BufferBuilder render = Tessellator.getInstance().getBuffer();
        render.begin(7, DefaultVertexFormats.POSITION_TEX);
        render.pos(xPosition, yPosition + iconY, 0D).tex((iconU) * scale, (iconV + iconY) * scale).endVertex();
        render.pos(xPosition + iconX, yPosition + iconY, 0D).tex((iconU + iconX) * scale, (iconV + iconY) * scale).endVertex();
        render.pos(xPosition + iconX, yPosition, 0D).tex((iconU + iconX) * scale, (iconV) * scale).endVertex();
        render.pos(xPosition, yPosition, 0D).tex((iconU) * scale, (iconV) * scale).endVertex();
        Tessellator.getInstance().draw();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    @Nonnull
    @Override
    public List<String> getTooltip(ProduciveBeesJeiPlugin.BeeIngredient beeIngredient, ITooltipFlag iTooltipFlag) {
        List<String> list = new ArrayList<>();
        list.add(beeIngredient.getBeeType().getName().getFormattedText());
        list.add(TextFormatting.DARK_GRAY + "" + beeIngredient.getBeeType().getRegistryName());
        return list;
    }
}
