package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.packets.BeeDataMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    public static void tabContents(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ProductiveBees.MODID).versioned("1").optional();
        registrar.playToClient(
                BeeDataMessage.Type,
                BeeDataMessage.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleData,
                        ServerPayloadHandler::handleData
                )
        )
    }
}
