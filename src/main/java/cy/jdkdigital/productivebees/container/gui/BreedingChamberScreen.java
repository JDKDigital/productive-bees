package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.container.BreedingChamberContainer;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BreedingChamberScreen extends AbstractContainerScreen<BreedingChamberContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/breeding_chamber.png");

    public BreedingChamberScreen(BreedingChamberContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, -5, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, -5, (this.getYSize() - 96 + 2), 4210752, false);

        List<FormattedCharSequence> tooltipList = new ArrayList<>();
        this.menu.blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();

            // Energy level tooltip
            if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
                tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());
            }
        });

        // Bee output tooltip
        if (this.menu.blockEntity.chosenRecipe != null && minecraft != null) {
            BeeIngredient beeIngredient = this.menu.blockEntity.chosenRecipe.offspring.get();
            Entity bee = null;
            if (beeIngredient != null) {
                bee = beeIngredient.getCachedEntity(minecraft.level);
            }

            if (bee != null) {
                if (isHovering(134 - 13, 17, 16, 16, mouseX, mouseY)) {
                    tooltipList.add(bee.getName().getVisualOrderText());
                }
            }
        }

        // Progress countdown
//        if (isHovering(85 - 13, 14, 45, 22, mouseX, mouseY)) {
//            tooltipList.add(Component.translatable("productivebees.breeding_chamber.tooltip.progress", (this.menu.tileEntity.getProcessingTime() - this.menu.tileEntity.getRecipeProgress()) / 20).getVisualOrderText());
//        }

        // Up arrow
        if (isHovering(159 - 13, 14, 10, 10, mouseX, mouseY)) {
            tooltipList.add(Component.translatable("productivebees.breeding_chamber.tooltip.next_bee").getVisualOrderText());
        }

        // Empty cage slot
        if (isHovering(85 - 13, 14, 18, 18, mouseX, mouseY)) {
            this.menu.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                if (handler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE).isEmpty()) {
                    tooltipList.add(Component.translatable("productivebees.breeding_chamber.tooltip.cage").getVisualOrderText());
                }
            });
        }
        guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() - 13, getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());

        // Draw progress
        int progress = (int) (this.menu.blockEntity.getRecipeProgress() * (45 / (float) this.menu.blockEntity.getProcessingTime(this.menu.blockEntity.chosenRecipe)));
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() + 85 - 13, getGuiTop() + 14, 202, 52, progress + 1, 22);

        // Draw energy level
        guiGraphics.blit(GUI_TEXTURE, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, 52);
        this.menu.blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();
            int energyLevel = (int) (energyAmount * (52 / 10000F));
            guiGraphics.blit(GUI_TEXTURE, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, 52 - energyLevel);
        });

        // Draw output bee
        if (minecraft != null) {
            if (this.menu.blockEntity.chosenRecipe != null) {
                BeeIngredient beeIngredient = this.menu.blockEntity.chosenRecipe.offspring.get();

                if (beeIngredient != null) {
                    BeeRenderer.render(guiGraphics, getGuiLeft() + 134 - 13, getGuiTop() + 17, beeIngredient, minecraft);
                }
            } else {
                this.menu.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    ItemStack cage1 = handler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
                    ItemStack cage2 = handler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);
                    if (BeeCage.isFilled(cage1) && BeeCage.isFilled(cage2)) {
                        CompoundTag tag1 = cage1.getTag();
                        CompoundTag tag2 = cage2.getTag();
                        if (tag1 != null) {
                            var beeData = BeeIngredientFactory.getIngredient(tag1.contains("type") ? tag1.getString("type") : tag1.getString("entity"));
                            if (tag1.getString("name").equals(tag2.getString("name")) && (!tag1.getBoolean("isProductiveBee") || (beeData.get() != null && (beeData.get().getCachedEntity(this.menu.blockEntity.getLevel()) instanceof ProductiveBee pBee) && pBee.canSelfBreed()))) {
                                Supplier<BeeIngredient> beeIngredient = BeeIngredientFactory.getIngredient(tag1.getString("type"));
                                if (beeIngredient.get() != null) {
                                    BeeRenderer.render(guiGraphics, getGuiLeft() + 134 - 13, getGuiTop() + 17, beeIngredient.get(), minecraft);
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
