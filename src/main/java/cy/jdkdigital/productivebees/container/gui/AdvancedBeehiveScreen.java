package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.entity.DragonEggHiveBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.neoforged.fml.ModList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedBeehiveScreen extends AbstractContainerScreen<AdvancedBeehiveContainer>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
    private static final ResourceLocation GUI_TEXTURE_EXPANDED = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");
    private static final ResourceLocation GUI_TEXTURE_SIMULATED = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_simulated.png");

    public AdvancedBeehiveScreen(AdvancedBeehiveContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean expanded = this.menu.blockEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        boolean simulated = expanded && this.menu.blockEntity.isSim();

        guiGraphics.drawString(font, this.title, expanded ? -5 : 8, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, expanded ? -5 : 8, (this.getYSize() - 96 + 2), 4210752, false);

        assert minecraft != null;
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;
        List<FormattedCharSequence> tooltipList = new ArrayList<FormattedCharSequence>();

        // Cage slot tooltip
        if (simulated && isHovering(86 - 13, 53, 16, 16, mouseX, mouseY) && this.menu.blockEntity.inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).isEmpty()) {
            tooltipList.add(Component.translatable("productivebees.advanced_hive.tooltip.bee_cage").getVisualOrderText());
        }

        // Bee Tooltips
        int j = 0;
        for (BeehiveBlockEntity.BeeData inhabitant : this.menu.blockEntity.stored) {
            var occupant = inhabitant.occupant.entityData().copyTag();

            Entity bee = null;
            String type = occupant.getString("type");
            if (type.isEmpty()) {
                type = occupant.getString("id");
            }
            BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();
            if (beeIngredient != null) {
                bee = beeIngredient.getCachedEntity(minecraft.level);
            } else {
                ProductiveBees.LOGGER.info("try render bee tooltip " + beeIngredient + " " + type + " " + isHovering(positions.get(j).get(0) - (expanded ? 13 : 0), positions.get(j).get(1), 16, 16, mouseX, mouseY) + " " + bee);
            }

            if (bee != null && bee.getEncodeId() != null) {
                if (bee instanceof ConfigurableBee && occupant.contains("type")) {
                    ((ConfigurableBee) bee).setBeeType(occupant.getString("type"));
                }

                if (positions.containsKey(j) && isHovering(positions.get(j).get(0) - (expanded ? 13 : 0), positions.get(j).get(1), 16, 16, mouseX, mouseY)) {
                    CompoundTag tag = occupant.copy();
                    tooltipList.add(bee.getName().getVisualOrderText());

                    if (Screen.hasShiftDown()) {
                        String modId = ResourceLocation.parse(bee.getEncodeId()).getNamespace();
                        if (modId.equals(ProductiveBees.MODID)) {
                            tag.putBoolean("isProductiveBee", true);
                        }

                        String modName = ModList.get().getModContainerById(modId).get().getClass().getSimpleName();
                        if (modId.equals("minecraft")) {
                            modName = "Minecraft";
                        }
                        tag.putString("mod", modName);

                        List<Component> list = BeeHelper.populateBeeInfoFromTag(tag, null);

                        for (Component textComponent : list) {
                            tooltipList.add(textComponent.getVisualOrderText());
                        }
                    } else {
                        tooltipList.add(Component.translatable("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE).getVisualOrderText());
                    }
                }
            }
            j++;
        }
        guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        boolean expanded = this.menu.blockEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        boolean simulated = expanded && this.menu.blockEntity.isSim();
        var GUI = expanded ? (simulated ? GUI_TEXTURE_SIMULATED : GUI_TEXTURE_EXPANDED) : GUI_TEXTURE;

        int honeyLevel = this.menu.blockEntity.getBlockState().getValue(BeehiveBlock.HONEY_LEVEL);
        // Draw main screen
        guiGraphics.blit(GUI, getGuiLeft() - (expanded ? 13 : 0), getGuiTop(), 0, 0, this.getXSize() + (expanded ? 26 : 0), this.getYSize());
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        // Draw honey level
        int xOffset = this.menu.blockEntity instanceof DragonEggHiveBlockEntity ? 13 : 0;
        guiGraphics.blit(GUI, getGuiLeft() + 87 - (expanded ? 13 : 0), getGuiTop() + 37, 202 + xOffset, honeyLevel * 13, 13, 13);

        // draw bee cage
        if (simulated && this.menu.blockEntity.inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).isEmpty()) {
            guiGraphics.blit(GUI, getGuiLeft() + 87 - 13, getGuiTop() + 53, 202 + xOffset, 78, 14, 16);
        }

        // Bees
        int i = 0;
        for (BeehiveBlockEntity.BeeData inhabitant : this.menu.blockEntity.stored) {
            var occupant = inhabitant.occupant.entityData().copyTag();
            if (minecraft != null && positions.containsKey(i)) {
                String type = occupant.getString("type");
                if (type.isEmpty() || type.equals("minecraft:")) {
                    type = occupant.getString("id");
                }
                BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();
                if (beeIngredient == null) {
                    ProductiveBees.LOGGER.info("render bee in hive " + positions.get(i) + " " + type + " " + occupant);
                }

                if (beeIngredient != null) {
                    BeeRenderer.render(guiGraphics, getGuiLeft() + positions.get(i).get(0) - (expanded ? 13 : 0), getGuiTop() + positions.get(i).get(1), beeIngredient, minecraft);
                }
            }
            i++;
        }
    }
}
