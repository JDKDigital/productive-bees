package cy.jdkdigital.productivebees.init;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.util.GeneGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

public class ModDataComponents
{
    public static final Supplier<DataComponentType<ResourceLocation>> BEE_TYPE = ProductiveBees.DATA_COMPONENTS.register("bee_type", () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<List<ResourceLocation>>> BEE_TYPE_LIST = ProductiveBees.DATA_COMPONENTS.register("bee_type_list", () -> DataComponentType.<List<ResourceLocation>>builder().persistent(ResourceLocation.CODEC.listOf()).cacheEncoding().build());
    public static final Supplier<DataComponentType<ResourceLocation>> NEST_BLOCK = ProductiveBees.DATA_COMPONENTS.register("nest_block", () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<String>> BEE_NAME = ProductiveBees.DATA_COMPONENTS.register("bee_name", () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static final Supplier<DataComponentType<BlockPos>> POSITION = ProductiveBees.DATA_COMPONENTS.register("blockpos", () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final Supplier<DataComponentType<GeneGroup>> GENE_GROUP = ProductiveBees.DATA_COMPONENTS.register("gene_group", () -> DataComponentType.<GeneGroup>builder().persistent(GeneGroup.CODEC).networkSynchronized(GeneGroup.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<List<GeneGroup>>> GENE_GROUP_LIST = ProductiveBees.DATA_COMPONENTS.register("gene_group_list", () -> DataComponentType.<List<GeneGroup>>builder().persistent(GeneGroup.CODEC.listOf()).cacheEncoding().build());

    public static void register() {
    }
}
