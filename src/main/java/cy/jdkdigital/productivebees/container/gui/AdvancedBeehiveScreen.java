package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.DragonEggHiveBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedBeehiveScreen extends AbstractContainerScreen<AdvancedBeehiveContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
    private static final ResourceLocation GUI_TEXTURE_EXPANDED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");
    private static final ResourceLocation GUI_TEXTURE_SIMULATED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_simulated.png");

    public AdvancedBeehiveScreen(AdvancedBeehiveContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean expanded = this.menu.tileEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        boolean simulated = expanded && this.menu.tileEntity.isSim();

        guiGraphics.drawString(font, this.title, expanded ? -5 : 8, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, expanded ? -5 : 8, (this.getYSize() - 96 + 2), 4210752, false);

        assert minecraft != null;
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;
        List<FormattedCharSequence> tooltipList = new ArrayList<FormattedCharSequence>();

        // Cage slot tooltip
        if (simulated && isHovering(86 - 13, 53, 16, 16, mouseX, mouseY) && this.menu.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).map(iItemHandler -> iItemHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).isEmpty()).orElse(false)) {
            tooltipList.add(Component.translatable("productivebees.advanced_hive.tooltip.bee_cage").getVisualOrderText());
        }

        this.menu.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bee Tooltips
            int j = 0;
            for (AdvancedBeehiveBlockEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                CompoundTag nbt = inhabitant.nbt;

                Entity bee = null;
                String type = inhabitant.nbt.getString("type");
                if (type.isEmpty()) {
                    type = inhabitant.nbt.getString("id");
                }
                BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();
                if (beeIngredient != null) {
                    bee = beeIngredient.getCachedEntity(minecraft.level);
                }

                if (bee != null && bee.getEncodeId() != null) {
                    if (bee instanceof ConfigurableBee && nbt.contains("type")) {
                        ((ConfigurableBee) bee).setBeeType(nbt.getString("type"));
                    }

                    if (positions.containsKey(j) && isHovering(positions.get(j).get(0) - (expanded ? 13 : 0), positions.get(j).get(1), 16, 16, mouseX, mouseY)) {
                        CompoundTag tag = inhabitant.nbt.copy();
                        tooltipList.add(bee.getName().getVisualOrderText());

                        if (Screen.hasShiftDown()) {
                            String modId = new ResourceLocation(bee.getEncodeId()).getNamespace();
                            if (modId.equals(ProductiveBees.MODID)) {
                                tag.putBoolean("isProductiveBee", true);
                            }

                            String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();
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
                    j++;
                }
            }
        });
        guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        boolean expanded = this.menu.tileEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        boolean simulated = expanded && this.menu.tileEntity.isSim();
        var GUI = expanded ? (simulated ? GUI_TEXTURE_SIMULATED : GUI_TEXTURE_EXPANDED) : GUI_TEXTURE;

        int honeyLevel = this.menu.tileEntity.getBlockState().getValue(BeehiveBlock.HONEY_LEVEL);
        // Draw main screen
        guiGraphics.blit(GUI, getGuiLeft() - (expanded ? 13 : 0), getGuiTop(), 0, 0, this.getXSize() + (expanded ? 26 : 0), this.getYSize());
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        // Draw honey level
        int xOffset = this.menu.tileEntity instanceof DragonEggHiveBlockEntity ? 13 : 0;
        guiGraphics.blit(GUI, getGuiLeft() + 87 - (expanded ? 13 : 0), getGuiTop() + 37, 202 + xOffset, honeyLevel * 13, 13, 13);

        // draw bee cage
        if (simulated && this.menu.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).map(iItemHandler -> iItemHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).isEmpty()).orElse(false)) {
            guiGraphics.blit(GUI, getGuiLeft() + 87 - 13, getGuiTop() + 53, 202 + xOffset, 78, 14, 16);
        }

        this.menu.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bees
            int i = 0;
            for (AdvancedBeehiveBlockEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                if (minecraft != null && positions.containsKey(i)) {
                    String type = inhabitant.nbt.getString("type");
                    if (type.isEmpty()) {
                        type = inhabitant.nbt.getString("id");
                    }
                    BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();

                    if (beeIngredient != null) {
                        BeeRenderer.render(guiGraphics, getGuiLeft() + positions.get(i).get(0) - (expanded ? 13 : 0), getGuiTop() + positions.get(i).get(1), beeIngredient, minecraft);
                    }
                }
                i++;
            }
        });
    }
}
