package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class BottlerScreen extends ContainerScreen<BottlerContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/bottler.png");

    public BottlerScreen(BottlerContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
        this.font.draw(matrixStack, this.inventory.getName(), 8.0F, (float) (this.getYSize() - 96 + 2), 4210752);

        // Draw fluid tank
        this.menu.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            if (isHovering(139, 16, 6, 54, mouseX, mouseY)) {
                List<IReorderingProcessor> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(new TranslationTextComponent("productivebees.screen.fluid_level", new TranslationTextComponent(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
                } else {
                    tooltipList.add(new TranslationTextComponent("productivebees.hive.tooltip.empty").getVisualOrderText());
                }

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        assert minecraft != null;
        minecraft.textureManager.bind(GUI_TEXTURE);

        // Draw main screen
        blit(matrixStack, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());

        // Draw fluid tank
        this.menu.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);
            int fluidLevel = (int) (fluidStack.getAmount() * (52 / 10000F));
            if (fluidStack.getAmount() > 0) {
                FluidContainerUtil.setColors(fluidStack);

                FluidContainerUtil.drawTiledSprite(this.getGuiLeft() + 140, this.getGuiTop() + 69, 0, 4, fluidLevel, FluidContainerUtil.getSprite(fluidStack.getFluid().getAttributes().getStillTexture()), 16, 16, getBlitOffset());

                FluidContainerUtil.resetColor();
            }
        });
    }
}
