package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class HoneyGeneratorScreen extends ContainerScreen<HoneyGeneratorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/honey_generator.png");

    public HoneyGeneratorScreen(HoneyGeneratorContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), -5.0f, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), -5.0f, (float) (this.ySize - 96 + 2), 4210752);

        this.container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isPointInRegion(129, 16, 6, 54, mouseX, mouseY)) {
                List<String> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(new TranslationTextComponent("productivebees.screen.fluid_level", new TranslationTextComponent(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mb").getString());
                } else {
                    tooltipList.add(new TranslationTextComponent("productivebees.screen.empty").getString());
                }

                renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });

        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isPointInRegion(- 5, 16, 6, 54, mouseX, mouseY)) {
                List<String> tooltipList = new ArrayList<>();
                tooltipList.add(new TranslationTextComponent("productivebees.screen.energy_level",  energyAmount + "FE").getString());

                renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        assert minecraft != null;

        minecraft.getTextureManager().bindTexture(GUI_TEXTURE);

        // Draw main screen
        this.blit(this.guiLeft - 13, this.guiTop, 0, 0, this.xSize + 26, this.ySize);

        // Draw energy level
        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            float energyAmount = (float) handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52f / (float) handler.getMaxEnergyStored()));
            ProductiveBees.LOGGER.info("energyAmount: " + energyAmount + " energyLevel: " + energyLevel + " MaxEnergyStored: " + handler.getMaxEnergyStored());
            this.blit(this.guiLeft - 5, this.guiTop + 69, 206, 52, 4, -1 * energyLevel);
        });

        // Draw fluid tank
        this.container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                int fluidLevel = (int) (fluidStack.getAmount() * (52 / 10000F));

                FluidContainerUtil.setColors(fluidStack);

                FluidContainerUtil.drawTiledSprite(this.guiLeft + 127, this.guiTop + 69, 0, 4, fluidLevel, FluidContainerUtil.getSprite(fluidStack.getFluid().getAttributes().getStillTexture()), 16, 16, getBlitOffset());

                FluidContainerUtil.resetColor();
            }
        });
    }
}
