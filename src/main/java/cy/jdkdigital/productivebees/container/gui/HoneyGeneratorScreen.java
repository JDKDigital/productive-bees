package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import cy.jdkdigital.productivelib.util.FluidContainerUtil;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public class HoneyGeneratorScreen extends AbstractUpgradeableContainerScreen<HoneyGeneratorContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/honey_generator.png");

    public HoneyGeneratorScreen(HoneyGeneratorContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        if (isHovering(142, 16, 6, 54, mouseX, mouseY)) {
            FluidResource resource = this.menu.getBlockEntity().getFluidHandler().getResource(0);
            int amount = this.menu.getBlockEntity().getFluidHandler().getAmountAsInt(0);
            if (amount > 0 && !resource.isEmpty()) {
                FluidStack stack = resource.toStack(amount);
                graphics.setTooltipForNextFrame(
                        List.of(Component.translatable("productivebees.screen.fluid_level", stack.getHoverName().getString(), amount + "mB").getVisualOrderText()),
                        mouseX, mouseY);
            } else {
                graphics.setTooltipForNextFrame(
                        List.of(Component.translatable("productivebees.screen.empty").getVisualOrderText()),
                        mouseX, mouseY);
            }
        }

        if (isHovering(8, 16, 6, 54, mouseX, mouseY)) {
            int energyAmount = this.menu.getBlockEntity().getEnergyHandler().getAmountAsInt();
            graphics.setTooltipForNextFrame(
                    List.of(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText()),
                    mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        // Energy bar background
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 8, this.topPos + 17, 206.0F, 0.0F, 4, 52, 256, 256);
        float energyAmount = this.menu.getBlockEntity().getEnergyHandler().getAmountAsInt();
        int capacity = this.menu.getBlockEntity().getEnergyHandler().getCapacityAsInt();
        int energyLevel = capacity > 0 ? (int) (energyAmount * (52f / capacity)) : 0;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 8, this.topPos + 17, 8.0F, 17.0F, 4, 52 - energyLevel, 256, 256);

        FluidResource resource = this.menu.getBlockEntity().getFluidHandler().getResource(0);
        int amount = this.menu.getBlockEntity().getFluidHandler().getAmountAsInt(0);
        if (amount > 0 && !resource.isEmpty()) {
            FluidStack stack = resource.toStack(amount);
            int fluidCapacity = this.menu.getBlockEntity().getFluidHandler().getCapacityAsInt(0, resource);
            FluidContainerUtil.renderTiledFluid(graphics, this, stack, amount, fluidCapacity, 140, 17, 4, 52);
        }
    }
}
