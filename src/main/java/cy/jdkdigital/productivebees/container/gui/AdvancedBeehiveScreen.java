package cy.jdkdigital.productivebees.container.gui;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.entity.DragonEggHiveBlockEntity;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import org.lwjgl.glfw.GLFW;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedBeehiveScreen extends AbstractUpgradeableContainerScreen<AdvancedBeehiveContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
    private static final Identifier GUI_TEXTURE_EXPANDED = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");
    private static final Identifier GUI_TEXTURE_SIMULATED = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_simulated.png");
    private final boolean expanded;

    public AdvancedBeehiveScreen(AdvancedBeehiveContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.expanded = this.menu.getBlockEntity().getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        if (!this.expanded) {
            this.imageWidth = 179;
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        boolean simulated = this.expanded && this.menu.getBlockEntity().isSim();
        assert minecraft != null;
        HashMap<Integer, List<Integer>> positions = this.expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        List<FormattedCharSequence> tooltipList = new ArrayList<>();

        if (simulated && isHovering(86 - 13, 53, 16, 16, mouseX, mouseY)
                && this.menu.getBlockEntity().inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).isEmpty()) {
            tooltipList.add(Component.translatable("productivebees.advanced_hive.tooltip.bee_cage").getVisualOrderText());
        }

        int j = 0;
        for (BeehiveBlockEntity.BeeData inhabitant : this.menu.getBlockEntity().stored) {
            CompoundTag occupant = inhabitant.occupant.entityData().copyTagWithoutId();

            Entity bee = null;
            BeeIngredient beeIngredient = resolveBeeIngredient(inhabitant);
            if (beeIngredient != null) {
                bee = beeIngredient.getCachedEntity(minecraft.level);
            }

            if (bee != null && bee.getEncodeId() != null) {
                if (bee instanceof ConfigurableBee configurableBee && occupant.contains("type")) {
                    configurableBee.setBeeType(occupant.getString("type").orElse(""));
                }

                if (positions.containsKey(j) && isHovering(positions.get(j).get(0), positions.get(j).get(1), 16, 16, mouseX, mouseY)) {
                    CompoundTag tag = occupant.copy();
                    tooltipList.add(bee.getName().getVisualOrderText());

                    var window = Minecraft.getInstance().getWindow();
                    boolean shiftDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
                    if (shiftDown) {
                        String modId = Identifier.parse(bee.getEncodeId()).getNamespace();
                        if (modId.equals(ProductiveBees.MODID)) {
                            tag.putBoolean("isProductiveBee", true);
                        }

                        String modName = ModList.get().getModContainerById(modId).get().getClass().getSimpleName();
                        if (modId.equals("minecraft")) {
                            modName = "Minecraft";
                        }
                        tag.putString("mod", modName);

                        List<Component> list = BeeHelper.populateBeeInfoFromTag(tag, (List<Component>) null);
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

        if (!tooltipList.isEmpty()) {
            graphics.setTooltipForNextFrame(tooltipList, mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        boolean simulated = this.expanded && this.menu.getBlockEntity().isSim();
        Identifier texture = this.expanded ? (simulated ? GUI_TEXTURE_SIMULATED : GUI_TEXTURE_EXPANDED) : GUI_TEXTURE;

        int honeyLevel = this.menu.getBlockEntity().getBlockState().getValue(BeehiveBlock.HONEY_LEVEL);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        HashMap<Integer, List<Integer>> positions = this.expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        int xOffset = this.menu.getBlockEntity() instanceof DragonEggHiveBlockEntity ? 13 : 0;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 87, this.topPos + 37, 202.0F + xOffset, honeyLevel * 13.0F, 13, 13, 256, 256);

        if (simulated && this.menu.getBlockEntity().inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).isEmpty()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 87, this.topPos + 53, 202.0F + xOffset, 78.0F, 14, 16, 256, 256);
        }

        int i = 0;
        for (BeehiveBlockEntity.BeeData inhabitant : this.menu.getBlockEntity().stored) {
            if (minecraft != null && positions.containsKey(i)) {
                BeeIngredient beeIngredient = resolveBeeIngredient(inhabitant);
                if (beeIngredient == null) {
                    ProductiveBees.LOGGER.info("render bee in hive " + positions.get(i) + " " + inhabitant.occupant.entityData());
                } else {
                    BeeRenderer.render(graphics, this.leftPos + positions.get(i).get(0), this.topPos + positions.get(i).get(1), beeIngredient, minecraft);
                }
            }
            i++;
        }
    }

    private static BeeIngredient resolveBeeIngredient(BeehiveBlockEntity.BeeData inhabitant) {
        var entityData = inhabitant.occupant.entityData();
        CompoundTag tag = entityData.copyTagWithoutId();
        String type = tag.getString("type").orElse("");
        if (!type.isEmpty() && !type.equals("minecraft:")) {
            return BeeIngredientFactory.getIngredient(type).get();
        }
        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityData.type());
        return BeeIngredientFactory.getIngredient(entityId.toString()).get();
    }

    protected boolean insideUpgradeSlots(double mouseX, double mouseY) {
        return this.expanded && isHovering(this.imageWidth - 24, 8, 18, 72, mouseX, mouseY);
    }
}
