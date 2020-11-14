package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.FeederContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class FeederScreen extends ContainerScreen<FeederContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/feeder.png");

    public FeederScreen(FeederContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        assert minecraft != null;
        minecraft.getTextureManager().bindTexture(GUI_TEXTURE);

        // Draw main screen
        blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
