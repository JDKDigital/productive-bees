package cy.jdkdigital.productivebees.network;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.packets.Messages;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
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

        channel.messageBuilder(Messages.BeeDataMessage.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(Messages.BeeDataMessage::decode)
                .encoder(Messages.BeeDataMessage::encode)
                .consumer(Messages.BeeDataMessage::handle)
                .add();

        channel.messageBuilder(Messages.ReindexMessage.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(Messages.ReindexMessage::decode)
                .encoder(Messages.ReindexMessage::encode)
                .consumer(Messages.ReindexMessage::handle)
                .add();
    }

    public static int getId() {
        return ++id;
    }

    public static void sendBeeDataToPlayer(Messages.BeeDataMessage message, ServerPlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendReindexCommandToPlayer(Messages.ReindexMessage message, ServerPlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAllPlayers(Messages.BeeDataMessage message) {
        channel.send(PacketDistributor.ALL.noArg(), message);
    }
}
