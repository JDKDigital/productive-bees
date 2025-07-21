package cy.jdkdigital.productivebees.compat.hwyla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import snownee.jade.api.ui.Element;

public class BeeEntityElement extends Element {

    private final BeeIngredient bee;
    private final float scale;

    private BeeEntityElement(BeeIngredient bee, float scale) {
        this.bee = bee;
        this.scale = scale == 0 ? 1 : scale;
    }

    public static BeeEntityElement of(BeeIngredient bee) {
        return of(bee, 1);
    }

    public static BeeEntityElement of(BeeIngredient bee, float scale) {
        return new BeeEntityElement(bee, scale);
    }

    @Override
    public Vec2 getSize() {
        int size = Mth.floor(18 * scale);
        return new Vec2(size, size);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        Minecraft mc = Minecraft.getInstance();
        BeeRenderer.render(guiGraphics, (int) x + 1, (int) y + 1, this.bee, mc);
    }
}
