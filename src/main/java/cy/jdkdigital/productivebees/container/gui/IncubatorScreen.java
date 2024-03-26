package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class IncubatorScreen extends AbstractContainerScreen<IncubatorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/incubator.png");

    public IncubatorScreen(IncubatorContainer container, Inventory inv, Component titleIn) {
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
        guiGraphics.drawString(font, this.title, -5, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, -5, (this.getYSize() - 96 + 2), 4210752, false);

        this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();
                tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });

        this.menu.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).isEmpty()) {
                if (isHovering(80 - 13, 17, 18, 18, mouseX, mouseY)) {
                    List<FormattedCharSequence> tooltipList = new ArrayList<>();
                    tooltipList.add(Component.translatable("productivebees.incubator.tooltip.treat_item").getVisualOrderText());

                    guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
                }
            }
        });
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() - 13, getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());

        // Draw progress
        int progress = (int) (this.menu.tileEntity.recipeProgress * (24 / (float) this.menu.tileEntity.getProcessingTime(this.menu.tileEntity.getCurrentRecipe())));
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 76 - 13, getGuiTop() + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
        this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            guiGraphics.blit(GUI_TEXTURE, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
        });
    }
}
