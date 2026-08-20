package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class IncubatorScreen extends AbstractUpgradeableContainerScreen<IncubatorContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/incubator.png");

    public IncubatorScreen(IncubatorContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        int energyAmount = this.menu.getBlockEntity().energyHandler.getAmountAsInt();

        if (isHovering(8, 16, 6, 54, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(
                    List.of(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText()),
                    mouseX, mouseY);
        }

        if (this.menu.getBlockEntity().inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).isEmpty()
                && isHovering(80, 17, 18, 18, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(
                    List.of(Component.translatable("productivebees.incubator.tooltip.treat_item").getVisualOrderText()),
                    mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        // Progress bar
        int processingTime = this.menu.getBlockEntity().getProcessingTime(this.menu.getBlockEntity().getCurrentRecipe());
        int progress = (int) (this.menu.getBlockEntity().recipeProgress * (24 / (float) processingTime));
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 76, this.topPos + 35, 202.0F, 52.0F, progress + 1, 16, 256, 256);

        // Energy bar background
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 8, this.topPos + 17, 206.0F, 0.0F, 4, 52, 256, 256);

        // Energy bar fill (the bar's "empty" portion blits over the energy level)
        int energyAmount = this.menu.getBlockEntity().energyHandler.getAmountAsInt();
        int energyLevel = (int) (energyAmount * (52 / 10000F));
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 8, this.topPos + 17, 8.0F, 17.0F, 4, 52 - energyLevel, 256, 256);
    }
}
