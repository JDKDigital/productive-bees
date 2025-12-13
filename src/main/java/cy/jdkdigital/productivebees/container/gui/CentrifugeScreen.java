package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.util.FluidContainerUtil;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import cy.jdkdigital.productivelib.common.block.entity.ICapabilityBlockEntity;
import cy.jdkdigital.productivelib.container.AbstractContainer;
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

public class CentrifugeScreen<T extends CentrifugeContainer<? extends CentrifugeBlockEntity>> extends AbstractUpgradeableContainerScreen<T>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_POWERED = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/powered_centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_HEATED = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/heated_centrifuge.png");

    public CentrifugeScreen(T container, Inventory inv, Component titleIn) {
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

        FluidStack fluidStack = this.menu.getBlockEntity().getFluidHandler().getFluidInTank(0);

        // Fluid level tooltip
        if (isHovering(140, 16, 6, 54, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();

            if (fluidStack.getAmount() > 0) {
                tooltipList.add(Component.translatable("productivebees.screen.fluid_level", fluidStack.getHoverName().getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
            } else {
                tooltipList.add(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText());
            }

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        if (this.menu.getBlockEntity() instanceof PoweredCentrifugeBlockEntity poweredCentrifugeBlockEntity) {
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
        var GUI = this.menu.getBlockEntity() instanceof PoweredCentrifugeBlockEntity ? GUI_TEXTURE_POWERED : GUI_TEXTURE;

        // Draw main screen
        guiGraphics.blit(GUI, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());

        // Draw progress
        if (this.menu.getBlockEntity() instanceof CentrifugeBlockEntity centrifugeBlockEntity) {
            if (centrifugeBlockEntity.recipeProgress > 0) {
                int processingTime = centrifugeBlockEntity.getProcessingTime(centrifugeBlockEntity.getCurrentRecipe());
                int progress = (int) ((processingTime - centrifugeBlockEntity.recipeProgress) * (24 / (float) processingTime));
                guiGraphics.blit(GUI, this.getGuiLeft() + 48, this.getGuiTop() + 35, 202, 52, progress + 1, 16);
            }
        }

        // Draw energy level
        if (this.menu.getBlockEntity() instanceof PoweredCentrifugeBlockEntity poweredCentrifugeBlockEntity) {
            guiGraphics.blit(GUI, getGuiLeft() + 8, getGuiTop() + 17, 206, 0, 4, 52);
            int energyAmount = poweredCentrifugeBlockEntity.energyHandler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            guiGraphics.blit(GUI, getGuiLeft() + 8, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
        }

        // Draw fluid tank
        FluidStack fluidStack = this.menu.getBlockEntity().getFluidHandler().getFluidInTank(0);

        if (fluidStack.getAmount() > 0) {
            FluidContainerUtil.renderFluidTank(guiGraphics, this, fluidStack, this.menu.getBlockEntity().getFluidHandler().getTankCapacity(0), 140, 17, 4, 52, 0);
        }
    }
}
