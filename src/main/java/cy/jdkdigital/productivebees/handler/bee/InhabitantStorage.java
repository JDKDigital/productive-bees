package cy.jdkdigital.productivebees.handler.bee;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.List;

public class InhabitantStorage implements IInhabitantStorage, INBTSerializable<CompoundTag>
{
    private List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> inhabitantList = Lists.newArrayList();

    public InhabitantStorage() {
    }

    @Nonnull
    @Override
    public List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> getInhabitants() {
        return this.inhabitantList;
    }

    @Override
    public void setInhabitants(List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> inhabitantList) {
        this.inhabitantList = inhabitantList;
    }

    @Override
    public void addInhabitant(AdvancedBeehiveBlockEntityAbstract.Inhabitant inhabitant) {
        this.inhabitantList.add(inhabitant);
    }

    @Override
    public void clearInhabitants() {
        this.inhabitantList.clear();
    }

    @Nonnull
    @Override
    public ListTag getInhabitantListAsListNBT() {
        ListTag listNBT = new ListTag();

        for (AdvancedBeehiveBlockEntityAbstract.Inhabitant inhabitant : this.getInhabitants()) {
            CompoundTag copyNbt = inhabitant.nbt.copy();

            CompoundTag tag = new CompoundTag();
            tag.put("EntityData", copyNbt);
            tag.putInt("TicksInHive", inhabitant.ticksInHive);
            if (inhabitant.flowerPos != null) {
                tag.put("FlowerPos", NbtUtils.writeBlockPos(inhabitant.flowerPos));
            }
            tag.putInt("MinOccupationTicks", inhabitant.minOccupationTicks);
            tag.putString("Name", inhabitant.localizedName);
            listNBT.add(tag);
        }

        return listNBT;
    }

    @Override
    public void setInhabitantsFromListNBT(ListTag list) {
        clearInhabitants();
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag tag = list.getCompound(i);
            BlockPos flowerPos = tag.contains("FlowerPos") ? NbtUtils.readBlockPos(tag.getCompound("FlowerPos")) : null;
            AdvancedBeehiveBlockEntityAbstract.Inhabitant inhabitant = new AdvancedBeehiveBlockEntityAbstract.Inhabitant(tag.getCompound("EntityData"), tag.getInt("TicksInHive"), tag.getInt("MinOccupationTicks"), flowerPos, tag.getString("Name"));
            this.addInhabitant(inhabitant);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag listNBT = getInhabitantListAsListNBT();
        CompoundTag nbt = new CompoundTag();
        nbt.put("Inhabitants", listNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag list = nbt.getList("Inhabitants", 10);
        setInhabitantsFromListNBT(list);
    }

    @Override
    public void onContentsChanged() {

    }
}
