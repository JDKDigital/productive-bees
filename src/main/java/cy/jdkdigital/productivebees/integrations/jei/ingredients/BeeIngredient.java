package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.integrations.resourcefulbees.ResourcefulBeesCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeIngredient
{
    private static Map<BeeIngredient, BeeEntity> cache = new HashMap<>();

    private EntityType<? extends BeeEntity> bee;
    private ResourceLocation beeType;
    private int renderType = 0;
    private boolean configurable = false;

    public BeeIngredient(EntityType<? extends BeeEntity> bee, int renderType) {
        this.bee = bee;
        this.renderType = renderType;
    }

    public BeeIngredient(EntityType<? extends BeeEntity> bee, ResourceLocation beeType, int renderType) {
        this(bee, renderType);
        this.beeType = beeType;
    }

    public BeeIngredient(EntityType<? extends BeeEntity> bee, ResourceLocation beeType, int renderType, boolean isConfigurable) {
        this(bee, renderType);
        this.beeType = beeType;
        this.configurable = isConfigurable;
    }

    public EntityType<? extends BeeEntity> getBeeEntity() {
        return bee;
    }

    public BeeEntity getCachedEntity(World world) {
        if (!cache.containsKey(this)) {
            cache.put(this, getBeeEntity().create(world));
        }
        return cache.get(this);
    }

    /**
     * productivebees:osmium, prouctivebees:leafcutter_bee
     */
    public ResourceLocation getBeeType() {
        return beeType != null ? beeType : bee.getRegistryName();
    }

    public int getRenderType() {
        return renderType;
    }

    public static BeeIngredient read(PacketBuffer buffer) {
        String beeName = buffer.readString();

        return new BeeIngredient((EntityType<? extends BeeEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeName)), buffer.readResourceLocation(), buffer.readInt(), buffer.readBoolean());
    }

    public final void write(PacketBuffer buffer) {
        buffer.writeString("" + bee.getRegistryName());
        buffer.writeResourceLocation(getBeeType());
        buffer.writeInt(renderType);
        buffer.writeBoolean(configurable);
    }

    @Override
    public String toString() {
        return "BeeIngredient{" +
                "bee=" + bee +
                ", beeType=" + beeType +
                ", renderType=" + renderType +
                '}';
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public static void render(MatrixStack matrixStack, int xPosition, int yPosition, BeeIngredient beeIngredient, Minecraft minecraft) {
        if (ProductiveBeesConfig.CLIENT.renderBeeIngredientAsEntity.get()) {
            BeeEntity bee = beeIngredient.getCachedEntity(minecraft.world);

            if (minecraft.player != null && bee != null) {
                if (bee instanceof ConfigurableBeeEntity) {
                    ((ConfigurableBeeEntity) bee).setBeeType(beeIngredient.getBeeType().toString());
                }

                if (bee instanceof ProductiveBeeEntity) {
                    ((ProductiveBeeEntity) bee).setRenderStatic();
                }

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

    private static final Map<Integer, Map<String, Integer>> renderSettings = new HashMap<Integer, Map<String, Integer>>()
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
    private static void renderBeeFace(int xPosition, int yPosition, BeeIngredient beeIngredient, World world) {
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
