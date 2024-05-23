package cy.jdkdigital.productivebees.setup;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class ClientProxy
{
    // TODO check if this is still needed
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
}