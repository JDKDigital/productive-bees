package cy.jdkdigital.productivebees.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class AdvancedBeehiveScreen extends ContainerScreen<AdvancedBeehiveContainer> {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive.png");
	private static final ResourceLocation GUI_TEXTURE_EXPANDED = new ResourceLocation(ProductiveBees.MODID, "textures/gui/container/advanced_beehive_expanded.png");

	private static final HashMap<String, Integer> beeTextureLocations = new HashMap<String, Integer>() {{
		put("minecraft:bee", 0);
		put("productivebees:diamond_bee", 7);
		put("productivebees:emerald_bee", 14);
		put("productivebees:lapis_bee", 21);
		put("productivebees:redstone_bee", 28); // 249
	}};

	private static final Logger LOGGER = LogManager.getLogger();

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
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		boolean expanded = this.container.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED);
		int honeyLevel = this.container.tileEntity.getBlockState().get(AdvancedBeehiveAbstract.HONEY_LEVEL);
		this.minecraft.getTextureManager().bindTexture(expanded ? GUI_TEXTURE_EXPANDED : GUI_TEXTURE);

		// Draw main screen
		this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		// Draw honey level
		int l = 24 / 5 * honeyLevel;
		this.blit(this.guiLeft + 82, this.guiTop + 35, 176, 14, l + 1, 16);

		// Draw bees
		HashMap<Integer, ArrayList<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;
		ListNBT beeList = this.container.getBees();
		if (beeList.size() > 0) {
//			LOGGER.info(beeList);
			int i = 0;
			for (INBT inbt : beeList) {
				CompoundNBT inb = (CompoundNBT) inbt;
				String beeId = ((CompoundNBT) inb.get("EntityData")).getString("id");

				int beeTexureLocation = 0;
				if (beeTextureLocations.containsKey(beeId)) {
					beeTexureLocation = beeTextureLocations.get(beeId);
				}
//				LOGGER.info(beeId + ":" + beeTexureLocation);
				this.blit(beeTexureLocation, 249, positions.get(i).get(0), positions.get(i).get(1), 7, 7);
			}
		}
	}

}
