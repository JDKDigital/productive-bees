package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class HoneyloggedTrigger extends AbstractCriterionTrigger<HoneyloggedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "honeylogged");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    private static Block getBlockCriteria(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonObject, "block"));
            return ForgeRegistries.BLOCKS.getValue(resourcelocation);
        }
        else {
            return null;
        }
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack item) {
        BlockState blockstate = player.getServerWorld().getBlockState(pos);
        this.triggerListeners(player, trigger -> trigger.test(blockstate));
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

        @Nonnull
        @Override
        public JsonObject serialize(ConditionArraySerializer serializer) {
            JsonObject jsonobject = super.serialize(serializer);
            if (this.block != null) {
                jsonobject.addProperty("block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(this.block)).toString());
            }

            return jsonobject;
        }
    }
}
