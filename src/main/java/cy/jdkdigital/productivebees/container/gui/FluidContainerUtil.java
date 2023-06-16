package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

class FluidContainerUtil
{
    private static float getRed(int color) {
        return (float) (color >> 16 & 255) / 255.0F;
    }

    private static float getGreen(int color) {
        return (float) (color >> 8 & 255) / 255.0F;
    }

    private static float getBlue(int color) {
        return (float) (color & 255) / 255.0F;
    }

    private static float getAlpha(int color) {
        return (float) (color >> 24 & 255) / 255.0F;
    }

    public static void setColors(int color) {
        RenderSystem.setShaderColor(getRed(color), getGreen(color), getBlue(color), getAlpha(color));
    }

    public static void bindTexture(ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void renderFluidTank(GuiGraphics matrices, AbstractContainerScreen<?> screen, FluidStack stack, int capacity, int x, int y, int width, int height, int depth) {
        renderFluidTank(matrices, screen, stack, stack.getAmount(), capacity, x, y, width, height, depth);
    }

    public static void renderFluidTank(GuiGraphics matrices, AbstractContainerScreen<?> screen, FluidStack stack, int amount, int capacity, int x, int y, int width, int height, int depth) {
        if(!stack.isEmpty() && capacity > 0) {
            int maxY = y + height;
            int fluidHeight = Math.min(height * amount / capacity, height);
            renderTiledFluid(matrices, screen, stack, x, maxY - fluidHeight, width, fluidHeight, depth);
        }
    }

    public static void renderTiledFluid(GuiGraphics matrices, AbstractContainerScreen<?> screen, FluidStack stack, int x, int y, int width, int height, int depth) {
        if (!stack.isEmpty()) {
            var attributes = IClientFluidTypeExtensions.of(stack.getFluid());
            TextureAtlasSprite fluidSprite = screen.getMinecraft().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(attributes.getStillTexture());
            setColors(attributes.getTintColor());
            renderTiledTextureAtlas(matrices, screen, fluidSprite, x, y, width, height, depth, false);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public static void renderTiledTextureAtlas(GuiGraphics matrices, AbstractContainerScreen<?> screen, TextureAtlasSprite sprite, int x, int y, int width, int height, int depth, boolean upsideDown) {
        // start drawing sprites
        bindTexture(sprite.atlasLocation());
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // tile vertically
        float u1 = sprite.getU0();
        float v1 = sprite.getV0();
        int spriteHeight = sprite.contents().height();
        int spriteWidth = sprite.contents().width();
        int startX = x + screen.getGuiLeft();
        int startY = y + screen.getGuiTop();
        do {
            int renderHeight = Math.min(spriteHeight, height);
            height -= renderHeight;
            float v2 = sprite.getV((16f * renderHeight) / spriteHeight);

            // we need to draw the quads per width too
            int x2 = startX;
            int widthLeft = width;
            Matrix4f matrix = matrices.pose().last().pose();
            // tile horizontally
            do {
                int renderWidth = Math.min(spriteWidth, widthLeft);
                widthLeft -= renderWidth;

                float u2 = sprite.getU((16f * renderWidth) / spriteWidth);
                if(upsideDown) {
                    buildSquare(matrix, builder, x2, x2 + renderWidth, startY, startY + renderHeight, depth, u1, u2, v2, v1);
                } else {
                    buildSquare(matrix, builder, x2, x2 + renderWidth, startY, startY + renderHeight, depth, u1, u2, v1, v2);
                }
                x2 += renderWidth;
            } while(widthLeft > 0);

            startY += renderHeight;
        } while(height > 0);

        // RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        // finish drawing sprites
        BufferUploader.drawWithShader(builder.end());
    }

    private static void buildSquare(Matrix4f matrix, BufferBuilder builder, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
        builder.vertex(matrix, x1, y2, z).uv(u1, v2).endVertex();
        builder.vertex(matrix, x2, y2, z).uv(u2, v2).endVertex();
        builder.vertex(matrix, x2, y1, z).uv(u2, v1).endVertex();
        builder.vertex(matrix, x1, y1, z).uv(u1, v1).endVertex();
    }
}
