package cy.jdkdigital.productivebees.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.patchouli.ProductiveBeesPatchouli;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeReloadListener extends JsonReloadListener
{
    public static RecipeManager recipeManager;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final BeeReloadListener INSTANCE = new BeeReloadListener();
    private Map<String, CompoundNBT> BEE_DATA = new HashMap<>();
    private Map<String, JsonObject> BEE_CONDITIONS = new HashMap<>();

    public BeeReloadListener() {
        super(GSON, "productivebees");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(IResourceManager manager, IProfiler profiler) {
        Map<ResourceLocation, JsonElement> map = super.prepare(manager, profiler);

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation id = entry.getKey();
            String simpleId = id.getPath().contains("/") ? id.getNamespace() + ":" + id.getPath().substring(id.getPath().lastIndexOf("/") + 1) : id.toString();
            BEE_CONDITIONS.put(simpleId, entry.getValue().getAsJsonObject());
        }

        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, @Nonnull IResourceManager resourceManager, IProfiler profiler) {
        profiler.push("BeeReloadListener");

        Map<String, CompoundNBT> data = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : dataMap.entrySet()) {
            ResourceLocation id = entry.getKey();

            try {
                if (!CraftingHelper.processConditions(entry.getValue().getAsJsonObject(), "conditions")) {
                    ProductiveBees.LOGGER.debug("Skipping loading productive bee {} as its conditions were not met", id);
                    continue;
                }
            } catch (Exception e) {
                ProductiveBees.LOGGER.debug("Skipping loading productive bee {} as its conditions were invalid", id);
                throw e;
            }

            ResourceLocation simpleId = id.getPath().contains("/") ? new ResourceLocation(id.getNamespace(), id.getPath().substring(id.getPath().lastIndexOf("/") + 1)) : id;
            CompoundNBT nbt = BeeCreator.create(simpleId, entry.getValue().getAsJsonObject());

            data.remove(simpleId.toString());
            data.put(simpleId.toString(), nbt);

            ProductiveBees.LOGGER.debug("Adding to bee data " + simpleId);
        }

        setData(data);

        profiler.popPush("BeeReloadListener");
    }

    public CompoundNBT getData(String id) {
        return BEE_DATA.get(id);
    }

    public Map<String, CompoundNBT> getData() {
        return BEE_DATA;
    }

    public JsonObject getCondition(String id) {
        return BEE_CONDITIONS.get(id);
    }

    public void setData(Map<String, CompoundNBT> data) {
        BEE_DATA = data;
        if (ModList.get().isLoaded("patchouli")) {
            ProductiveBeesPatchouli.setBeeFlags();
        }
    }
}
