package cy.jdkdigital.productivebees.network;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.packets.Messages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

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

        channel.messageBuilder(Messages.BeeDataMessage.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(Messages.BeeDataMessage::decode)
                .encoder(Messages.BeeDataMessage::encode)
                .consumer(Messages.BeeDataMessage::handle)
                .add();
    }

    public static int getId() {
        return ++id;
    }

    public static void sendBeeDataToPlayer(Messages.BeeDataMessage message, ServerPlayer player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAllPlayers(Messages.BeeDataMessage message) {
        channel.send(PacketDistributor.ALL.noArg(), message);
    }
}
