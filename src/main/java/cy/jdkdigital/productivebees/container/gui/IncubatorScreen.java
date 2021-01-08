package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;

public class IncubatorScreen extends ContainerScreen<IncubatorContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/incubator.png");

    public IncubatorScreen(IncubatorContainer container, PlayerInventory inv, ITextComponent titleIn) {
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
        this.font.drawString(this.title.getFormattedText(), -5f, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), -5f, (float) (this.ySize - 96 + 2), 4210752);

        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isPointInRegion(- 5, 16, 6, 54, mouseX, mouseY)) {
                List<String> tooltipList = new ArrayList<>();
                tooltipList.add("Energy: " + energyAmount + "FE");

                renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
            }
        });

        this.container.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            if (handler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).isEmpty()) {
                if (isPointInRegion(80-13, 17, 18, 18, mouseX, mouseY)) {
                    List<String> tooltipList = new ArrayList<>();
                    tooltipList.add(new TranslationTextComponent("productivebees.incubator.tooltip.treat_item").getString());

                    renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
                }
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

        // Draw progress
        int progress = (int) (this.container.tileEntity.recipeProgress * (24 / (float) this.container.tileEntity.getProcessingTime()));
        this.blit(this.guiLeft + 77-13, this.guiTop + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        this.container.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            this.blit(this.guiLeft - 5, this.guiTop + 69, 206, 52, 4, -1 * energyLevel);
        });
    }
}
