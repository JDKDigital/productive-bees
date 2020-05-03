package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.gui.AdvancedBeehiveScreen;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeIngredientRenderer implements IIngredientRenderer<ProduciveBeesJeiPlugin.BeeIngredient> {

    private final Map<Integer, Map<String, Integer>> renderSettings = new HashMap<Integer, Map<String, Integer>>() {{
        put(0, new HashMap<String, Integer>() {{
            put("scale", 128);
            put("iconX", 14);
            put("iconY", 14);
            put("iconU", 20);
            put("iconV", 20);
        }});
        put(1, new HashMap<String, Integer>() {{
            put("scale", 128);
            put("iconX", 12);
            put("iconY", 12);
            put("iconU", 20);
            put("iconV", 20);
        }});
    }};

    @Override
    public void render(int xPosition, int yPosition, @Nullable ProduciveBeesJeiPlugin.BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();

        ResourceLocation resLocation = AdvancedBeehiveScreen.getBeeTexture(beeIngredient.getBeeType().getRegistryName(), ProductiveBees.proxy.getClientWorld());
        Minecraft.getInstance().getTextureManager().bindTexture(resLocation);

        Map<String, Integer> iconSettings = renderSettings.get(beeIngredient.getRenderType());

        float scale = (float) 1 / iconSettings.get("scale");
        int iconX = iconSettings.get("iconX");
        int iconY = iconSettings.get("iconY");
        int iconU = iconSettings.get("iconU");
        int iconV = iconSettings.get("iconV");

        BufferBuilder renderBuffer = Tessellator.getInstance().getBuffer();
        renderBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderBuffer.pos(xPosition, yPosition + iconY, 0D).tex((iconU) * scale, (iconV + iconY) * scale).endVertex();
        renderBuffer.pos(xPosition + iconX, yPosition + iconY, 0D).tex((iconU + iconX) * scale, (iconV + iconY) * scale).endVertex();
        renderBuffer.pos(xPosition + iconX, yPosition, 0D).tex((iconU + iconX) * scale, (iconV) * scale).endVertex();
        renderBuffer.pos(xPosition, yPosition, 0D).tex((iconU) * scale, (iconV) * scale).endVertex();
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
