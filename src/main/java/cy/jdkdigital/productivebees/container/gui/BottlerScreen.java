package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivelib.util.FluidContainerUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public class BottlerScreen extends AbstractContainerScreen<BottlerContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/bottler.png");

    public BottlerScreen(BottlerContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        if (isHovering(139, 16, 6, 54, mouseX, mouseY)) {
            FluidResource resource = this.menu.getBlockEntity().fluidHandler.getResource(0);
            int amount = this.menu.getBlockEntity().fluidHandler.getAmountAsInt(0);
            if (amount > 0 && !resource.isEmpty()) {
                FluidStack stack = resource.toStack(amount);
                graphics.setTooltipForNextFrame(
                        List.of(Component.translatable("productivebees.screen.fluid_level", stack.getHoverName().getString(), amount + "mB").getVisualOrderText()),
                        mouseX, mouseY);
            } else {
                graphics.setTooltipForNextFrame(
                        List.of(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText()),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        FluidResource resource = this.menu.getBlockEntity().fluidHandler.getResource(0);
        int amount = this.menu.getBlockEntity().fluidHandler.getAmountAsInt(0);
        if (amount > 0 && !resource.isEmpty()) {
            FluidStack stack = resource.toStack(amount);
            int capacity = this.menu.getBlockEntity().fluidHandler.getCapacityAsInt(0, resource);
            FluidContainerUtil.renderTiledFluid(graphics, this, stack, amount, capacity, 140, 17, 4, 52);
        }
    }
}
