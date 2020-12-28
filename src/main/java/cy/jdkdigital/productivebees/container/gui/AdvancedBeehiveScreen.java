package cy.jdkdigital.productivebees.container.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.common.tileentity.DragonEggHiveTileEntity;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.integrations.resourcefulbees.ResourcefulBeesCompat;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

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

        this.container.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bee Tooltips
            int j = 0;
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                CompoundNBT nbt = inhabitant.nbt;

                ResourceLocation rLoc = new ResourceLocation(nbt.getString("id"));
                BeeEntity bee = null;
                if (rLoc.getNamespace().equals(ResourcefulBeesCompat.MODID)) {
                    CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(rLoc.getPath().replace("_bee", ""));
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
                    bee = (BeeEntity) entityType.create(this.container.tileEntity.getWorld());
                } else {
                    BeeIngredient ingredient = BeeIngredientFactory.getIngredient(nbt.getString("id")).get();
                    if (ingredient != null) {
                        bee = ingredient.getBeeEntity().create(this.container.tileEntity.getWorld());
                    } else {
                        ProductiveBees.LOGGER.warn("Bee ingredient not found for " + nbt.getString("id"));
                    }
                }

                if (bee != null) {
                    if (bee instanceof ConfigurableBeeEntity && nbt.contains("type")) {
                        ((ConfigurableBeeEntity) bee).setBeeType(nbt.getString("type"));
                    }

                    if (positions.containsKey(j) && isPointInRegion(positions.get(j).get(0) - (expanded ? 13 : 0), positions.get(j).get(1), 16, 16, mouseX, mouseY)) {
                        BeeEntity finalBee = bee;
                        List<IReorderingProcessor> tooltipList = new ArrayList<IReorderingProcessor>()
                        {{
                            add(finalBee.getName().func_241878_f());
                        }};

                        String modId = new ResourceLocation(bee.getEntityString()).getNamespace();
                        String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

                        if (modId.equals("minecraft")) {
                            modName = "Minecraft";
                        }
                        tooltipList.add(new StringTextComponent(modName).mergeStyle(TextFormatting.ITALIC).mergeStyle(TextFormatting.BLUE).func_241878_f());
                        renderTooltip(matrixStack, tooltipList, mouseX - guiLeft, mouseY - guiTop);
                    }
                    j++;
                }
            }
        });
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
        blit(matrixStack, getGuiLeft() - (expanded ? 13 : 0), getGuiTop(), 0, 0, this.xSize + (expanded ? 26 : 0), this.ySize);
        HashMap<Integer, List<Integer>> positions = expanded ? AdvancedBeehiveContainer.BEE_POSITIONS_EXPANDED : AdvancedBeehiveContainer.BEE_POSITIONS;

        // Draw honey level
        int yOffset = this.container.tileEntity instanceof DragonEggHiveTileEntity ? 17 : 0;
        int progress = honeyLevel == 0 ? 0 : 27 / 5 * honeyLevel;
        blit(matrixStack, getGuiLeft() + 82 - (expanded ? 13 : 0), getGuiTop() + 35, 176 + (expanded ? 26 : 0), 14 + yOffset, progress, 16);

        this.container.tileEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
            // Bees
            int i = 0;
            for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : inhabitantHandler.getInhabitants()) {
                CompoundNBT nbt = inhabitant.nbt;

                ResourceLocation rLoc = new ResourceLocation(nbt.getString("id"));
                BeeEntity bee = null;
                float scaledSize = 28;
                if (this.container.tileEntity.getWorld() != null) {
                    if (rLoc.getNamespace().equals(ResourcefulBeesCompat.MODID)) {
                        CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(rLoc.getPath().replace("_bee", ""));
                        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
                        bee = (BeeEntity) entityType.create(this.container.tileEntity.getWorld());
                        scaledSize = 20 / beeData.getSizeModifier();
                    }
                    else {
                        BeeIngredient ingredient = BeeIngredientFactory.getIngredient(nbt.getString("id")).get();
                        if (ingredient != null) {
                            bee = ingredient.getBeeEntity().create(this.container.tileEntity.getWorld());
                        }
                    }
                }

                if (minecraft.player != null && bee != null && positions.containsKey(i)) {
                    if (bee instanceof ConfigurableBeeEntity && nbt.contains("type")) {
                        ((ConfigurableBeeEntity) bee).setBeeType(nbt.getString("type"));
                    }

                    bee.ticksExisted = minecraft.player.ticksExisted;
                    bee.renderYawOffset = -20;

                    matrixStack.push();
                    matrixStack.translate(7 + getGuiLeft() + positions.get(i).get(0) - (expanded ? 13 : 0), 17 + getGuiTop() + positions.get(i).get(1), 1.5D);
                    matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                    matrixStack.translate(0.0F, -0.2F, 1);

                    matrixStack.scale(scaledSize, scaledSize, 32);

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
