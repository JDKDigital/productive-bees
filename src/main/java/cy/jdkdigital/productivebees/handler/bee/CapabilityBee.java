package cy.jdkdigital.productivebees.handler.bee;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
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
        CapabilityManager.INSTANCE.register(IInhabitantStorage.class, new Capability.IStorage<IInhabitantStorage>()
        {
            public INBT writeNBT(Capability<IInhabitantStorage> capability, IInhabitantStorage instance, Direction side) {
                return instance.getInhabitantListAsListNBT();
            }

            public void readNBT(Capability<IInhabitantStorage> capability, IInhabitantStorage instance, Direction side, INBT nbt) {
                if (!(instance instanceof InhabitantStorage)) {
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                }
                else {
                    instance.setInhabitantsFromListNBT((ListNBT) nbt);
                }
            }
        }, InhabitantStorage::new);
    }
}
