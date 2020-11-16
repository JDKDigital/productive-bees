package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

class FluidContainerUtil
{
    private static float getRed(int color) {
        return (float)(color >> 16 & 255) / 255.0F;
    }

    private static float getGreen(int color) {
        return (float)(color >> 8 & 255) / 255.0F;
    }

    private static float getBlue(int color) {
        return (float)(color & 255) / 255.0F;
    }

    private static float getAlpha(int color) {
        return (float)(color >> 24 & 255) / 255.0F;
    }

    public static void setColors(int color) {
        RenderSystem.color4f(getRed(color), getGreen(color), getBlue(color), getAlpha(color));
    }

    public static void setColors(@Nonnull FluidStack fluid) {
        if (!fluid.isEmpty()) {
            setColors(fluid.getFluid().getAttributes().getColor(fluid));
        }
    }

    public static void resetColor() {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        if (desiredWidth != 0 && desiredHeight != 0 && textureWidth != 0 && textureHeight != 0) {
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            int xTileCount = desiredWidth / textureWidth;
            int xRemainder = desiredWidth - xTileCount * textureWidth;
            int yTileCount = desiredHeight / textureHeight;
            int yRemainder = desiredHeight - yTileCount * textureHeight;
            int yStart = yPosition + yOffset;
            float uMin = sprite.getMinU();
            float uMax = sprite.getMaxU();
            float vMin = sprite.getMinV();
            float vMax = sprite.getMaxV();
            float uDif = uMax - uMin;
            float vDif = vMax - vMin;
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
            vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

            for(int xTile = 0; xTile <= xTileCount; ++xTile) {
                int width = xTile == xTileCount ? xRemainder : textureWidth;
                if (width == 0) {
                    break;
                }

                int x = xPosition + xTile * textureWidth;
                int maskRight = textureWidth - width;
                int shiftedX = x + textureWidth - maskRight;
                float uMaxLocal = uMax - uDif * (float)maskRight / (float)textureWidth;

                for(int yTile = 0; yTile <= yTileCount; ++yTile) {
                    int height = yTile == yTileCount ? yRemainder : textureHeight;
                    if (height == 0) {
                        break;
                    }

                    int y = yStart - (yTile + 1) * textureHeight;
                    int maskTop = textureHeight - height;
                    float vMaxLocal = vMax - vDif * (float)maskTop / (float)textureHeight;
                    vertexBuffer.pos(x, y + textureHeight, zLevel).tex(uMin, vMaxLocal).endVertex();
                    vertexBuffer.pos(shiftedX, y + textureHeight, zLevel).tex(uMaxLocal, vMaxLocal).endVertex();
                    vertexBuffer.pos(shiftedX, y + maskTop, zLevel).tex(uMaxLocal, vMin).endVertex();
                    vertexBuffer.pos(x, y + maskTop, zLevel).tex(uMin, vMin).endVertex();
                }
            }

            vertexBuffer.finishDrawing();
            WorldVertexBufferUploader.draw(vertexBuffer);
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(spriteLocation);
    }

    public static void bindTexture(ResourceLocation texture) {
        Minecraft.getInstance().textureManager.bindTexture(texture);
    }
}
