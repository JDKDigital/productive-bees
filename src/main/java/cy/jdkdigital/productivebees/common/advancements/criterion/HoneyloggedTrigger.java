package cy.jdkdigital.productivebees.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Feeder;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public class HoneyloggedTrigger extends AbstractCriterionTrigger<HoneyloggedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "honeylogged");

    public ResourceLocation getId() {
        return ID;
    }

    public HoneyloggedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Block block = getBlockCriteria(json);

        return new HoneyloggedTrigger.Instance(block);
    }

    @Nullable
    private static Block getBlockCriteria(JsonObject json) {
        if (json.has("block")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "block"));
            return Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + resourcelocation + "'"));
        } else {
            return null;
        }
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack item) {
        BlockState blockstate = player.getServerWorld().getBlockState(pos);
        this.func_227070_a_(player.getAdvancements(), (trigger) -> trigger.test(blockstate));
    }

    public static class Instance extends CriterionInstance
    {
        private final Block block;

        public Instance(@Nullable Block block) {
            super(HoneyloggedTrigger.ID);
            this.block = block;
        }

        public static HoneyloggedTrigger.Instance placedBlock(Block block) {
            return new HoneyloggedTrigger.Instance(block);
        }

        public boolean test(BlockState state) {
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            }

            return state.has(Feeder.HONEYLOGGED) && state.get(Feeder.HONEYLOGGED);
        }

        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            if (this.block != null) {
                jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            }

            return jsonobject;
        }
    }
}
