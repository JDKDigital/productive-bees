package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HoneyGeneratorScreen extends AbstractContainerScreen<HoneyGeneratorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/honey_generator.png");

    public HoneyGeneratorScreen(HoneyGeneratorContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, -5.0f, 6.0F, 4210752);
        this.font.draw(matrixStack, this.playerInventoryTitle, -5.0f, (float) (this.getYSize() - 96 + 2), 4210752);

        this.menu.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isHovering(129, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(new TranslatableComponent("productivebees.screen.fluid_level", new TranslatableComponent(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
                } else {
                    tooltipList.add(new TranslatableComponent("productivebees.screen.empty").getVisualOrderText());
                }

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });

        this.menu.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();
                tooltipList.add(new TranslatableComponent("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        // Draw main screen
        blit(matrixStack, getGuiLeft() - 13, getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());

        // Draw energy level
        blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
        this.menu.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            float energyAmount = (float) handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52f / (float) handler.getMaxEnergyStored()));
            blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 8, 0, 4, 52 - energyLevel);
        });

        // Draw fluid tank
        this.menu.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                int fluidLevel = (int) (fluidStack.getAmount() * (52 / 10000F));

                FluidContainerUtil.setColors(fluidStack);

                FluidContainerUtil.drawTiledSprite(getGuiLeft() + 127, getGuiTop() + 69, 0, 4, fluidLevel, FluidContainerUtil.getSprite(fluidStack.getFluid().getAttributes().getStillTexture()), 16, 16, getBlitOffset());

                FluidContainerUtil.resetColor();
            }
        });
    }
}
