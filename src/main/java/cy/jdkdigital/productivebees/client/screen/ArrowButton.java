package cy.jdkdigital.productivebees.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ArrowButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/icons.png");
    private final int textureX = 0;
    private final int textureY = 0;

    public ArrowButton(int x, int y, Button.OnPress onPress) {
        super(x, y, 10, 10, Component.translatable("Next"), onPress);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int i = this.getYImage(this.isHovered);
        this.blit(stack, this.x, this.y, this.textureX, this.textureY + i * this.height, this.width, this.height);
        if (this.isHovered) {
            super.renderToolTip(stack, mouseX, mouseY);
        }
    }
}
