package cy.jdkdigital.productivebees.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class JarBlockItemRenderer implements SpecialModelRenderer<JarBlockItemRenderer.BeeData>
{
    private static final ConcurrentHashMap<String, Entity> BEE_CACHE = new ConcurrentHashMap<>();

    public record BeeData(String beeTypeOrEntityType, String entityType) {
        public static final BeeData EMPTY = new BeeData("", "");

        public boolean isPresent() {
            return !beeTypeOrEntityType.isEmpty() && !entityType.isEmpty();
        }
    }

    @Override
    public @Nullable BeeData extractArgument(ItemStack stack) {
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container == null) {
            return null;
        }
        ItemStack cage = container.copyOne();
        if (cage.isEmpty()) {
            return null;
        }
        CustomData customData = cage.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        String entityType = customData.copyTag().getString("entity").orElse("");
        if (entityType.isEmpty()) {
            return null;
        }
        String beeTypeKey = entityType;
        if (entityType.equals(ProductiveBees.MODID + ":configurable_bee")) {
            String beeType = customData.copyTag().getString("type").orElse("");
            if (!beeType.isEmpty()) {
                beeTypeKey = beeType;
            }
        }
        return new BeeData(beeTypeKey, entityType);
    }

    @Override
    public void submit(@Nullable BeeData data, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        if (data == null || !data.isPresent()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Entity bee = BEE_CACHE.computeIfAbsent(data.beeTypeOrEntityType, key -> {
            EntityType<?> type = EntityType.byString(data.entityType).orElse(null);
            if (type == null) {
                return null;
            }
            Entity e = type.create(mc.level, EntitySpawnReason.NATURAL);
            if (e instanceof ConfigurableBee configurableBee && !data.entityType.equals(data.beeTypeOrEntityType)) {
                configurableBee.setBeeType(data.beeTypeOrEntityType);
            }
            return e;
        });
        if (bee == null) {
            return;
        }

        bee.tickCount++;
        float angle = bee.tickCount % 360;

        float scale = 0.47F;
        float maxDim = Math.max(bee.getBbWidth(), bee.getBbHeight());
        if (maxDim > 1.0F) {
            scale /= maxDim;
        }

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderState renderState = dispatcher.extractEntity(bee, 0.0F);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.4F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0.0F, -0.2F, 0.0F);
        poseStack.scale(scale, scale, scale);
        dispatcher.submit(renderState, DUMMY_CAMERA, 0.0D, 0.0D, 0.0D, poseStack, collector);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        // The jar block model carries its own extents — this overlay sits inside that volume.
        consumer.accept(new Vector3f(0.0F, 0.0F, 0.0F));
        consumer.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    private static final CameraRenderState DUMMY_CAMERA = new CameraRenderState();

    public record Unbaked() implements SpecialModelRenderer.Unbaked<BeeData> {
        public static final Identifier ID = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "jar_bee");
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<BeeData> bake(SpecialModelRenderer.BakingContext context) {
            return new JarBlockItemRenderer();
        }
    }
}
