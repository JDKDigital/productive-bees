package cy.jdkdigital.productivebees.common.crafting.ingredient;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class BeeIngredient
{
    public static final Codec<Supplier<BeeIngredient>> CODEC = Codec.STRING.comapFlatMap(BeeIngredientFactory::read, BeeIngredient::write).stable();
    public static final Codec<List<Supplier<BeeIngredient>>> LIST_CODEC = Codec.list(CODEC);

    private static final int CACHE_CAPACITY = 512;
    private static final Map<BeeIngredient, Entity> cache = new LinkedHashMap<>(64, 0.75f, true)
    {
        @Override
        protected boolean removeEldestEntry(Map.Entry<BeeIngredient, Entity> eldest) {
            return size() > CACHE_CAPACITY;
        }
    };

    private EntityType<? extends Entity> bee;
    private Identifier beeType;
    private boolean configurable = false;

    public BeeIngredient(EntityType<? extends Entity> bee) {
        this.bee = bee;
    }

    public BeeIngredient(EntityType<? extends Entity> bee, Identifier beeType) {
        this(bee);
        this.beeType = beeType;
    }

    public BeeIngredient(EntityType<? extends Entity> bee, Identifier beeType, boolean isConfigurable) {
        this(bee);
        this.beeType = beeType;
        this.configurable = isConfigurable;
    }

    public EntityType<? extends Entity> getBeeEntity() {
        return bee;
    }

    public Entity getCachedEntity(Level world) {
        Entity entity = cache.get(this);
        try {
            if (entity == null) {
                entity = getBeeEntity().create(world, EntitySpawnReason.NATURAL);
                if (entity instanceof ConfigurableBee) {
                    ((ConfigurableBee) entity).setBeeType(getBeeType().toString());
                    ((ConfigurableBee) entity).setDefaultAttributes();
                }
                cache.put(this, entity);
            }
        } catch (IllegalStateException ise) {
            // no op
        }
        return entity;
    }

    public static void clearEntityCache() {
        cache.clear();
    }

    /**
     * productivebees:osmium, prouctivebees:leafcutter_bee
     */
    public Identifier getBeeType() {
        return beeType != null ? beeType : BuiltInRegistries.ENTITY_TYPE.getKey(bee);
    }

    public static BeeIngredient fromNetwork(FriendlyByteBuf buffer) {
        String beeName = buffer.readUtf();
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(beeName)).map(Holder::value).orElse(null);
        return new BeeIngredient(entityType, buffer.readIdentifier(), buffer.readBoolean());
    }

    public final void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf("" + BuiltInRegistries.ENTITY_TYPE.getKey(bee));
        buffer.writeUtf(getBeeType().toString());
        buffer.writeBoolean(configurable);
    }

    public static String write(Supplier<BeeIngredient> beeIngredient) {
        var ing = beeIngredient.get();
        return ing.beeType != null ? ing.beeType.toString() : BuiltInRegistries.ENTITY_TYPE.getKey(ing.bee).toString();
    }

    @Override
    public String toString() {
        return "BeeIngredient{bee=" + bee + ", beeType=" + beeType + "}";
    }

    public boolean isConfigurable() {
        return configurable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeeIngredient that = (BeeIngredient) o;
        if (configurable == that.configurable && beeType != null && beeType.equals(that.beeType)) {
            return true;
        }
        return bee.equals(that.bee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BuiltInRegistries.ENTITY_TYPE.getKey(bee), beeType, configurable);
    }
}
