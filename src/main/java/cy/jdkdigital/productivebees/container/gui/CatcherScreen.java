package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.CatcherContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class CatcherScreen extends AbstractContainerScreen<CatcherContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/catcher.png");

    public CatcherScreen(CatcherContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, 8, (this.getYSize() - 96 + 2), 4210752, false);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft() - 13, this.getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());
    }
}
