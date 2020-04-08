package cy.jdkdigital.productivebees.handler.bee;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.List;

public class InhabitantStorage implements IInhabitantStorage, INBTSerializable<CompoundNBT> {
    private List<AdvancedBeehiveTileEntityAbstract.Inhabitant> inhabitantList = Lists.newArrayList();

    public InhabitantStorage() {}

    @Nonnull
    @Override
    public List<AdvancedBeehiveTileEntityAbstract.Inhabitant> getInhabitants() {
        return this.inhabitantList;
    }

    @Override
    public void setInhabitants(List<AdvancedBeehiveTileEntityAbstract.Inhabitant> inhabitantList) {
        this.inhabitantList = inhabitantList;
        onContentsChanged();
    }

    @Override
    public void addInhabitant(AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant) {
        this.inhabitantList.add(inhabitant);
        onContentsChanged();
    }

    @Override
    public void clearInhabitants() {
        this.inhabitantList.clear();
        onContentsChanged();
    }

    @Nonnull
    @Override
    public ListNBT getInhabitantListAsListNBT() {
        ListNBT listNBT = new ListNBT();

        for (AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant : this.getInhabitants()) {
            inhabitant.nbt.removeUniqueId("UUID");
            CompoundNBT tag = new CompoundNBT();
            tag.put("EntityData", inhabitant.nbt);
            tag.putInt("TicksInHive", inhabitant.ticksInHive);
            tag.putInt("MinOccupationTicks", inhabitant.minOccupationTicks);
            tag.putString("Name", inhabitant.localizedName);
            listNBT.add(tag);
        }

        return listNBT;
    }

    @Override
    public void setInhabitantsFromListNBT(ListNBT list) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tag = list.getCompound(i);
            AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant = new AdvancedBeehiveTileEntityAbstract.Inhabitant(tag.getCompound("EntityData"), tag.getInt("TicksInHive"), tag.getInt("MinOccupationTicks"), tag.getString("Name"));
            this.addInhabitant(inhabitant);
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        ListNBT listNBT = getInhabitantListAsListNBT();
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Inhabitants", listNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.clearInhabitants();
        ListNBT list = nbt.getList("Inhabitants", Constants.NBT.TAG_COMPOUND);
        setInhabitantsFromListNBT(list);
        onLoad();
    }

    protected void onLoad() {

    }
    @Override
    public void onContentsChanged() {

    }
}
