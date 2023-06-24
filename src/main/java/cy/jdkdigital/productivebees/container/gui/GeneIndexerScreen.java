package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.GeneIndexerContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GeneIndexerScreen extends AbstractContainerScreen<GeneIndexerContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/gene_indexer.png");

    public GeneIndexerScreen(GeneIndexerContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        imageWidth = 256;
        imageHeight = 256;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        try {
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        } catch (Exception e) {
            ProductiveBees.LOGGER.info("something crashed when rendering tooltip in gene indexer" + e.getMessage());
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, 10, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, 10, (this.getYSize() - 96 + 2), 4210752, false);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());
    }
}
