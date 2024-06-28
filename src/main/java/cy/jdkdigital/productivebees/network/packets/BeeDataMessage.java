package cy.jdkdigital.productivebees.network.packets;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record BeeDataMessage(Map<ResourceLocation, CompoundTag> data) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<BeeDataMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "beedata"));

    public static final StreamCodec<ByteBuf, BeeDataMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.unboundedMap(ResourceLocation.CODEC, CompoundTag.CODEC)),
            BeeDataMessage::data,
            BeeDataMessage::new
    );

    public static void clientHandle(final BeeDataMessage data, final IPayloadContext context) {
        BeeReloadListener.INSTANCE.setData(data.data);
    }

    public static void serverHandle(final BeeDataMessage data, final IPayloadContext context) {
        BeeReloadListener.INSTANCE.setData(data.data);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
