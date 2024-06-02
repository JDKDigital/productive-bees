package cy.jdkdigital.productivebees.network.packets;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public record BeeDataMessage(Map<String, CompoundTag> data) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<BeeDataMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(ProductiveBees.MODID, "beedata"));

    public static final StreamCodec<ByteBuf, BeeDataMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC)),
            BeeDataMessage::data,
            BeeDataMessage::new
    );

    public static void handle(BeeDataMessage message, Supplier<NetworkEvent.Context> context) {
        BeeReloadListener.INSTANCE.setData(message.data);
        context.get().setPacketHandled(true);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
