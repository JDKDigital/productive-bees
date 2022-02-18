package cy.jdkdigital.productivebees.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy implements IProxy
{
    @Nullable
    public static MultiBufferSource buffer;

    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @SubscribeEvent
    public static void prePlayerRender(RenderLivingEvent.Pre<? extends LivingEntity, ? extends HumanoidModel<? extends LivingEntity>> event) {
        buffer = event.getMultiBufferSource();
    }

    @SubscribeEvent
    public static void postPlayerRender(RenderLivingEvent.Post<? extends LivingEntity, ? extends HumanoidModel<? extends LivingEntity>> event) {
        buffer = null;
    }
}