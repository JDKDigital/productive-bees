package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
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

public class AdvancedBeehiveScreen extends ContainerScreen<AdvancedBeehiveContainer> {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
	private static final ResourceLocation GUI_TEXTURE_EXPANDED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");
	private static final ResourceLocation GUI_TEXTURE_BEE_OVERLAY = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_bee_overlay.png");

	private static HashMap<String, ResourceLocation> beeTextureLocations = new HashMap<>();
	private static HashMap<String, ITextComponent> stringCache = new HashMap<>();

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
		this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);

		// Draw bees here
		assert minecraft != null;
		boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED);
		HashMap<Integer, ArrayList<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

		if (this.container.tileEntity.inhabitantList.size() > 0) {
			int i = 0;
			for (String beeId: this.container.tileEntity.inhabitantList) {
				if (positions.get(i) == null || beeId.isEmpty()) {
					continue;
				}
				ResourceLocation beeTexture = getBeeTexture(beeId, this.container.tileEntity.getWorld());
				minecraft.getTextureManager().bindTexture(beeTexture);
				blit(positions.get(i).get(0), positions.get(i).get(1), 20, 20, 14, 14, 128, 128);

				minecraft.getTextureManager().bindTexture(GUI_TEXTURE_BEE_OVERLAY);
				blit(positions.get(i).get(0), positions.get(i).get(1), 0, 0, 14, 14, 14, 14);

				i++;
			}
			int j = 0;
			for (String beeId: this.container.tileEntity.inhabitantList) {
				if (isPointInRegion(positions.get(j).get(0), positions.get(j).get(1), 16, 16, mouseX, mouseY) && stringCache.containsKey(beeId)) {
					List<String> tooltipList = new ArrayList<String>() {{add(stringCache.get(beeId).getFormattedText());}};
					tooltipList.add(stringCache.get(beeId + "_mod").applyTextStyle(TextFormatting.ITALIC).applyTextStyle(TextFormatting.BLUE).getFormattedText());
					renderTooltip(tooltipList, mouseX - guiLeft, mouseY - guiTop);
				}
				j++;
			}
		}
		// https://gist.github.com/gigaherz/f61fe604f38e27afad4d1553bc6cf311
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED);
		int honeyLevel = this.container.tileEntity.getBlockState().get(BeehiveBlock.HONEY_LEVEL);

		assert minecraft != null;
		minecraft.getTextureManager().bindTexture(expanded ? GUI_TEXTURE_EXPANDED : GUI_TEXTURE);

		// Draw main screen
		this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		// Draw honey level
		int l = 24 / 5 * honeyLevel;
		ProductiveBees.LOGGER.info("progress: " + l);
		this.blit(this.guiLeft + 82, this.guiTop + 35, 176, 14, l + 1, 16);
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
