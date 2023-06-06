package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class AmberBlockEntity extends AbstractBlockEntity
{
    private static final Map<Integer, PathfinderMob> cachedEntities = new HashMap<>();

    public CompoundTag entityTag = null;

    public AmberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AMBER.get(), pos, state);
    }

    public PathfinderMob getCachedEntity() {
        if (entityTag != null) {
            int key = entityTag.hashCode();
            if (!cachedEntities.containsKey(key)) {
                PathfinderMob cachedEntity = createEntity(level, entityTag);
                cachedEntities.put(key, cachedEntity);
            }
            return cachedEntities.getOrDefault(key, null);
        }
        return null;
    }

    public static PathfinderMob createEntity(Level world, CompoundTag tag) {
        EntityType<?> type = EntityType.byString(tag.getString("entityType")).orElse(null);
        if (type != null) {
            try {
                Entity loadedEntity = type.create(world);
                if (loadedEntity instanceof PathfinderMob pathfinderMob) {
                    loadedEntity.load(tag);
                    return pathfinderMob;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);

        if (entityTag != null) {
            tag.put("EntityData", entityTag);
        }
    }

    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);
        if (tag.contains("EntityData")) {
            this.entityTag = tag.getCompound("EntityData");
        }
    }

    public void setEntity(PathfinderMob target) {
        var entityDataTag = target.saveWithoutId(new CompoundTag());
        entityDataTag.putString("entityType", ForgeRegistries.ENTITY_TYPES.getKey(target.getType()).toString());
        entityDataTag.putString("lootTable", target.getLootTable().toString());
        if (target.hasCustomName()) {
            entityDataTag.putString("name", target.getCustomName().getString());
        } else {
            entityDataTag.putString("name", target.getName().getString());
        }
        this.entityTag = entityDataTag;
        AdvancedBeehiveBlockEntityAbstract.removeIgnoredTags(this.entityTag, true);
        if (this.entityTag.contains("ActiveEffects")) {
            this.entityTag.remove("ActiveEffects");
        }
    }
}
