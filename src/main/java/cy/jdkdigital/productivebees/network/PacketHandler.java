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

        channel.messageBuilder(Messages.BeesMessage.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(Messages.BeesMessage::decode)
                .encoder(Messages.BeesMessage::encode)
                .consumer(Messages.BeesMessage::handle)
                .add();
    }

    public static int getId() {
        return ++id;
    }

    public static void sendToPlayer(Messages.BeesMessage message, ServerPlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAllPlayers(Messages.BeesMessage message) {
        channel.send(PacketDistributor.ALL.noArg(), message);
    }
}
