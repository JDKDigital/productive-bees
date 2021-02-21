package cy.jdkdigital.productivebees.handler.attributes;

import cy.jdkdigital.productivebees.handler.bee.IInhabitantStorage;
import cy.jdkdigital.productivebees.handler.bee.InhabitantStorage;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityBeeAttribute
{
    @CapabilityInject(IInhabitantStorage.class)
    public static Capability<IBeeAttributes> ATTRIBUTE = null;

    public CapabilityBeeAttribute() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IBeeAttributes.class, new Capability.IStorage<IBeeAttributes>()
        {
            public INBT writeNBT(Capability<IBeeAttributes> capability, IBeeAttributes instance, Direction side) {
                return instance.getAsNBT();
            }

            public void readNBT(Capability<IBeeAttributes> capability, IBeeAttributes instance, Direction side, INBT nbt) {
                if (!(instance instanceof InhabitantStorage)) {
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                }
                else {
                    instance.readFromNBT(nbt);
                }
            }
        }, BeeAttributesHandler::new);
    }
}
