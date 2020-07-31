package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.tileentity.DragonEggHiveTileEntity;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
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

    private static HashMap<String, ResourceLocation> beeTextureLocations = new HashMap<>();
    private static HashMap<String, ITextComponent> stringCache = new HashMap<>();

    public AdvancedBeehiveScreen(AdvancedBeehiveContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.func_230459_a_(matrixStack, mouseX, mouseY);
        assert minecraft != null;

        this.font.func_238422_b_(matrixStack, this.title, 8.0F, 6.0F, 4210752);
        this.font.func_238422_b_(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);

        // Draw bees here
        boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;

        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        if (this.container.tileEntity.inhabitantList.size() > 0) {
            // Bee icons
            int i = 0;
            for (String beeId : this.container.tileEntity.inhabitantList) {
                if (positions.get(i) == null || beeId.isEmpty()) {
                    continue;
                }
                ResourceLocation beeTexture = getBeeTexture(beeId, this.container.tileEntity.getWorld());
                minecraft.textureManager.bindTexture(beeTexture);
                blit(matrixStack, positions.get(i).get(0) + guiLeft, positions.get(i).get(1) + guiTop, 20, 20, 14, 14, 128, 128);

                minecraft.textureManager.bindTexture(GUI_TEXTURE_BEE_OVERLAY);
                blit(matrixStack, positions.get(i).get(0) + guiLeft, positions.get(i).get(1) + guiTop, 0, 0, 14, 14, 14, 14);

                i++;
            }
            // Bee Tooltips
            int j = 0;
            for (String beeId : this.container.tileEntity.inhabitantList) {
                if (isPointInRegion(positions.get(j).get(0), positions.get(j).get(1), 16, 16, mouseX, mouseY) && stringCache.containsKey(beeId)) {
                    List<ITextComponent> tooltipList = new ArrayList<ITextComponent>()
                    {{
                        add(stringCache.get(beeId));
                    }};
                    tooltipList.add(stringCache.get(beeId + "_mod"));
                    renderTooltip(matrixStack, tooltipList, mouseX, mouseY);
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
        blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        // Draw honey level
        int yOffset = this.container.tileEntity instanceof DragonEggHiveTileEntity ? 17 : 0;
        int progress = honeyLevel == 0 ? 0 : 27 / 5 * honeyLevel;
        blit(matrixStack, this.guiLeft + 82, this.guiTop + 35, 176, 14 + yOffset, progress, 16);
    }

    public static ResourceLocation getBeeTexture(@Nonnull ResourceLocation res, World world) {
        String beeId = res.toString();
        if (beeTextureLocations.get(beeId) != null) {
            return beeTextureLocations.get(beeId);
        }
        Entity bee = ForgeRegistries.ENTITIES.getValue(res).create(world);

        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        EntityRenderer renderer = manager.getRenderer(bee);

        ResourceLocation resource = renderer.getEntityTexture(bee);
        beeTextureLocations.put(beeId, resource);
        stringCache.put(beeId, bee.getDisplayName());

        String modId = resource.getNamespace();
        String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

        if (modId.equals("minecraft")) {
            modName = "Minecraft";
        }

        stringCache.put(beeId + "_mod", new StringTextComponent(modName));

        return beeTextureLocations.get(beeId);
    }

    public static ResourceLocation getBeeTexture(String beeId, World world) {
        ResourceLocation resLocation = new ResourceLocation(beeId);

        return getBeeTexture(resLocation, world);
    }
}
