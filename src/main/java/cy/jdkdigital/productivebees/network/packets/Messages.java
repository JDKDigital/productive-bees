package cy.jdkdigital.productivebees.network.packets;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Messages
{
    public static class BeesMessage
    {
        public Map<ResourceLocation, CompoundNBT> data;

        public BeesMessage(Map<ResourceLocation, CompoundNBT> data) {
            this.data = data;
        }

        public static void encode(BeesMessage message, PacketBuffer buffer) {
            buffer.writeInt(message.data.size());
            for (Map.Entry<ResourceLocation, CompoundNBT> entry : message.data.entrySet()) {
                buffer.writeResourceLocation(entry.getKey());
                buffer.writeCompoundTag(entry.getValue());
            }
        }

        public static BeesMessage decode(PacketBuffer buffer) {
            Map<ResourceLocation, CompoundNBT> data = new HashMap<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> {
                data.put(buffer.readResourceLocation(), buffer.readCompoundTag());
            });
            return new BeesMessage(data);
        }

        public static void handle(BeesMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                BeeReloadListener.INSTANCE.setData(message.data);
                // Trigger jei reload
                ProductiveBees.LOGGER.debug("trigger recipe reload");
                RecipeManager manager = ProductiveBees.proxy.getWorld().getRecipeManager();
                net.minecraftforge.client.ForgeHooksClient.onRecipesUpdated(manager);
            });
            context.get().setPacketHandled(true);
        }
    }
}
