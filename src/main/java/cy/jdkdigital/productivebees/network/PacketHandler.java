package cy.jdkdigital.productivebees.network;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.packets.BeesMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.*;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler
{
    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel channel;

    public static void init() {
        channel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ProductiveBees.MODID, "buzzinga"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();

        channel.messageBuilder(BeesMessage.class, getId(), NetworkDirection.LOGIN_TO_CLIENT)
                .loginIndex(BeesMessage::getLoginIndex, BeesMessage::setLoginIndex)
                .decoder(BeesMessage::decode)
                .encoder(BeesMessage::encode)
                .consumer(FMLHandshakeHandler.biConsumerFor((handler, msg, ctx) -> BeesMessage.handle(msg, ctx)))
                .markAsLoginPacket()
                .add();
    }

    public static int getId() {
        return ++id;
    }
}
