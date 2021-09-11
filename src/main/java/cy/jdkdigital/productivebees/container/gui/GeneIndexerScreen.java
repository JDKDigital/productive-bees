package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.GeneIndexerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class GeneIndexerScreen extends ContainerScreen<GeneIndexerContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/gene_indexer.png");

    public GeneIndexerScreen(GeneIndexerContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        imageWidth = 256;
        imageHeight = 256;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        try {
            this.renderTooltip(matrixStack, mouseX, mouseY);
        } catch (Exception e) {
            ProductiveBees.LOGGER.info("something crashed " + e.getMessage());
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, 10f, 6.0F, 4210752);
        this.font.draw(matrixStack, this.inventory.getName(), 10f, (float) (this.getYSize() - 96 + 2), 4210752);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        assert minecraft != null;

        minecraft.getTextureManager().bind(GUI_TEXTURE);

        // Draw main screen
        blit(matrixStack, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());
    }
}
