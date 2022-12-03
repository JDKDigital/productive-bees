package cy.jdkdigital.productivebees.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class GlowstoneNestFeature extends Feature<BlockStateConfiguration>
{
    public static final Codec<BlockStateConfiguration> CODEC = RecordCodecBuilder.create((conf) -> {
        return conf.group(BlockState.CODEC.fieldOf("state").forGetter((configuration) -> {
            return configuration.state;
        })).apply(conf, BlockStateConfiguration::new);
    });

    public GlowstoneNestFeature(Codec<BlockStateConfiguration> pCodec) {
        super(pCodec);
    }

    public boolean place(FeaturePlaceContext<BlockStateConfiguration> conf) {
        WorldGenLevel worldgenlevel = conf.level();
        BlockPos blockpos = conf.origin();
        Random random = conf.random();
        if (!worldgenlevel.isEmptyBlock(blockpos)) {
            return false;
        } else {
            BlockState blockstate = worldgenlevel.getBlockState(blockpos.above());
            if (!blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.BASALT) && !blockstate.is(Blocks.BLACKSTONE)) {
                return false;
            } else {
                BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();
                Set<BlockPos> set = Sets.newHashSet();
                BiConsumer<BlockPos, BlockState> glowstoneConsumer = (pos, blockState) -> {
                    set.add(pos.immutable());
                    worldgenlevel.setBlock(pos, blockState, 2);
                };

                glowstoneConsumer.accept(blockpos, glowstone);

                for(int i = 0; i < 1500; ++i) {
                    BlockPos blockpos1 = blockpos.offset(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
                    if (worldgenlevel.getBlockState(blockpos1).isAir()) {
                        int j = 0;

                        for(Direction direction : Direction.values()) {
                            if (worldgenlevel.getBlockState(blockpos1.relative(direction)).is(Blocks.GLOWSTONE)) {
                                ++j;
                            }

                            if (j > 1) {
                                break;
                            }
                        }

                        if (j == 1) {
                            glowstoneConsumer.accept(blockpos1, glowstone);
                        }
                    }
                }

                // pick random glowstone and replace with a nest
                if (!set.isEmpty()) {
                    List<BlockPos> glowstonePositions = Lists.newArrayList(set);

                    List<BlockPos> positionsWithAir = glowstonePositions.stream().flatMap((pos) -> {
                        return Stream.of(Direction.values()).map(direction -> {
                            return Feature.isAir(worldgenlevel, pos.relative(direction)) ? pos : null;
                        });
                    }).filter(Objects::nonNull).toList();

                    BlockPos nestPos = positionsWithAir.get(random.nextInt(positionsWithAir.size()));

                    worldgenlevel.setBlock(nestPos, conf.config().state, 2);

                    worldgenlevel.getBlockEntity(nestPos, ModTileEntityTypes.SOLITARY_NEST.get()).ifPresent((nestBlockEntity) -> {
                        ProductiveBees.LOGGER.debug("Spawned glowstone nest at " + nestPos + " " + conf.config().state);

                        BlockState nestBlock = nestBlockEntity.getBlockState();
                        if (nestBlock.getBlock() instanceof SolitaryNest solitaryNest) {
                            List<BeeSpawningRecipe> recipes = solitaryNest.getSpawningRecipes(worldgenlevel.getLevel(), worldgenlevel.getBiome(blockpos).value());
                            BeeSpawningRecipe spawningRecipe = recipes.get(random.nextInt(recipes.size()));
                            BeeIngredient beeIngredient = spawningRecipe.output.get(random.nextInt(spawningRecipe.output.size())).get();
                            try {
                                CompoundTag bee = BeeHelper.getBeeAsCompoundTag(beeIngredient);
                                nestBlockEntity.addBee(bee, random.nextInt(599), 600, null, new TranslatableComponent("entity.productivebees." + beeIngredient.getBeeType().getPath()).getString());
                            } catch (CommandSyntaxException e) {
                                ProductiveBees.LOGGER.warn("Failed to put bee into glowstone nest :(" + e.getMessage());
                            }
                        }
                    });
                }

                return true;
            }
        }
    }
}
