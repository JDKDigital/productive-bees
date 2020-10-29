package cy.jdkdigital.productivebees.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.network.PacketHandler;
import cy.jdkdigital.productivebees.network.packets.Messages;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeReloadListener extends JsonReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final BeeReloadListener INSTANCE = new BeeReloadListener();
    private Map<String, CompoundNBT> BEE_DATA = new HashMap<>();

    public BeeReloadListener()
    {
        super(GSON, "productivebees");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> dataMap, @Nonnull IResourceManager resourceManager, IProfiler profiler) {
        profiler.startSection("BeeReloadListener");
        for (Map.Entry<ResourceLocation, JsonObject> entry : dataMap.entrySet()) {
            ResourceLocation id = entry.getKey();

            if (!CraftingHelper.processConditions(entry.getValue(), "conditions")) {
                ProductiveBees.LOGGER.debug("Skipping loading productive bee {} as it's conditions were not met", id);
                continue;
            }

            CompoundNBT nbt = BeeCreator.create(id, entry.getValue());

            BEE_DATA.remove(id.toString());
            BEE_DATA.put(id.toString(), nbt);

            ProductiveBees.LOGGER.debug("Adding to bee data " + id);
        }
        try {
            PacketHandler.sendToAllPlayers(new Messages.BeesMessage(BeeReloadListener.INSTANCE.getData()));
        } catch (Exception e) {
            // ignore on server lad when the server is not ready to send messages
        }
        profiler.endStartSection("BeeReloadListener");
    }

    public CompoundNBT getData(String id) {
        return BEE_DATA.get(id);
    }

    public Map<String, CompoundNBT> getData() {
        return BEE_DATA;
    }

    public void setData(Map<String, CompoundNBT> data) {
        BEE_DATA = data;
    }
}
