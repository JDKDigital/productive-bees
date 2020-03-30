package cy.jdkdigital.productivebees.handler.bee;

import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.List;

public interface IBeeStorage {

    @Nonnull
    List<AdvancedBeehiveTileEntityAbstract.Bee> getBees();

    void setBees(List<AdvancedBeehiveTileEntityAbstract.Bee> beeList);

    void addBee(AdvancedBeehiveTileEntityAbstract.Bee bee);

    void clearBees();

    ListNBT getBeeListAsListNBT();

    void setBeesFromListNBT(ListNBT list);

    void onContentsChanged();
}
