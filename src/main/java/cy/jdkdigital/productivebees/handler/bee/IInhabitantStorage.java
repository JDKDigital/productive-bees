package cy.jdkdigital.productivebees.handler.bee;

import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.List;

public interface IInhabitantStorage
{
    @Nonnull
    List<AdvancedBeehiveTileEntityAbstract.Inhabitant> getInhabitants();

    void setInhabitants(List<AdvancedBeehiveTileEntityAbstract.Inhabitant> inhabitantList);

    void addInhabitant(AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant);

    void clearInhabitants();

    @Nonnull
    ListNBT getInhabitantListAsListNBT();

    void setInhabitantsFromListNBT(ListNBT list);

    void onContentsChanged();
}
