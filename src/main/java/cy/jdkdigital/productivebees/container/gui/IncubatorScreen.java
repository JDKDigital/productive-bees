package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class IncubatorScreen extends AbstractUpgradeableContainerScreen<IncubatorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/incubator.png");

    public IncubatorScreen(IncubatorContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        int energyAmount = this.menu.getBlockEntity().energyHandler.getEnergyStored();

        // Energy level tooltip
        if (isHovering(8, 16, 6, 54, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();
            tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        if (this.menu.getBlockEntity().inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).isEmpty()) {
            if (isHovering(80 , 17, 18, 18, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();
                tooltipList.add(Component.translatable("productivebees.incubator.tooltip.treat_item").getVisualOrderText());

                guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, this.getXSize(), this.getYSize());

        // Draw progress
        int progress = (int) (this.menu.getBlockEntity().recipeProgress * (24 / (float) this.menu.getBlockEntity().getProcessingTime(this.menu.getBlockEntity().getCurrentRecipe())));
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 76, getGuiTop() + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 8, getGuiTop() + 17, 206, 0, 4, 52);
        int energyAmount = this.menu.getBlockEntity().energyHandler.getEnergyStored();
        int energyLevel = (int) (energyAmount * (52 / 10000F));
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 8, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
    }
}
