package cy.jdkdigital.productivebees.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FluidContainerUtil
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

        int spriteHeight = sprite.contents().height();
        int spriteWidth = sprite.contents().width();
        // tile vertically
        int startX = x + screen.getGuiLeft();
        int startY = y + screen.getGuiTop();

        Matrix4f matrix = matrices.pose().last().pose();

        final int xTileCount = width / spriteWidth;
        final int xRemainder = width - (xTileCount * spriteWidth);
        final long yTileCount = height / spriteHeight;
        final long yRemainder = height - (yTileCount * spriteHeight);

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int widthLeft = (xTile == xTileCount) ? xRemainder : spriteWidth;
                long heightLeft = (yTile == yTileCount) ? yRemainder : spriteHeight;
                int x2 = startX + (xTile * spriteWidth);
                int y2 = startY + height - ((yTile + 1) * spriteHeight);
                if (widthLeft > 0 && heightLeft > 0) {
                    long maskTop = spriteHeight - heightLeft;
                    int maskRight = spriteWidth - widthLeft;

                    drawTextureWithMasking(matrix, x2, y2, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, float zLevel) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16F * (uMax - uMin));
        vMax = vMax - (maskTop / 16F * (vMax - vMin));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix, xCoord, yCoord + 16, zLevel).setUv(uMin, vMax);
        bufferBuilder.addVertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).setUv(uMax, vMax);
        bufferBuilder.addVertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).setUv(uMax, vMin);
        bufferBuilder.addVertex(matrix, xCoord, yCoord + maskTop, zLevel).setUv(uMin, vMin);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
