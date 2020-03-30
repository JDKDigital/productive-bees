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

public class BeeStorage implements IBeeStorage, INBTSerializable<CompoundNBT> {
    private List<AdvancedBeehiveTileEntityAbstract.Bee> beeList = Lists.newArrayList();

    public BeeStorage() {}

    @Nonnull
    @Override
    public List<AdvancedBeehiveTileEntityAbstract.Bee> getBees() {
        return this.beeList;
    }

    @Override
    public void setBees(List<AdvancedBeehiveTileEntityAbstract.Bee> beeList) {
        this.beeList = beeList;
        onContentsChanged();
    }

    @Override
    public void addBee(AdvancedBeehiveTileEntityAbstract.Bee bee) {
        this.beeList.add(bee);
        onContentsChanged();
    }

    public void removeBee(AdvancedBeehiveTileEntityAbstract.Bee bee) {
        ProductiveBees.LOGGER.info("Remove bee " + bee);
        ProductiveBees.LOGGER.info("from " + this.beeList.size() + " " + this.beeList);
        this.beeList.remove(bee);
        ProductiveBees.LOGGER.info("after " + this.beeList.size() + " " + this.beeList);
        onContentsChanged();
    }

    @Override
    public void clearBees() {
        this.beeList.clear();
        onContentsChanged();
    }

    public ListNBT getBeeListAsListNBT() {
        ListNBT listNBT = new ListNBT();

        for (AdvancedBeehiveTileEntityAbstract.Bee bee : this.getBees()) {
            bee.nbt.removeUniqueId("UUID");
            CompoundNBT beeTag = new CompoundNBT();
            beeTag.put("EntityData", bee.nbt);
            beeTag.putInt("TicksInHive", bee.ticksInHive);
            beeTag.putInt("MinOccupationTicks", bee.minOccupationTicks);
            listNBT.add(beeTag);
        }

        return listNBT;
    }

    public void setBeesFromListNBT(ListNBT list) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT beeTag = list.getCompound(i);
            AdvancedBeehiveTileEntityAbstract.Bee bee = new AdvancedBeehiveTileEntityAbstract.Bee(beeTag.getCompound("EntityData"), beeTag.getInt("TicksInHive"), beeTag.getInt("MinOccupationTicks"));
            this.addBee(bee);
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        ListNBT listNBT = getBeeListAsListNBT();
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Bees", listNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.clearBees();
        ListNBT list = nbt.getList("Bees", Constants.NBT.TAG_COMPOUND);
        setBeesFromListNBT(list);
        onLoad();
    }

    protected void onLoad() {

    }
    public void onContentsChanged() {

    }
}
