package cy.jdkdigital.productivebees.network.packets;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Messages
{
    public static class BeesMessage
    {
        public Map<String, CompoundNBT> data;

        public BeesMessage(Map<String, CompoundNBT> data) {
            this.data = data;
        }

        public static void encode(BeesMessage message, PacketBuffer buffer) {
            buffer.writeInt(message.data.size());
            for (Map.Entry<String, CompoundNBT> entry : message.data.entrySet()) {
                buffer.writeString(entry.getKey());
                buffer.writeCompoundTag(entry.getValue());
            }
        }

        public static BeesMessage decode(PacketBuffer buffer) {
            Map<String, CompoundNBT> data = new HashMap<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> {
                data.put(buffer.readString(), buffer.readCompoundTag());
            });
            return new BeesMessage(data);
        }

        public static void handle(BeesMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                BeeReloadListener.INSTANCE.setData(message.data);
                // Trigger jei reload
                ProductiveBees.LOGGER.debug("trigger recipe reload (bees:" + message.data.size() + ")");
                ITagCollectionSupplier tagCollectionSupplier;
                for (ServerType type : ServerType.values()) {
                    if (type.connected()) {
                        switch (type.name) {
                            case "integrated":
                                RecipeManager manager = ProductiveBees.proxy.getWorld().getRecipeManager();
                                net.minecraftforge.client.ForgeHooksClient.onRecipesUpdated(manager);
                                break;
                            case "vanilla":
                                tagCollectionSupplier = ProductiveBees.proxy.getWorld().getTags();
                                MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent.VanillaTagTypes(tagCollectionSupplier));
                                break;
                            case "modded":
                                tagCollectionSupplier = ProductiveBees.proxy.getWorld().getTags();
                                MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent.CustomTagTypes(tagCollectionSupplier));
                                break;
                        }
                    }
                }
            });
            context.get().setPacketHandled(true);
        }
    }

    private enum ServerType {
        // Three cases, since there's no such thing as a vanilla integrated server
        INTEGRATED("integrated", false, true),
        VANILLA_REMOTE("vanilla", true, false),
        MODDED_REMOTE("modded", false, false);

        public final String name;
        public final boolean isVanilla;
        public final boolean isIntegrated;

        ServerType(String name, boolean isVanilla, boolean isIntegrated) {
            this.name = name;
            this.isVanilla = isVanilla;
            this.isIntegrated = isIntegrated;
        }

        public boolean connected() {
            ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
            boolean isVanilla = connection != null && NetworkHooks.isVanillaConnection(connection.getNetworkManager());
            boolean isIntegrated = Minecraft.getInstance().isIntegratedServerRunning();
            return isVanilla == this.isVanilla && isIntegrated == this.isIntegrated;
        }
    }
}
