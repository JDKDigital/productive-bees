package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.container.BreedingChamberContainer;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BreedingChamberScreen extends AbstractUpgradeableContainerScreen<BreedingChamberContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/breeding_chamber.png");

    public BreedingChamberScreen(BreedingChamberContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        List<FormattedCharSequence> tooltipList = new ArrayList<>();
        int energyAmount = this.menu.getBlockEntity().energyHandler.getAmountAsInt();

        if (isHovering(-5, 16, 6, 54, mouseX, mouseY)) {
            tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());
        }

        if (this.menu.getBlockEntity().chosenRecipe != null && minecraft != null) {
            BeeIngredient beeIngredient = this.menu.getBlockEntity().chosenRecipe.value().offspring.get();
            Entity bee = beeIngredient != null ? beeIngredient.getCachedEntity(minecraft.level) : null;
            if (bee != null && isHovering(134 - 13, 17, 16, 16, mouseX, mouseY)) {
                tooltipList.add(bee.getName().getVisualOrderText());
            }
        }

        if (isHovering(159 - 13, 14, 10, 10, mouseX, mouseY)) {
            tooltipList.add(Component.translatable("productivebees.breeding_chamber.tooltip.next_bee").getVisualOrderText());
        }

        if (isHovering(85 - 13, 14, 18, 18, mouseX, mouseY)
                && this.menu.getBlockEntity().inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE).isEmpty()) {
            tooltipList.add(Component.translatable("productivebees.breeding_chamber.tooltip.cage").getVisualOrderText());
        }

        if (!tooltipList.isEmpty()) {
            graphics.setTooltipForNextFrame(tooltipList, mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        int processingTime = this.menu.getBlockEntity().getProcessingTime(this.menu.getBlockEntity().chosenRecipe);
        int progress = (int) (this.menu.getBlockEntity().getRecipeProgress() * (45 / (float) processingTime));
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 85, this.topPos + 14, 202.0F, 52.0F, progress + 1, 22, 256, 256);

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 8, this.topPos + 17, 206.0F, 0.0F, 4, 52, 256, 256);
        int energyAmount = this.menu.getBlockEntity().energyHandler.getAmountAsInt();
        int energyLevel = (int) (energyAmount * (52 / 10000F));
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 8, this.topPos + 17, 8.0F, 17.0F, 4, 52 - energyLevel, 256, 256);

        // Bee preview
        if (minecraft != null) {
            if (this.menu.getBlockEntity().chosenRecipe != null) {
                BeeIngredient beeIngredient = this.menu.getBlockEntity().chosenRecipe.value().offspring.get();
                if (beeIngredient != null) {
                    BeeRenderer.render(graphics, this.leftPos + 134, this.topPos + 17, beeIngredient, minecraft);
                }
            } else {
                ItemStack cage1 = this.menu.getBlockEntity().inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
                ItemStack cage2 = this.menu.getBlockEntity().inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);
                if (BeeCage.isFilled(cage1) && BeeCage.isFilled(cage2)) {
                    CompoundTag tag1 = cage1.get(DataComponents.CUSTOM_DATA).copyTag();
                    CompoundTag tag2 = cage2.get(DataComponents.CUSTOM_DATA).copyTag();
                    String beeType1 = tag1.getString("type").orElseGet(() -> tag1.getString("entity").orElse(""));
                    var beeIngredient = BeeIngredientFactory.getIngredient(beeType1);
                    if (tag1.getString("name").orElse("").equals(tag2.getString("name").orElse(""))
                            && (!tag1.getBooleanOr("isProductiveBee", false)
                                || (beeIngredient.get() != null
                                    && beeIngredient.get().getCachedEntity(this.menu.getBlockEntity().getLevel()) instanceof ProductiveBee pBee
                                    && pBee.canSelfBreed()))
                            && beeIngredient.get() != null) {
                        BeeRenderer.render(graphics, this.leftPos + 134, this.topPos + 17, beeIngredient.get(), minecraft);
                    }
                }
            }
        }
    }
}
