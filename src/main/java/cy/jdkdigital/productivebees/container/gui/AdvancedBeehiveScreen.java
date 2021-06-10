package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.common.tileentity.DragonEggHiveTileEntity;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedBeehiveScreen extends ContainerScreen<AdvancedBeehiveContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
    private static final ResourceLocation GUI_TEXTURE_EXPANDED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");

    public AdvancedBeehiveScreen(AdvancedBeehiveContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        boolean expanded = this.menu.tileEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;

        this.font.draw(matrixStack, this.title, expanded ? -5f : 8.0F, 6.0F, 4210752);
        this.font.draw(matrixStack, this.inventory.getName(), expanded ? -5f : 8.0F, (float) (this.getYSize() - 96 + 2), 4210752);

        assert minecraft != null;
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        this.menu.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bee Tooltips
            int j = 0;
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                CompoundNBT nbt = inhabitant.nbt;

                BeeEntity bee = null;
                String type = inhabitant.nbt.getString("type");
                if (type.isEmpty()) {
                    type = inhabitant.nbt.getString("id");
                }
                BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();
                if (beeIngredient != null) {
                    bee = beeIngredient.getCachedEntity(minecraft.level);
                }

                if (bee != null && bee.getEncodeId() != null) {
                    if (bee instanceof ConfigurableBeeEntity && nbt.contains("type")) {
                        ((ConfigurableBeeEntity) bee).setBeeType(nbt.getString("type"));
                    }

                    if (positions.containsKey(j) && isHovering(positions.get(j).get(0) - (expanded ? 13 : 0), positions.get(j).get(1), 16, 16, mouseX, mouseY)) {
                        CompoundNBT tag = inhabitant.nbt.copy();
                        List<IReorderingProcessor> tooltipList = new ArrayList<IReorderingProcessor>();
                        tooltipList.add(bee.getName().getVisualOrderText());

                        if (Screen.hasShiftDown()) {
                            String modId = new ResourceLocation(bee.getEncodeId()).getNamespace();
                            if (modId.equals(ProductiveBees.MODID)) {
                                tag.putBoolean("isProductiveBee", true);
                            }

                            List<ITextComponent> list = BeeHelper.populateBeeInfoFromTag(tag, null);

                            for (ITextComponent textComponent : list) {
                                tooltipList.add(textComponent.getVisualOrderText());
                            }

                            if (!tag.getBoolean("isProductiveBee")) {
                                String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();
                                if (modId.equals("minecraft")) {
                                    modName = "Minecraft";
                                }
                                tooltipList.add(new StringTextComponent(modName).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.BLUE).getVisualOrderText());
                            }
                        } else {
                            tooltipList.add(new TranslationTextComponent("productivebees.information.hold_shift").withStyle(TextFormatting.WHITE).getVisualOrderText());
                        }
                        renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
                    }
                    j++;
                }
            }
        });
        // https://gist.github.com/gigaherz/f61fe604f38e27afad4d1553bc6cf311
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean expanded = this.menu.tileEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        int honeyLevel = this.menu.tileEntity.getBlockState().getValue(BeehiveBlock.HONEY_LEVEL);

        assert minecraft != null;
        minecraft.textureManager.bind(expanded ? GUI_TEXTURE_EXPANDED : GUI_TEXTURE);

        // Draw main screen
        blit(matrixStack, getGuiLeft() - (expanded ? 13 : 0), getGuiTop(), 0, 0, this.getXSize() + (expanded ? 26 : 0), this.getYSize());
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        // Draw honey level
        int xOffset = this.menu.tileEntity instanceof DragonEggHiveTileEntity ? 13 : 0;
        blit(matrixStack, getGuiLeft() + 87 - (expanded ? 13 : 0), getGuiTop() + 37, 202 + xOffset, honeyLevel * 13, 13, 13);

        this.menu.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bees
            int i = 0;
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                if (minecraft.player != null && positions.containsKey(i)) {
                    String type = inhabitant.nbt.getString("type");
                    if (type.isEmpty()) {
                        type = inhabitant.nbt.getString("id");
                    }
                    BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();

                    if (beeIngredient != null) {
                        BeeRenderer.render(matrixStack, getGuiLeft() + positions.get(i).get(0) - (expanded ? 13 : 0), getGuiTop() + positions.get(i).get(1), beeIngredient, minecraft);
                    }
                }
                i++;
            }
        });
    }
}
