package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.List;
import java.util.Optional;

public record NestConfiguration(List<OreConfiguration.TargetBlockState> targets, NestSearch search, Optional<String> configKey) implements FeatureConfiguration
{
    public static final Codec<NestConfiguration> CODEC = RecordCodecBuilder.create(b -> b.group(
            Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter(NestConfiguration::targets),
            NestSearch.CODEC.fieldOf("search").forGetter(NestConfiguration::search),
            Codec.STRING.optionalFieldOf("config_key").forGetter(NestConfiguration::configKey)
    ).apply(b, NestConfiguration::new));
}
