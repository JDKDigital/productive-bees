package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeScreen extends AbstractContainerScreen<CentrifugeContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_POWERED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/powered_centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_HEATED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/heated_centrifuge.png");

    public CentrifugeScreen(CentrifugeContainer container, Inventory inv, Component titleIn) {
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
        this.font.draw(matrixStack, this.title, -5f, 6.0F, 4210752);
        this.font.draw(matrixStack, this.playerInventoryTitle, -5f, (float) (this.getYSize() - 96 + 2), 4210752);

        this.menu.tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isHovering(129, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(Component.translatable("productivebees.screen.fluid_level", Component.translatable(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
                }
                else {
                    tooltipList.add(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText());
                }

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });

        this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();
                tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            RenderSystem.setShaderTexture(0, GUI_TEXTURE_POWERED);
        } else {
            RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        }

        // Draw main screen
        blit(matrixStack, this.getGuiLeft() - 13, this.getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());

        // Draw progress
        int progress = (int) (this.menu.tileEntity.recipeProgress * (24 / (float) this.menu.tileEntity.getProcessingTime()));
        blit(matrixStack, this.getGuiLeft() + 35, this.getGuiTop() + 35, 202, 52, progress + 1, 16);

        // Draw energy level
        if (this.menu.tileEntity instanceof PoweredCentrifugeBlockEntity) {
            blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
            this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
                int energyAmount = handler.getEnergyStored();
                int energyLevel = (int) (energyAmount * (52 / 10000F));
                blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
            });
        }

        // Draw fluid tank
        this.menu.tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                int fluidLevel = (int) (fluidStack.getAmount() * (52 / 10000F));

                FluidContainerUtil.setColors(fluidStack);

                FluidContainerUtil.drawTiledSprite(this.getGuiLeft() + 127, this.getGuiTop() + 69, 0, 4, fluidLevel, FluidContainerUtil.getSprite(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture()), 16, 16, getBlitOffset());

                FluidContainerUtil.resetColor();
            }
        });
    }
}
