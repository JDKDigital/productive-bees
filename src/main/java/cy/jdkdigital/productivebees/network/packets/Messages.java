package cy.jdkdigital.productivebees.network.packets;

import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Messages
{
    public static class BeeDataMessage
    {
        public Map<String, CompoundTag> data;

        public BeeDataMessage(Map<String, CompoundTag> data) {
            this.data = data;
        }

        public static void encode(BeeDataMessage message, FriendlyByteBuf buffer) {
            buffer.writeInt(message.data.size());
            for (Map.Entry<String, CompoundTag> entry : message.data.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeNbt(entry.getValue());
            }
        }

        public static BeeDataMessage decode(FriendlyByteBuf buffer) {
            Map<String, CompoundTag> data = new HashMap<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> {
                data.put(buffer.readUtf(), buffer.readAnySizeNbt());
            });
            return new BeeDataMessage(data);
        }

        public static void handle(BeeDataMessage message, Supplier<NetworkEvent.Context> context) {
            BeeReloadListener.INSTANCE.setData(message.data);
            context.get().setPacketHandled(true);
        }
    }
}
