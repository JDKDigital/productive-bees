package cy.jdkdigital.productivebees.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Set;

public class ModIsActive implements ILootCondition {
    private static final ModIsActive INSTANCE = new ModIsActive();

    private ModIsActive() {
    }

    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootParameters.THIS_ENTITY);
    }

    public boolean test(LootContext ctx) {
        Entity entity = ctx.get(LootParameters.THIS_ENTITY);
        return entity instanceof ProductiveBeeEntity;
    }

    public static IBuilder builder() {
        return () -> INSTANCE;
    }

    public static class Serializer extends AbstractSerializer<ModIsActive> {
        public Serializer() {
            super(new ResourceLocation(ProductiveBees.MODID, " od_is_active"), ModIsActive.class);
        }

        public void serialize(JsonObject p_186605_1_, ModIsActive p_186605_2_, JsonSerializationContext p_186605_3_) {
        }

        public ModIsActive deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
            return ModIsActive.INSTANCE;
        }
    }
}
