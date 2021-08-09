package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class HoneyloggedTrigger extends SimpleCriterionTrigger<HoneyloggedTrigger.Instance>
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
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            return ForgeRegistries.BLOCKS.getValue(resourcelocation);
        }
        else {
            return null;
        }
    }

    public void trigger(ServerPlayer player, BlockPos pos, ItemStack item) {
        BlockState blockstate = player.getLevel().getBlockState(pos);
        this.trigger(player, trigger -> trigger.test(blockstate));
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject jsonObject, EntityPredicate.Composite andPredicate, DeserializationContext conditionArrayParser) {
        Block block = getBlockCriteria(jsonObject);

        return new HoneyloggedTrigger.Instance(block);
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        private final Block block;

        public Instance(@Nullable Block block) {
            super(HoneyloggedTrigger.ID, EntityPredicate.Composite.ANY);
            this.block = block;
        }

        public boolean test(BlockState state) {
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            }

            return state.hasProperty(Feeder.HONEYLOGGED) && state.getValue(Feeder.HONEYLOGGED);
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(SerializationContext serializer) {
            JsonObject jsonobject = super.serializeToJson(serializer);
            if (this.block != null) {
                jsonobject.addProperty("block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(this.block)).toString());
            }

            return jsonobject;
        }
    }
}
