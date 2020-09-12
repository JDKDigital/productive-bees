package cy.jdkdigital.productivebees.network;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.packets.Messages;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
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

        channel.messageBuilder(Messages.BeesMessage.class, getId(), NetworkDirection.LOGIN_TO_CLIENT)
                .loginIndex(Messages.BeesMessage::getLoginIndex, Messages.BeesMessage::setLoginIndex)
                .decoder(Messages.BeesMessage::decode)
                .encoder(Messages.BeesMessage::encode)
                .consumer(FMLHandshakeHandler.biConsumerFor((handler, msg, ctx) -> Messages.BeesMessage.handle(msg, ctx)))
                .markAsLoginPacket()
                .add();
    }

    public static int getId() {
        return ++id;
    }
}
