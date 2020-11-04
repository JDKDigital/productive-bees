package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeScreen extends ContainerScreen<CentrifugeContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_POWERED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/powered_centrifuge.png");

    public CentrifugeScreen(CentrifugeContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);

        this.container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isPointInRegion(129, 16, 6, 54, mouseX, mouseY)) {
                List<String> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(new TranslationTextComponent(fluidStack.getTranslationKey()).getString() + " " + fluidStack.getAmount() + "mb");
                } else {
                    tooltipList.add(new TranslationTextComponent("empty").getString());
                }

                renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });

        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isPointInRegion(- 5, 16, 6, 54, mouseX, mouseY)) {
                List<String> tooltipList = new ArrayList<String>()
                {{
                    add("Energy: " + energyAmount + "FE");
                }};
                renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        assert minecraft != null;

        if (this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
            minecraft.getTextureManager().bindTexture(GUI_TEXTURE_POWERED);
        } else {
            minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        }

        // Draw main screen
        this.blit(this.guiLeft - 13, this.guiTop, 0, 0, this.xSize + 26, this.ySize);

        // Draw progress
        int progress = (int) (this.container.tileEntity.recipeProgress * (24 / (float) this.container.tileEntity.getProcessingTime()));
        this.blit(this.guiLeft + 36, this.guiTop + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            this.blit(this.guiLeft - 5, this.guiTop + 69, 206, 52, 4, -1 * energyLevel);
        });

        // Draw fluid tank
        this.container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                int fluidLevel = (int) (fluidStack.getAmount() * (52 / 10000F));

                setColors(fluidStack);

                drawTiledSprite(this.guiLeft + 127, this.guiTop + 69, 0, 4, fluidLevel, getSprite(fluidStack.getFluid().getAttributes().getStillTexture()), 16, 16, getBlitOffset());

                resetColor();
            }
        });
    }

    public static float getRed(int color) {
        return (float)(color >> 16 & 255) / 255.0F;
    }

    public static float getGreen(int color) {
        return (float)(color >> 8 & 255) / 255.0F;
    }

    public static float getBlue(int color) {
        return (float)(color & 255) / 255.0F;
    }

    public static float getAlpha(int color) {
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
            MekanismRenderer.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
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
}
