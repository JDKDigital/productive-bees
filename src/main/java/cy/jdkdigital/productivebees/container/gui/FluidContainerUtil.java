package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

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

    public static void setColors(@Nonnull FluidStack fluid) {
        if (!fluid.isEmpty()) {
            setColors(fluid.getFluid().getAttributes().getColor(fluid));
        }
    }

    public static void resetColor() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        if (desiredWidth != 0 && desiredHeight != 0 && textureWidth != 0 && textureHeight != 0) {
            bindTexture(TextureAtlas.LOCATION_BLOCKS);
            int xTileCount = desiredWidth / textureWidth;
            int xRemainder = desiredWidth - xTileCount * textureWidth;
            int yTileCount = desiredHeight / textureHeight;
            int yRemainder = desiredHeight - yTileCount * textureHeight;
            int yStart = yPosition + yOffset;
            float uMin = sprite.getU0();
            float uMax = sprite.getU1();
            float vMin = sprite.getV0();
            float vMax = sprite.getV1();
            float uDif = uMax - uMin;
            float vDif = vMax - vMin;
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            BufferBuilder vertexBuffer = Tesselator.getInstance().getBuilder();
            vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            for (int xTile = 0; xTile <= xTileCount; ++xTile) {
                int width = xTile == xTileCount ? xRemainder : textureWidth;
                if (width == 0) {
                    break;
                }

                int x = xPosition + xTile * textureWidth;
                int maskRight = textureWidth - width;
                int shiftedX = x + textureWidth - maskRight;
                float uMaxLocal = uMax - uDif * (float) maskRight / (float) textureWidth;

                for (int yTile = 0; yTile <= yTileCount; ++yTile) {
                    int height = yTile == yTileCount ? yRemainder : textureHeight;
                    if (height == 0) {
                        break;
                    }

                    int y = yStart - (yTile + 1) * textureHeight;
                    int maskTop = textureHeight - height;
                    float vMaxLocal = vMax - vDif * (float) maskTop / (float) textureHeight;
                    vertexBuffer.vertex(x, y + textureHeight, zLevel).uv(uMin, vMaxLocal).endVertex();
                    vertexBuffer.vertex(shiftedX, y + textureHeight, zLevel).uv(uMaxLocal, vMaxLocal).endVertex();
                    vertexBuffer.vertex(shiftedX, y + maskTop, zLevel).uv(uMaxLocal, vMin).endVertex();
                    vertexBuffer.vertex(x, y + maskTop, zLevel).uv(uMin, vMin).endVertex();
                }
            }

            vertexBuffer.end();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(spriteLocation);
    }

    public static void bindTexture(ResourceLocation texture) {
        Minecraft.getInstance().textureManager.bindForSetup(texture);
    }
}
