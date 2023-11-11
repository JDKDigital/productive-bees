package cy.jdkdigital.productivebees.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public class OptionalCopyBlockState extends LootItemConditionalFunction
{
    final Block block;
    final Set<Property<?>> properties;

    OptionalCopyBlockState(LootItemCondition[] lootItemConditions, Block pBlock, Set<Property<?>> pStatePredicate) {
        super(lootItemConditions);
        this.block = pBlock;
        this.properties = pStatePredicate;
    }

    @Override
    public LootItemFunctionType getType() {
        return ProductiveBees.OPTIONAL_BLOCK_STATE_PROPERTY.get();
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    protected ItemStack run(ItemStack p_80060_, LootContext p_80061_) {
        BlockState blockstate = p_80061_.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockstate != null) {
            CompoundTag compoundtag = p_80060_.getOrCreateTag();
            CompoundTag compoundtag1;
            if (compoundtag.contains("BlockStateTag", 10)) {
                compoundtag1 = compoundtag.getCompound("BlockStateTag");
            } else {
                compoundtag1 = new CompoundTag();
                compoundtag.put("BlockStateTag", compoundtag1);
            }

            this.properties.stream().filter(blockstate::hasProperty).forEach((p_80072_) -> {
                compoundtag1.putString(p_80072_.getName(), serialize(blockstate, p_80072_));
            });
        }

        return p_80060_;
    }

    public static OptionalCopyBlockState.Builder copyState(Block p_80063_) {
        return new OptionalCopyBlockState.Builder(p_80063_);
    }

    private static <T extends Comparable<T>> String serialize(BlockState p_80065_, Property<T> p_80066_) {
        T t = p_80065_.getValue(p_80066_);
        return p_80066_.getName(t);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<OptionalCopyBlockState.Builder>
    {
        private final Block block;
        private final Set<Property<?>> properties = Sets.newHashSet();

        Builder(Block p_80079_) {
            this.block = p_80079_;
        }

        public OptionalCopyBlockState.Builder copy(Property<?> property) {
            if (!this.block.getStateDefinition().getProperties().contains(property)) {
                throw new IllegalStateException("Property " + property + " is not present on block " + this.block);
            } else {
                this.properties.add(property);
                return this;
            }
        }

        protected OptionalCopyBlockState.Builder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return new OptionalCopyBlockState(this.getConditions(), this.block, this.properties);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<OptionalCopyBlockState>
    {
        public void serialize(JsonObject json, OptionalCopyBlockState optionalCopyBlockState, JsonSerializationContext serializationContext) {
            super.serialize(json, optionalCopyBlockState, serializationContext);
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(optionalCopyBlockState.block).toString());
            JsonArray jsonarray = new JsonArray();
            optionalCopyBlockState.properties.forEach((property) -> {
                if (property != null) {
                    jsonarray.add(property.getName());
                }
            });
            json.add("properties", jsonarray);
        }

        public OptionalCopyBlockState deserialize(JsonObject p_80093_, JsonDeserializationContext p_80094_, LootItemCondition[] p_80095_) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_80093_, "block"));
            Block block = BuiltInRegistries.BLOCK.getOptional(resourcelocation).orElse(Blocks.AIR);
            StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
            Set<Property<?>> set = Sets.newHashSet();
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_80093_, "properties", (JsonArray) null);
            if (jsonarray != null) {
                jsonarray.forEach((json) -> {
                    set.add(statedefinition.getProperty(GsonHelper.convertToString(json, "property")));
                });
            }

            return new OptionalCopyBlockState(p_80095_, block, set);
        }
    }
}
