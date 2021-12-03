package cy.jdkdigital.productivebees.handler.bee;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityBee
{
    public static Capability<IInhabitantStorage> BEE = null;

    public CapabilityBee() {
        BEE = CapabilityManager.get(new CapabilityToken<>() {});;
    }
}
