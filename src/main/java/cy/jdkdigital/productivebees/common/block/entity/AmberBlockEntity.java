package cy.jdkdigital.productivebees.common.block.entity;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmberBlockEntity extends AbstractBlockEntity
{
    private static final Logger LOGGER = LogUtils.getLogger();
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
            EntityType<?> type = EntityType.byString(tag.getString("id").orElse("")).orElse(null);
            if (type != null) {
                try {
                    Entity loadedEntity = type.create(world, EntitySpawnReason.NATURAL);
                    if (loadedEntity != null) {
                        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(loadedEntity.problemPath(), LOGGER)) {
                            loadedEntity.load(TagValueInput.create(reporter, world.registryAccess(), tag));
                        }
                        return loadedEntity;
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);

        if (entityTag != null) {
            output.store("entityTag", CompoundTag.CODEC, entityTag);
        }
        output.putInt("meltCounter", meltCounter);
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        this.entityTag = input.read("entityTag", CompoundTag.CODEC).orElse(null);
        this.meltCounter = input.getIntOr("meltCounter", 0);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        TypedEntityData<EntityType<?>> entityData = components.get(DataComponents.ENTITY_DATA);
        if (entityData != null) {
            CompoundTag tag = entityData.copyTagWithoutId();
            tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityData.type()).toString());
            this.entityTag = tag;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (this.entityTag != null) {
            String id = this.entityTag.getString("id").orElse("");
            EntityType<?> type = EntityType.byString(id).orElse(null);
            if (type != null) {
                components.set(DataComponents.ENTITY_DATA, TypedEntityData.of(type, this.entityTag));
            }
        }
    }

    public void setEntity(Mob target) {
        CompoundTag entityDataTag;
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(target.problemPath(), LOGGER)) {
            TagValueOutput out = TagValueOutput.createWithContext(reporter, target.registryAccess());
            target.saveWithoutId(out);
            entityDataTag = out.buildResult();
        }
        entityDataTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
        if (target.hasCustomName()) {
            entityDataTag.putString("name", target.getCustomName().getString());
        } else {
            entityDataTag.putString("name", target.getName().getString());
        }
        this.entityTag = entityDataTag;
        AdvancedBeehiveBlockEntityAbstract.removeIgnoredTags(this.entityTag);
    }

    public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, AmberBlockEntity amberBlockEntity) {
        BlockState below = level.getBlockState(blockPos.below());
        if (level instanceof ServerLevel && ++amberBlockEntity.tickCounter%21 == 0 && below.is(BlockTags.CAMPFIRES)) {
            amberBlockEntity.meltCounter = amberBlockEntity.meltCounter + 21;

            int meltingTime = below.is(Blocks.SOUL_CAMPFIRE) ? 400 : 800;
            if (amberBlockEntity.meltCounter > meltingTime) {
                amberBlockEntity.meltCounter = -100000; // fuck you tick accelerators
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
