package cy.jdkdigital.productivebees.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.item.JarBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public class JarBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ConcurrentHashMap<String, Entity> beeEntities = new ConcurrentHashMap<>();

    public JarBlockItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int packedLightIn, int packedUV) {
        Item item = itemStack.getItem();

        if (item instanceof JarBlockItem jarBlockItem) {
            String beeTypeOrEntityType = null;
            String entityType = null;

            // Very hacky
            if (itemStack.getTag() != null) {
                ListTag listTag = itemStack.getTag().getCompound("BlockEntityTag").getCompound("inv").getList("Items", 10);
                if (listTag.size() == 1) {
                    if (listTag.getCompound(0).getCompound("tag").contains("entity")) {
                        entityType = listTag.getCompound(0).getCompound("tag").getString("entity");
                        if (entityType.equals("productivebees:configurable_bee")) {
                            beeTypeOrEntityType = listTag.getCompound(0).getCompound("tag").getString("type");
                        } else {
                            beeTypeOrEntityType = listTag.getCompound(0).getCompound("tag").getString("entity");
                        }
                    }
                }
            }

            if (beeTypeOrEntityType != null) {
                if (!beeEntities.containsKey(beeTypeOrEntityType) && entityType != null) {
                    EntityType<?> type = EntityType.byString(entityType).orElse(null);
                    if (type != null && Minecraft.getInstance().level != null) {
                        Entity beeEntity = type.create(Minecraft.getInstance().level);
                        if (beeEntity != null) {
                            if (beeEntity instanceof ConfigurableBee configurableBee) {
                                configurableBee.setBeeType(beeTypeOrEntityType);
                            }
                            beeEntities.put(beeTypeOrEntityType, beeEntity);
                        }
                    }
                }

                Entity beeEntity = beeEntities.getOrDefault(beeTypeOrEntityType, null);
                if (beeEntity != null) {
                    renderBee(beeEntity, matrixStack, transformType);
                }
            }
            renderJar(matrixStack, jarBlockItem, itemStack, packedLightIn, packedUV, transformType);
        }
    }

    public static void renderBee(Entity bee, PoseStack matrixStack, ItemDisplayContext pTransformType) {
        float angle = bee.tickCount % 360;

        float f = 0.47F;
        float f1 = Math.max(bee.getBbWidth(), bee.getBbHeight());
        if ((double) f1 > 1.0D) {
            f /= f1;
        }

        matrixStack.pushPose();

        matrixStack.translate(0.5f, 0.4f, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(angle));
        matrixStack.translate(0.0f, -0.2f, 0.0f);
        matrixStack.scale(f, f, f);
        if (pTransformType.equals(ItemDisplayContext.GUI)) {
            matrixStack.scale(1.2f, 1.2f, 1.2f);
        }

        EntityRenderDispatcher entityRendererDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        entityRendererDispatcher.setRenderShadow(false);
        entityRendererDispatcher.render(bee, 0, 0, 0, 0, 0, matrixStack, buffer, 15728880);
        buffer.endBatch();

        matrixStack.popPose();
    }

    public static void renderJar(PoseStack matrixStack, JarBlockItem jarBlockItem, ItemStack itemStack, int packedLight, int packedOverlay, ItemDisplayContext transformType) {
        matrixStack.pushPose();
        
        if (transformType.equals(ItemDisplayContext.GUI)) {
            float x = -0.5f;
            float y = -0.45f;
            float z = -0.5f;
            matrixStack.translate(1, 1, 1);
            matrixStack.translate(x, y, z);
            matrixStack.mulPose(Axis.XP.rotationDegrees(20));
            matrixStack.mulPose(Axis.YP.rotationDegrees(120));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(0));
            matrixStack.translate(x, y, z);
        }

        BakedModel bakedModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(jarBlockItem.getBlock().defaultBlockState());
        RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, false);
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(buffer, renderType, true, itemStack.hasFoil());
        Minecraft.getInstance().getItemRenderer().renderModelLists(bakedModel, itemStack, packedLight, packedOverlay, matrixStack, vertexConsumer);
        buffer.endBatch();

        matrixStack.popPose();
    }
}
