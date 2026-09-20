package cy.jdkdigital.productivebees.compat.patchouli;

import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.neoforged.fml.ModList;
import vazkii.patchouli.api.PatchouliAPI;

/** Publishes a Patchouli config flag per bee id, so guide entries can gate on {@code "flag": "productivebees:gems/diamond"}. */
public final class ProductiveBeesPatchouli
{
    private ProductiveBeesPatchouli() {}

    public static void setBeeFlags() {
        if (!ModList.get().isLoaded("patchouli")) {
            return;
        }
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
        if (api.isStub()) {
            return;
        }
        BeeRegistries.all().forEach(holder -> api.setConfigFlag(holder.key().identifier().toString(), true));
    }
}
