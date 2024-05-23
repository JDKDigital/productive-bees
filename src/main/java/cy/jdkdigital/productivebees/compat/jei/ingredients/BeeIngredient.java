package cy.jdkdigital.productivebees.compat.jei.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class BeeIngredient implements Comparable<BeeIngredient>
{
    public static final Codec<Supplier<BeeIngredient>> CODEC = Codec.STRING.comapFlatMap(BeeIngredientFactory::read, BeeIngredient::write).stable();

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
        return beeType != null ? beeType : BuiltInRegistries.ENTITY_TYPE.getKey(bee);
    }

    public static BeeIngredient fromNetwork(FriendlyByteBuf buffer) {
        String beeName = buffer.readUtf();

        return new BeeIngredient(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(beeName)), buffer.readResourceLocation(), buffer.readBoolean());
    }

    public final void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf("" + BuiltInRegistries.ENTITY_TYPE.getKey(bee));
        buffer.writeResourceLocation(getBeeType());
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
