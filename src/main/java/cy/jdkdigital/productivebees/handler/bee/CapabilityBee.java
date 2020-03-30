package cy.jdkdigital.productivebees.handler.bee;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityBee {
    @CapabilityInject(IBeeStorage.class)
    public static Capability<IBeeStorage> BEE = null;

    public CapabilityBee() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IBeeStorage.class, new Capability.IStorage<IBeeStorage>() {
            public INBT writeNBT(Capability<IBeeStorage> capability, IBeeStorage instance, Direction side) {
                return instance.getBees();
            }

            public void readNBT(Capability<IBeeStorage> capability, IBeeStorage instance, Direction side, INBT nbt) {
                if (!(instance instanceof BeeStorage)) {
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                } else {
                    instance.setBees((ListNBT)nbt);
                }
            }
        }, BeeStorage::new);
    }
}
