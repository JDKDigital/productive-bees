package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.init.ModFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record NestBiomeModifier(HolderSet<Biome> biomes, GenerationStep.Decoration generationStage, HolderSet<PlacedFeature> features, float temperature) implements BiomeModifier
{
    private static DataResult<GenerationStep.Decoration> generationStageFromString(String name) {
        try {
            return DataResult.success(GenerationStep.Decoration.valueOf(name));
        } catch (Exception e) {
            return DataResult.error(() -> "Not a decoration stage: " + name);
        }
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && this.biomes.contains(biome) && this.temperature > builder.getClimateSettings().getTemperature()) {
            BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
            this.features.forEach(holder -> generation.addFeature(this.generationStage, holder));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return ModFeatures.NEST_BIOME_MODIFIER.get();
    }

    public static MapCodec<NestBiomeModifier> makeCodec() {
        return RecordCodecBuilder.mapCodec(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(NestBiomeModifier::biomes),
                Codec.STRING.comapFlatMap(NestBiomeModifier::generationStageFromString, GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(NestBiomeModifier::generationStage),
                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(NestBiomeModifier::features),
                Codec.FLOAT.fieldOf("maxTemperature").forGetter(NestBiomeModifier::temperature)
        ).apply(builder, NestBiomeModifier::new));
    }
}
