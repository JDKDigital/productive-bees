package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeIngredientRenderer implements IIngredientRenderer<BeeIngredient>
{
    private final Map<Integer, Map<String, Integer>> renderSettings = new HashMap<Integer, Map<String, Integer>>()
    {{
        put(0, new HashMap<String, Integer>()
        {{
            put("scale", 128);
            put("iconX", 14);
            put("iconY", 14);
            put("iconU", 20);
            put("iconV", 20);
        }});
        put(1, new HashMap<String, Integer>()
        {{
            put("scale", 128);
            put("iconX", 12);
            put("iconY", 12);
            put("iconU", 20);
            put("iconV", 20);
        }});
    }};

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int xPosition, int yPosition, @Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world != null) {
            if (ProductiveBeesConfig.CLIENT.renderBeeIngredientAsEntity.get()) {
                BeeEntity bee = beeIngredient.getCachedEntity(minecraft.world);

                if (bee instanceof ConfigurableBeeEntity) {
                    ((ConfigurableBeeEntity) bee).setBeeType(beeIngredient.getBeeType().toString());
                }

                if (bee instanceof ProductiveBeeEntity) {
                    ((ProductiveBeeEntity) bee).setRenderStatic();
                }

                if (minecraft.player != null && bee != null) {
                    bee.ticksExisted = minecraft.player.ticksExisted;
                    bee.renderYawOffset = -20;

                    float scaledSize = 18;

                    matrixStack.push();
                    matrixStack.translate(7 + xPosition, 12 + yPosition, 1.5);
                    matrixStack.rotate(Vector3f.ZP.rotationDegrees(190.0F));
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(20.0F));
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0F));
                    matrixStack.translate(0.0F, -0.2F, 1);
                    matrixStack.scale(scaledSize, scaledSize, scaledSize);

                    EntityRendererManager entityrenderermanager = minecraft.getRenderManager();
                    IRenderTypeBuffer.Impl buffer = minecraft.getRenderTypeBuffers().getBufferSource();
                    entityrenderermanager.renderEntityStatic(bee, 0, 0, 0.0D, minecraft.getRenderPartialTicks(), 1, matrixStack, buffer, 15728880);
                    buffer.finish();
                    matrixStack.pop();
                }
            } else {
                renderBeeFace(xPosition, yPosition, beeIngredient, minecraft.world);
            }
        }
    }

    private void renderBeeFace(int xPosition, int yPosition, BeeIngredient beeIngredient, World world) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        ResourceLocation resLocation = getBeeTexture(beeIngredient, world);
        Minecraft.getInstance().getTextureManager().bindTexture(resLocation);

        Map<String, Integer> iconSettings = renderSettings.get(beeIngredient.getRenderType());
        float[] color = colorCache.get(beeIngredient.getBeeType().toString());

        float scale = (float) 1 / iconSettings.get("scale");
        int iconX = iconSettings.get("iconX");
        int iconY = iconSettings.get("iconY");
        int iconU = iconSettings.get("iconU");
        int iconV = iconSettings.get("iconV");

        if (color == null) {
            color = new float[] {1.0f, 1.0f, 1.0f};
        }
        RenderSystem.color4f(color[0], color[1], color[2], 1.0f);
        BufferBuilder renderBuffer = Tessellator.getInstance().getBuffer();

        renderBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderBuffer.pos(xPosition, yPosition + iconY, 0D).tex((iconU) * scale, (iconV + iconY) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
        renderBuffer.pos(xPosition + iconX, yPosition + iconY, 0D).tex((iconU + iconX) * scale, (iconV + iconY) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
        renderBuffer.pos(xPosition + iconX, yPosition, 0D).tex((iconU + iconX) * scale, (iconV) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
        renderBuffer.pos(xPosition, yPosition, 0D).tex((iconU) * scale, (iconV) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();

        Tessellator.getInstance().draw();

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltip(BeeIngredient beeIngredient, ITooltipFlag iTooltipFlag) {
        List<ITextComponent> list = new ArrayList<>();
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
        if (nbt != null) {
            list.add(new TranslationTextComponent("entity.productivebees.bee_configurable", nbt.getString("name")));
        } else {
            list.add(beeIngredient.getBeeEntity().getName());
        }
        list.add(new StringTextComponent(beeIngredient.getBeeType().toString()).mergeStyle(TextFormatting.DARK_GRAY));
        return list;
    }

    private static HashMap<String, ResourceLocation> beeTextureLocations = new HashMap<>();
    private static HashMap<String, float[]> colorCache = new HashMap<>();
    public static ResourceLocation getBeeTexture(@Nonnull BeeIngredient ingredient, World world) {
        String beeId = ingredient.getBeeType().toString();
        if (beeTextureLocations.get(beeId) != null) {
            return beeTextureLocations.get(beeId);
        }

        Entity bee = ingredient.getBeeEntity().create(world);
        if (bee instanceof ConfigurableBeeEntity) {
            ((ConfigurableBeeEntity) bee).setBeeType(ingredient.getBeeType().toString());
            colorCache.put(beeId, ((ConfigurableBeeEntity) bee).getColor(0).getComponents(null));
        }

        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        EntityRenderer renderer = manager.getRenderer(bee);

        ResourceLocation resource = renderer.getEntityTexture(bee);
        beeTextureLocations.put(beeId, resource);

        return beeTextureLocations.get(beeId);
    }
}
