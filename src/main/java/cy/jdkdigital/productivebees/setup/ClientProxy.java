package cy.jdkdigital.productivebees.setup;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import cy.jdkdigital.productivebees.client.render.item.JarBlockItemRenderer;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class ClientProxy
{
    @Nullable
    public static MultiBufferSource buffer;

    @SubscribeEvent
    public static void prePlayerRender(RenderLivingEvent.Pre<? extends LivingEntity, ? extends HumanoidModel<? extends LivingEntity>> event) {
        buffer = event.getMultiBufferSource();
    }

    @SubscribeEvent
    public static void postPlayerRender(RenderLivingEvent.Post<? extends LivingEntity, ? extends HumanoidModel<? extends LivingEntity>> event) {
        buffer = null;
    }

    @SubscribeEvent
    public static void onClientLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            JarBlockItemRenderer.clearCache();
        }
    }
}