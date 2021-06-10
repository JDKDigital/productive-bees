package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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

    public BeeEntity getCachedEntity(World world) {
        if (!cache.containsKey(this)) {
            Entity newBee = getBeeEntity().create(world);
            if (newBee instanceof ConfigurableBeeEntity) {
                ((ConfigurableBeeEntity) newBee).setBeeType(getBeeType().toString());
                ((ConfigurableBeeEntity) newBee).setAttributes();
            }
            cache.put(this, newBee);
        }
        Entity cachedEntity = cache.get(this);
        if (cachedEntity instanceof BeeEntity) {
            return (BeeEntity) cachedEntity;
        }
        return null;
    }

    /**
     * productivebees:osmium, prouctivebees:leafcutter_bee
     */
    public ResourceLocation getBeeType() {
        return beeType != null ? beeType : bee.getRegistryName();
    }

    public static BeeIngredient fromNetwork(PacketBuffer buffer) {
        String beeName = buffer.readUtf();

        return new BeeIngredient(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeName)), buffer.readResourceLocation(), buffer.readBoolean());
    }

    public final void toNetwork(PacketBuffer buffer) {
        buffer.writeUtf("" + bee.getRegistryName());
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
