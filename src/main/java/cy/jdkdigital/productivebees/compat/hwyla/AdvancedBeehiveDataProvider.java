package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class AdvancedBeehiveDataProvider implements IServerDataProvider<BlockAccessor>
{
    static final AdvancedBeehiveDataProvider INSTANCE = new AdvancedBeehiveDataProvider();

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof AdvancedBeehiveBlockEntityAbstract hive)) return;
        if (hive instanceof SolitaryNestBlockEntity) return;
        if (hive.stored.isEmpty()) return;
        ListTag names = new ListTag();
        for (BeehiveBlockEntity.BeeData beeData : hive.stored) {
            names.add(StringTag.valueOf(occupantName(beeData.occupant).getString()));
        }
        tag.put("bee_names", names);
    }

    private static Component occupantName(BeehiveBlockEntity.Occupant occupant) {
        CompoundTag data = occupant.entityData().getUnsafe();
        String customType = data.getString("type").orElse("");
        if (!customType.isEmpty()) {
            return Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(Identifier.parse(customType)) + "_bee");
        }
        return Component.translatable(occupant.entityData().type().getDescriptionId());
    }

    @Override
    public Identifier getUid() {
        return AdvancedBeehiveProvider.UID;
    }
}
