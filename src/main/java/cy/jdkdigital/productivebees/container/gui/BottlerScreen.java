package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivebees.util.FluidContainerUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class BottlerScreen extends AbstractContainerScreen<BottlerContainer>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/bottler.png");

    public BottlerScreen(BottlerContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // Draw fluid tank
        FluidStack fluidStack = this.menu.getBlockEntity().fluidHandler.getFluidInTank(0);

        // Fluid level tooltip
        if (isHovering(139, 16, 6, 54, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();

            if (fluidStack.getAmount() > 0) {
                tooltipList.add(Component.translatable("productivebees.screen.fluid_level", fluidStack.getHoverName().getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
            } else {
                tooltipList.add(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText());
            }

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());

        // Draw fluid tank
        FluidStack fluidStack = this.menu.getBlockEntity().fluidHandler.getFluidInTank(0);

        if (fluidStack.getAmount() > 0) {
            FluidContainerUtil.renderFluidTank(guiGraphics, this, fluidStack, this.menu.getBlockEntity().fluidHandler.getTankCapacity(0), 140, 17, 4, 52, 0);
        }
    }
}
