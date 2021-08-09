package cy.jdkdigital.productivebees.network.packets;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

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
            context.get().enqueueWork(() -> {
                BeeReloadListener.INSTANCE.setData(message.data);

                int delay = ProductiveBeesConfig.GENERAL.beeSyncDelay.get();
                if (delay == 0) {
                    Messages.updateJEI();
                }
            });
            context.get().setPacketHandled(true);
        }
    }

    public static class ReindexMessage
    {
        public ReindexMessage() {
        }

        public static void encode(ReindexMessage message, FriendlyByteBuf buffer) {
        }

        public static ReindexMessage decode(FriendlyByteBuf buffer) {
            return new ReindexMessage();
        }

        public static void handle(ReindexMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                // Trigger jei reload
                ProductiveBees.LOGGER.debug("trigger recipe reload (bees:" + BeeReloadListener.INSTANCE.getData().size() + ")");
                Messages.updateJEI();
            });
            context.get().setPacketHandled(true);
        }
    }

    private static void updateJEI() {
        ProductiveBees.LOGGER.info("Calling updateJEI");
        for (ServerType type : ServerType.values()) {
            if (type.connected()) {
                switch (type.name) {
                    case "integrated":
                        RecipeManager manager = ProductiveBees.proxy.getWorld().getRecipeManager();
                        net.minecraftforge.client.ForgeHooksClient.onRecipesUpdated(manager);
                        break;
                    case "vanilla":
                        MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent.VanillaTagTypes(ProductiveBees.proxy.getWorld().getTagManager()));
                        break;
                    case "modded":
                        MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent.CustomTagTypes(ProductiveBees.proxy.getWorld().getTagManager()));
                        break;
                }
            }
        }
    }

    private enum ServerType
    {
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
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            boolean isVanilla = connection != null && NetworkHooks.isVanillaConnection(connection.getConnection());
            boolean isIntegrated = Minecraft.getInstance().hasSingleplayerServer();
            return isVanilla == this.isVanilla && isIntegrated == this.isIntegrated;
        }
    }
}
