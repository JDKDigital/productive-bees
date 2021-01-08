package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.common.tileentity.DragonEggHiveTileEntity;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;

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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;

        this.font.drawString(this.title.getFormattedText(), expanded ? -5f : 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), expanded ? -5f : 8.0F, (float) (this.ySize - 96 + 2), 4210752);

        // Draw bees here
        assert minecraft != null;
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        this.container.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bee Tooltips
            int j = 0;
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                BeeEntity bee = (BeeEntity) EntityType.loadEntityAndExecute(inhabitant.nbt, this.container.tileEntity.getWorld(), (spawnedEntity) -> spawnedEntity);

                if (bee != null && positions.containsKey(j) && isPointInRegion(positions.get(j).get(0) - (expanded ? 13 : 0), positions.get(j).get(1), 16, 16, mouseX, mouseY)) {
                    List<String> tooltipList = new ArrayList<String>()
                    {{
                        add(bee.getName().getFormattedText());
                    }};

                    String modId = new ResourceLocation(bee.getEntityString()).getNamespace();
                    String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

                    if (modId.equals("minecraft")) {
                        modName = "Minecraft";
                    }
                    tooltipList.add(new StringTextComponent(modName).applyTextStyle(TextFormatting.ITALIC).applyTextStyle(TextFormatting.BLUE).getFormattedText());
                    renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
                }
                j++;
            }
        });
        // https://gist.github.com/gigaherz/f61fe604f38e27afad4d1553bc6cf311
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        int honeyLevel = this.container.tileEntity.getBlockState().get(BeehiveBlock.HONEY_LEVEL);

        assert minecraft != null;
        minecraft.getTextureManager().bindTexture(expanded ? GUI_TEXTURE_EXPANDED : GUI_TEXTURE);
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        // Draw main screen
        this.blit(getGuiLeft() - (expanded ? 13 : 0), getGuiTop(), 0, 0, this.xSize + (expanded ? 26 : 0), this.ySize);

        // Draw honey level
        int yOffset = this.container.tileEntity instanceof DragonEggHiveTileEntity ? 17 : 0;
        int progress = honeyLevel == 0 ? 0 : 27 / 5 * honeyLevel;
        this.blit(getGuiLeft() + 82 - (expanded ? 13 : 0), getGuiTop() + 35, 176 + (expanded ? 26 : 0), 94 + yOffset, progress, 16);

        this.container.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bees
            int i = 0;
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                BeeEntity bee = (BeeEntity) EntityType.loadEntityAndExecute(inhabitant.nbt, this.container.tileEntity.getWorld(), (spawnedEntity) -> spawnedEntity);
                if (minecraft.player != null && bee != null && positions.containsKey(i)) {
                    bee.ticksExisted = minecraft.player.ticksExisted;
                    bee.renderYawOffset = -20;

                    MatrixStack matrixStack = new MatrixStack();
                    matrixStack.push();
                    matrixStack.translate(7 + getGuiLeft() + positions.get(i).get(0) - (expanded ? 13 : 0), 17 + getGuiTop() + positions.get(i).get(1), 1.5D);
                    matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                    matrixStack.translate(0.0F, -0.2F, 1);
                    matrixStack.scale(28, 28, 32);

                    EntityRendererManager entityrenderermanager = minecraft.getRenderManager();
                    IRenderTypeBuffer.Impl buffer = minecraft.getRenderTypeBuffers().getBufferSource();
                    entityrenderermanager.renderEntityStatic(bee, 0, 0, 0.0D, minecraft.getRenderPartialTicks(), 1, matrixStack, buffer, 15728880);
                    buffer.finish();

                    matrixStack.pop();
                }

                i++;
            }
        });
    }
}
