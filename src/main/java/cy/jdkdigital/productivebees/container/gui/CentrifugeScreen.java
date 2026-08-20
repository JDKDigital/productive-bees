package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
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

public class CentrifugeScreen<T extends CentrifugeContainer<? extends CentrifugeBlockEntity>> extends AbstractUpgradeableContainerScreen<T>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/centrifuge.png");
    private static final Identifier GUI_TEXTURE_POWERED = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/powered_centrifuge.png");
    private static final Identifier GUI_TEXTURE_HEATED = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/heated_centrifuge.png");

    public CentrifugeScreen(T container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        if (isHovering(140, 16, 6, 54, mouseX, mouseY)) {
            FluidResource resource = this.menu.getBlockEntity().getFluidHandler().getResource(0);
            int amount = this.menu.getBlockEntity().getFluidHandler().getAmountAsInt(0);
            if (amount > 0 && !resource.isEmpty()) {
                FluidStack stack = resource.toStack(amount);
                graphics.setTooltipForNextFrame(List.of(Component.translatable("productivebees.screen.fluid_level", stack.getHoverName().getString(), amount + "mB").getVisualOrderText()), mouseX, mouseY);
            } else {
                graphics.setTooltipForNextFrame(List.of(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText()), mouseX, mouseY);
            }
        }

        if (this.menu.getBlockEntity() instanceof PoweredCentrifugeBlockEntity poweredCentrifugeBlockEntity) {
            int energyAmount = poweredCentrifugeBlockEntity.energyHandler.getAmountAsInt();
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                graphics.setTooltipForNextFrame(List.of(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText()), mouseX, mouseY);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        Identifier texture = this.menu.getBlockEntity() instanceof PoweredCentrifugeBlockEntity ? GUI_TEXTURE_POWERED : GUI_TEXTURE;

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (this.menu.getBlockEntity() instanceof CentrifugeBlockEntity centrifugeBlockEntity && centrifugeBlockEntity.recipeProgress > 0) {
            int processingTime = centrifugeBlockEntity.getProcessingTime(centrifugeBlockEntity.getCurrentRecipe());
            int progress = (int) ((processingTime - centrifugeBlockEntity.recipeProgress) * (24 / (float) processingTime));
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 48, this.topPos + 35, 202.0F, 52.0F, progress + 1, 16, 256, 256);
        }

        if (this.menu.getBlockEntity() instanceof PoweredCentrifugeBlockEntity poweredCentrifugeBlockEntity) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 8, this.topPos + 17, 206.0F, 0.0F, 4, 52, 256, 256);
            int energyAmount = poweredCentrifugeBlockEntity.energyHandler.getAmountAsInt();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 8, this.topPos + 17, 8.0F, 17.0F, 4, 52 - energyLevel, 256, 256);
        }

        FluidResource resource = this.menu.getBlockEntity().getFluidHandler().getResource(0);
        int amount = this.menu.getBlockEntity().getFluidHandler().getAmountAsInt(0);
        if (amount > 0 && !resource.isEmpty()) {
            FluidStack stack = resource.toStack(amount);
            int capacity = this.menu.getBlockEntity().getFluidHandler().getCapacityAsInt(0, resource);
            FluidContainerUtil.renderTiledFluid(graphics, this, stack, amount, capacity, 140, 17, 4, 52);
        }
    }
}
