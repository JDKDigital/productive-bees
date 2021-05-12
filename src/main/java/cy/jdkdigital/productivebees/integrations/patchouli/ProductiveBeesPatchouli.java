package cy.jdkdigital.productivebees.integrations.patchouli;

import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.nbt.CompoundNBT;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Map;

public class ProductiveBeesPatchouli
{
    public static void setBeeFlags() {
        PatchouliAPI.IPatchouliAPI papi = PatchouliAPI.get();

        for (Map.Entry<String, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            papi.setConfigFlag(entry.getKey(), true);
        }
    }
}
