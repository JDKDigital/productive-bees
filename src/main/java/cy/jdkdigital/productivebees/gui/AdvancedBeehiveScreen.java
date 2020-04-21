package cy.jdkdigital.productivebees.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class AdvancedBeehiveScreen extends ContainerScreen<AdvancedBeehiveContainer> {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
	private static final ResourceLocation GUI_TEXTURE_EXPANDED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");
	private static final ResourceLocation GUI_TEXTURE_BEE_OVERLAY = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_bee_overlay.png");

	private static final HashMap<String, ResourceLocation> beeTextureLocations = new HashMap<>();

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
				minecraft.getTextureManager().bindTexture(getBeeTexture(beeId, this.container.tileEntity.getWorld()));
				blit(positions.get(i).get(0), positions.get(i).get(1), 20, 20, 14, 14, 128, 128);

				minecraft.getTextureManager().bindTexture(GUI_TEXTURE_BEE_OVERLAY);
				blit(positions.get(i).get(0), positions.get(i).get(1), 0, 0, 14, 14, 14, 14);

				i++;
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
		this.blit(this.guiLeft + 82, this.guiTop + 35, 176, 14, l + 1, 16);
	}

	private ResourceLocation getBeeTexture(String beeId, World world) {
		if (beeTextureLocations.get(beeId) != null) {
			return beeTextureLocations.get(beeId);
		}
		ResourceLocation resLocation = new ResourceLocation(beeId);

		Entity bee = ForgeRegistries.ENTITIES.getValue(resLocation).create(world);

		EntityRendererManager manager = minecraft.getRenderManager();
		EntityRenderer renderer = manager.getRenderer(bee);

		beeTextureLocations.put(beeId, renderer.getEntityTexture(bee));
		return beeTextureLocations.get(beeId);
	}
}
