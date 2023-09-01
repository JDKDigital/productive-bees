package cy.jdkdigital.productivebees.compat.patchouli;

import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundTag;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Map;

public class ProductiveBeesPatchouli
{
    public static void setBeeFlags() {
        PatchouliAPI.IPatchouliAPI papi = PatchouliAPI.get();

        for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            papi.setConfigFlag(entry.getKey(), true);
        }
    }
}
