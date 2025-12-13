package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import cy.jdkdigital.productivebees.util.FluidContainerUtil;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
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

public class HoneyGeneratorScreen extends AbstractUpgradeableContainerScreen<HoneyGeneratorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/honey_generator.png");

    public HoneyGeneratorScreen(HoneyGeneratorContainer container, Inventory inv, Component titleIn) {
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
        if (isHovering(142, 16, 6, 54, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();

            if (fluidStack.getAmount() > 0) {
                tooltipList.add(Component.translatable("productivebees.screen.fluid_level", fluidStack.getHoverName().getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
            } else {
                tooltipList.add(Component.translatable("productivebees.screen.empty").getVisualOrderText());
            }

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        int energyAmount = this.menu.getBlockEntity().getEnergyHandler().getEnergyStored();
        // Energy level tooltip
        if (isHovering(8, 16, 6, 54, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();
            tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, this.getXSize(), this.getYSize());

        // Draw energy level
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 8, getGuiTop() + 17, 206, 0, 4, 52);
        float energyAmount = (float) this.menu.getBlockEntity().getEnergyHandler().getEnergyStored();
        int energyLevel = (int) (energyAmount * (52f / (float) this.menu.getBlockEntity().getEnergyHandler().getMaxEnergyStored()));
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 8, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);

        // Draw fluid tank
        FluidStack fluidStack = this.menu.getBlockEntity().getFluidHandler().getFluidInTank(0);

        if (fluidStack.getAmount() > 0) {
            FluidContainerUtil.renderFluidTank(guiGraphics, this, fluidStack, this.menu.getBlockEntity().getFluidHandler().getTankCapacity(0), 140, 17, 4, 52, 0);
        }
    }
}
