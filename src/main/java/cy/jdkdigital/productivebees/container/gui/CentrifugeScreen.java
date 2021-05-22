package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.common.tileentity.PoweredCentrifugeTileEntity;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeScreen extends ContainerScreen<CentrifugeContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_POWERED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/powered_centrifuge.png");

    public CentrifugeScreen(CentrifugeContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.drawText(matrixStack, this.title, -5f, 6.0F, 4210752);
        this.font.drawText(matrixStack, this.playerInventory.getDisplayName(), -5f, (float) (this.ySize - 96 + 2), 4210752);

        this.container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isPointInRegion(129, 16, 6, 54, mouseX, mouseY)) {
                List<IReorderingProcessor> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(new TranslationTextComponent("productivebees.screen.fluid_level", new TranslationTextComponent(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mb").func_241878_f());
                }
                else {
                    tooltipList.add(new TranslationTextComponent("productivebees.hive.tooltip.empty").func_241878_f());
                }

                renderTooltip(matrixStack, tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });

        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isPointInRegion(-5, 16, 6, 54, mouseX, mouseY)) {
                List<IReorderingProcessor> tooltipList = new ArrayList<>();
                tooltipList.add(new TranslationTextComponent("productivebees.screen.energy_level", energyAmount + "FE").func_241878_f());

                renderTooltip(matrixStack, tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });

        this.container.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            if (handler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).isEmpty()) {
                if (isPointInRegion(138, 16, 18, 18, mouseX, mouseY)) {
                    List<IReorderingProcessor> tooltipList = new ArrayList<>();
                    tooltipList.add(new TranslationTextComponent("productivebees.centrifuge.tooltip.input_item").func_241878_f());

                    renderTooltip(matrixStack, tooltipList, mouseX - guiLeft, mouseY - guiTop);
                }
            }
        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        assert minecraft != null;

        if (this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
            minecraft.getTextureManager().bindTexture(GUI_TEXTURE_POWERED);
        }
        else {
            minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        }

        // Draw main screen
        blit(matrixStack, this.guiLeft - 13, this.guiTop, 0, 0, this.xSize + 26, this.ySize);

        // Draw progress
        int progress = (int) (this.container.tileEntity.recipeProgress * (24 / (float) this.container.tileEntity.getProcessingTime()));
        blit(matrixStack, this.guiLeft + 35, this.guiTop + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        if (this.container.tileEntity instanceof PoweredCentrifugeTileEntity) {
            blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
            this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                int energyAmount = handler.getEnergyStored();
                int energyLevel = (int) (energyAmount * (52 / 10000F));
                blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
            });
        }

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
