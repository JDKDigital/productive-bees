package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.CryoStasisContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class CryoStasisScreen extends AbstractContainerScreen<CryoStasisContainer>
{
    int scrollOff;
    private EditBox searchBox;

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/cryo_stasis.png");

    public CryoStasisScreen(CryoStasisContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.imageWidth = 276;
    }

    @Override
    protected void init() {
        super.init();

        this.searchBox = new EditBox(this.font, this.leftPos + 10, this.topPos + 147, 85, 16, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setCanLoseFocus(false);
        this.searchBox.setFocused(true);
        this.addWidget(this.searchBox);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if ((event.key() == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) || this.getFocused() != null && !this.getFocused().isFocused()) {
            return super.keyPressed(event);
        }
        return false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        // Main screen background (note: this texture is 512x256, not the standard 256x256)
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);

        // Scrollbar
        renderScroller(graphics, i, j, 2);
    }

    private void renderScroller(GuiGraphicsExtractor graphics, int offsetX, int offsetY, int items) {
        int total = items + 1 - 7;
        if (total > 1) {
            int j = 139 - (27 + (total - 1) * 139 / total);
            int k = 1 + j / total + 139 / total;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == total - 1) {
                i1 = 113;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, offsetX + 94, offsetY + 18 + i1, 0.0F, 166.0F, 6, 27, 512, 256);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, offsetX + 94, offsetY + 18, 6.0F, 166.0F, 6, 27, 512, 256);
        }
    }
}
