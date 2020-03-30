package cy.jdkdigital.productivebees.handler.bee;

import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;

public class BeeStorage implements IBeeStorage {

    private ListNBT nbt = new ListNBT();

    public BeeStorage() {}

    @Nonnull
    @Override
    public ListNBT getBees() {
        return nbt;
    }

    @Override
    public void setBees(ListNBT nbt) {
        this.nbt = nbt;
    }
}
