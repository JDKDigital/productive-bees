package cy.jdkdigital.productivebees.compat.jei.ingredients;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class BeeIngredient
{
    private static final Map<BeeIngredient, WeakReference<Entity>> cache = new WeakHashMap<>();

    private final EntityType<? extends Entity> bee;
    private ResourceLocation beeType;
    private boolean configurable = false;

    public BeeIngredient(EntityType<? extends Entity> bee) {
        this.bee = bee;
    }

    public BeeIngredient(EntityType<? extends Entity> bee, ResourceLocation beeType) {
        this(bee);
        this.beeType = beeType;
    }

    public BeeIngredient(EntityType<? extends Entity> bee, ResourceLocation beeType, boolean isConfigurable) {
        this(bee);
        this.beeType = beeType;
        this.configurable = isConfigurable;
    }

    public EntityType<? extends Entity> getBeeEntity() {
        return bee;
    }

    public Entity getCachedEntity(Level world) {
        Entity entity = null;
        WeakReference<Entity> entityRef = cache.get(this);
        if (entityRef != null) {
            entity = entityRef.get();
        }
        if (entity == null) {
            entity = getBeeEntity().create(world);
            if (entity instanceof ConfigurableBee) {
                ((ConfigurableBee) entity).setBeeType(getBeeType().toString());
                ((ConfigurableBee) entity).setDefaultAttributes();
            }
            cache.put(this, new WeakReference<>(entity));
        }
        return entity;
    }

    /**
     * productivebees:osmium, prouctivebees:leafcutter_bee
     */
    public ResourceLocation getBeeType() {
        return beeType != null ? beeType : ForgeRegistries.ENTITY_TYPES.getKey(bee);
    }

    public static BeeIngredient fromNetwork(FriendlyByteBuf buffer) {
        String beeName = buffer.readUtf();

        return new BeeIngredient(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(beeName)), buffer.readResourceLocation(), buffer.readBoolean());
    }

    public final void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf("" + ForgeRegistries.ENTITY_TYPES.getKey(bee));
        buffer.writeResourceLocation(getBeeType());
        buffer.writeBoolean(configurable);
    }

    @Override
    public String toString() {
        return "BeeIngredient{" +
                "bee=" + bee +
                ", beeType=" + beeType +
                '}';
    }

    public boolean isConfigurable() {
        return configurable;
    }
}
