package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class BeeIngredient
{
    private static Map<BeeIngredient, Entity> cache = new HashMap<>();

    private EntityType<? extends Entity> bee;
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
        if (!cache.containsKey(this)) {
            Entity newBee = getBeeEntity().create(world);
            if (newBee instanceof ConfigurableBee) {
                ((ConfigurableBee) newBee).setBeeType(getBeeType().toString());
                ((ConfigurableBee) newBee).setDefaultAttributes();
            }
            cache.put(this, newBee);
        }
        return cache.getOrDefault(this, null);
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
