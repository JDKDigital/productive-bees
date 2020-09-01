package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.tileentity.DragonEggHiveTileEntity;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedBeehiveScreen extends ContainerScreen<AdvancedBeehiveContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
    private static final ResourceLocation GUI_TEXTURE_EXPANDED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");
    private static final ResourceLocation GUI_TEXTURE_BEE_OVERLAY = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_bee_overlay.png");

    private static HashMap<String, Entity> beeCache = new HashMap<>();
    private static HashMap<String, ITextComponent> stringCache = new HashMap<>();

    public AdvancedBeehiveScreen(AdvancedBeehiveContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        assert minecraft != null;

        this.font.func_243248_b(matrixStack, this.title, 8.0F, 6.0F, 4210752);
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);

        // Draw bees here
        boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;

        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        if (this.container.tileEntity.inhabitantList.size() > 0) {
            // Bee Tooltips
            int j = 0;
            for (String beeId : this.container.tileEntity.inhabitantList) {
                if (isPointInRegion(positions.get(j).get(0), positions.get(j).get(1), 16, 16, mouseX, mouseY) && stringCache.containsKey(beeId)) {
                    List<IReorderingProcessor> tooltipList = new ArrayList<IReorderingProcessor>()
                    {{
                        add(stringCache.get(beeId).func_241878_f());
                    }};
                    tooltipList.add(stringCache.get(beeId + "_mod").func_241878_f());
                    renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
                }
                j++;
            }
        }
        // https://gist.github.com/gigaherz/f61fe604f38e27afad4d1553bc6cf311
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
        int honeyLevel = this.container.tileEntity.getBlockState().get(BeehiveBlock.HONEY_LEVEL);

        assert minecraft != null;
        minecraft.textureManager.bindTexture(expanded ? GUI_TEXTURE_EXPANDED : GUI_TEXTURE);

        // Draw main screen
        blit(matrixStack, getGuiLeft(), getGuiTop(), 0, 0, this.xSize, this.ySize);
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        // Draw honey level
        int yOffset = this.container.tileEntity instanceof DragonEggHiveTileEntity ? 17 : 0;
        int progress = honeyLevel == 0 ? 0 : 27 / 5 * honeyLevel;
        blit(matrixStack, getGuiLeft() + 82, getGuiTop() + 35, 176, 14 + yOffset, progress, 16);

        if (this.container.tileEntity.inhabitantList.size() > 0) {
            // Bees
            int i = 0;
            for (String beeId : this.container.tileEntity.inhabitantList) {
                if (beeId.isEmpty()) {
                    continue;
                }

                BeeEntity bee = (BeeEntity) getBee(beeId, this.container.tileEntity.getWorld());

                if (minecraft.player != null && bee != null) {
                    bee.ticksExisted = minecraft.player.ticksExisted;
                    bee.renderYawOffset = -15;

                    matrixStack.push();
                    matrixStack.translate(7 + getGuiLeft() + positions.get(i).get(0), 17 + getGuiTop() + positions.get(i).get(1), 1.5D);
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
        }
    }

    public static Entity getBee(@Nonnull ResourceLocation res, World world) {
        String beeId = res.toString();
        if (beeCache.get(beeId) != null) {
            return beeCache.get(beeId);
        }
        Entity bee = ForgeRegistries.ENTITIES.getValue(res).create(world);
        beeCache.put(beeId, bee);

        String modId = res.getNamespace();
        String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

        if (modId.equals("minecraft")) {
            modName = "Minecraft";
        }

        stringCache.put(beeId, bee.getDisplayName());
        stringCache.put(beeId + "_mod", new StringTextComponent(modName).mergeStyle(TextFormatting.BLUE).mergeStyle(TextFormatting.ITALIC));

        return bee;
    }

    public static Entity getBee(String beeId, World world) {
        ResourceLocation resLocation = new ResourceLocation(beeId);

        return getBee(resLocation, world);
    }
}
