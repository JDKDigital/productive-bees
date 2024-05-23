package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmberBlockEntity extends AbstractBlockEntity
{
    private int tickCounter = 0;
    private int meltCounter = 0;

    private static final Map<Integer, Entity> cachedEntities = new HashMap<>();

    public CompoundTag entityTag = null;

    public AmberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AMBER.get(), pos, state);
    }

    @Nullable
    public Entity getCachedEntity() {
        if (entityTag != null) {
            int key = entityTag.hashCode();
            if (!cachedEntities.containsKey(key)) {
                Entity cachedEntity = createEntity(level, entityTag);
                cachedEntities.put(key, cachedEntity);
            }
            return cachedEntities.getOrDefault(key, null);
        }
        return null;
    }

    @Nullable
    public static Entity createEntity(Level world, CompoundTag tag) {
        if (tag != null) {
            EntityType<?> type = EntityType.byString(tag.getString("entityType")).orElse(null);
            if (type != null) {
                try {
                    Entity loadedEntity = type.create(world);
                    if (loadedEntity != null) {
                        loadedEntity.load(tag);
                        return loadedEntity;
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);

        if (entityTag != null) {
            tag.put("EntityData", entityTag);
        }
        tag.putInt("meltCounter", meltCounter);
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("EntityData")) {
            this.entityTag = tag.getCompound("EntityData");
        }
        this.meltCounter = tag.contains("meltCounter") ? tag.getInt("meltCounter") : 0;
    }

    public void setEntity(Mob target) {
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

    public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, AmberBlockEntity amberBlockEntity) {
        BlockState below = level.getBlockState(blockPos.below());
        if (level instanceof ServerLevel && ++amberBlockEntity.tickCounter%21 == 0 && below.is(BlockTags.CAMPFIRES)) {
            amberBlockEntity.meltCounter = amberBlockEntity.meltCounter + 21;

            int meltingTime = below.is(Blocks.SOUL_CAMPFIRE) ? 400 : 800;
            if (amberBlockEntity.meltCounter > meltingTime) {
                Entity mob = AmberBlockEntity.createEntity(level, amberBlockEntity.entityTag);
                if (mob != null) {
                    mob.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                    List<Player> players = level.getEntitiesOfClass(Player.class, (new AABB(blockPos).inflate(10.0D, 5.0D, 10.0D)));
                    if (players.size() > 0 && mob instanceof PathfinderMob pathfinderMob) {
                        pathfinderMob.setLastHurtByMob(players.iterator().next());
                    }
                    // release entity
                    level.addFreshEntity(mob);
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
