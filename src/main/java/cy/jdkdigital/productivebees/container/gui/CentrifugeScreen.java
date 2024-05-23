package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.PoweredCentrifuge;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeScreen extends AbstractContainerScreen<CentrifugeContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_POWERED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/powered_centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_HEATED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/heated_centrifuge.png");

    public CentrifugeScreen(CentrifugeContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, -5, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, -5, (this.getYSize() - 96 + 2), 4210752, false);

        FluidStack fluidStack = this.menu.blockEntity.fluidInventory.getFluidInTank(0);

        // Fluid level tooltip
        if (isHovering(129, 16, 6, 54, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();

            if (fluidStack.getAmount() > 0) {
                tooltipList.add(Component.translatable("productivebees.screen.fluid_level", fluidStack.getHoverName().getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
            } else {
                tooltipList.add(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText());
            }

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        if (this.menu.blockEntity instanceof PoweredCentrifugeBlockEntity poweredCentrifugeBlockEntity) {
            int energyAmount = poweredCentrifugeBlockEntity.energyHandler.getEnergyStored();

            // Energy level tooltip
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();
                tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        var GUI = this.menu.blockEntity instanceof PoweredCentrifugeBlockEntity ? GUI_TEXTURE_POWERED : GUI_TEXTURE;

        // Draw main screen
        guiGraphics.blit(GUI, this.getGuiLeft() - 13, this.getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());

        // Draw progress
        int progress = (int) (this.menu.blockEntity.recipeProgress * (24 / (float) this.menu.blockEntity.getProcessingTime(this.menu.blockEntity.getCurrentRecipe())));
        guiGraphics.blit(GUI, this.getGuiLeft() + 35, this.getGuiTop() + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        if (this.menu.blockEntity instanceof PoweredCentrifugeBlockEntity poweredCentrifugeBlockEntity) {
            guiGraphics.blit(GUI, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
            int energyAmount = poweredCentrifugeBlockEntity.energyHandler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            guiGraphics.blit(GUI, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
        }

        // Draw fluid tank
        FluidStack fluidStack = this.menu.blockEntity.fluidInventory.getFluidInTank(0);

        if (fluidStack.getAmount() > 0) {
            FluidContainerUtil.renderFluidTank(guiGraphics, this, fluidStack, this.menu.blockEntity.fluidInventory.getTankCapacity(0), 127, 17, 4, 52, 0);
        }
    }
}
