package cy.jdkdigital.productivebees.handler.bee;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityBee
{
    @CapabilityInject(IInhabitantStorage.class)
    public static Capability<IInhabitantStorage> BEE = null;

    public CapabilityBee() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IInhabitantStorage.class);
    }
}
