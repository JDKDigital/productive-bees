package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HoneyloggedTrigger extends AbstractCriterionTrigger<HoneyloggedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "honeylogged");

    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    private static Block getBlockCriteria(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonObject, "block"));
            return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
            });
        } else {
            return null;
        }
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack item) {
        BlockState blockstate = player.getServerWorld().getBlockState(pos);
        this.triggerListeners(player, (trigger) -> trigger.test(blockstate));
    }

    @Nonnull
    @Override
    protected Instance deserializeTrigger(JsonObject jsonObject, EntityPredicate.AndPredicate andPredicate, ConditionArrayParser conditionArrayParser) {
        Block block = getBlockCriteria(jsonObject);

        return new HoneyloggedTrigger.Instance(block);
    }

    public static class Instance extends CriterionInstance
    {
        private final Block block;

        public Instance(@Nullable Block block) {
            super(HoneyloggedTrigger.ID, EntityPredicate.AndPredicate.ANY_AND);
            this.block = block;
        }

        public boolean test(BlockState state) {
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            }

            return state.hasProperty(Feeder.HONEYLOGGED) && state.get(Feeder.HONEYLOGGED);
        }

        public JsonObject serialize(ConditionArraySerializer serializer) {
            JsonObject jsonobject = super.serialize(serializer);
            if (this.block != null) {
                jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            }

            return jsonobject;
        }
    }
}
