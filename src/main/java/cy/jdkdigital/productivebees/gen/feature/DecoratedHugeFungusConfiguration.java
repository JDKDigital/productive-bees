package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.List;

public class DecoratedHugeFungusConfiguration extends HugeFungusConfiguration {
    public static final Codec<DecoratedHugeFungusConfiguration> CODEC = RecordCodecBuilder
            .create((configurationInstance) -> configurationInstance.group(
                BlockState.CODEC.fieldOf("valid_base_block").forGetter((configuration) -> configuration.validBaseState),
                BlockState.CODEC.fieldOf("stem_state").forGetter((configuration) -> configuration.stemState),
                BlockState.CODEC.fieldOf("hat_state").forGetter((configuration) -> configuration.hatState),
                BlockState.CODEC.fieldOf("decor_state").forGetter((configuration) -> configuration.decorState),
                BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter((p_284923_) -> p_284923_.replaceableBlocks),
                BlockState.CODEC.fieldOf("nest_state").forGetter((configuration) -> configuration.nestState),
                TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter((configuration) -> configuration.decorators),
                Codec.BOOL.fieldOf("planted").orElse(false).forGetter((configuration) -> configuration.planted)
            )
            .apply(configurationInstance, DecoratedHugeFungusConfiguration::new));

    public final BlockState nestState;
    public final List<TreeDecorator> decorators;

    public DecoratedHugeFungusConfiguration(BlockState validBaseState, BlockState stemState, BlockState hatState, BlockState decorState, BlockPredicate replaceableBlocks, BlockState nestState, List<TreeDecorator> decorators, boolean planted) {
        super(validBaseState, stemState, hatState, decorState, replaceableBlocks, planted);
        this.nestState = nestState;
        this.decorators = decorators;
    }
}
