package cy.jdkdigital.productivebees.network;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.packets.BeesMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler
{
    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(ProductiveBees.MODID, "buzzinga"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(++id, BeesMessage.class, BeesMessage::encode, BeesMessage::decode, BeesMessage::handle);
    }

    public static void sendToPlayer(BeesMessage message, ServerPlayerEntity player) {
        ProductiveBees.LOGGER.info("sending bee list to " + player.getDisplayName());
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAllPlayers(BeesMessage message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
