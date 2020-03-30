package cy.jdkdigital.productivebees.handler.bee;

import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;

public interface IBeeStorage {

    @Nonnull
    ListNBT getBees();

    void setBees(ListNBT nbt);
}
