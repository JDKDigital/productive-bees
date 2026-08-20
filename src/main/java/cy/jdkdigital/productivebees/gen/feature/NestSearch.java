package cy.jdkdigital.productivebees.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public sealed interface NestSearch permits NestSearch.Surface, NestSearch.Tree, NestSearch.Reed, NestSearch.OreRange, NestSearch.Structure, NestSearch.Cavern
{
    Map<String, MapCodec<? extends NestSearch>> CODECS_BY_TYPE = Map.of(
            "surface", Surface.CODEC,
            "tree", Tree.CODEC,
            "reed", Reed.CODEC,
            "ore_range", OreRange.CODEC,
            "structure", Structure.CODEC,
            "cavern", Cavern.CODEC
    );

    Codec<NestSearch> CODEC = Codec.STRING.<NestSearch>dispatch(NestSearch::typeName, CODECS_BY_TYPE::get);

    String typeName();

    @Nullable
    BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, OreConfiguration.TargetBlockState target);

    record Surface(boolean placeOntop) implements NestSearch
    {
        public static final MapCodec<Surface> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                Codec.BOOL.optionalFieldOf("place_ontop", false).forGetter(Surface::placeOntop)
        ).apply(b, Surface::new));

        @Override
        public String typeName() {
            return "surface";
        }

        @Override
        public @Nullable BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos origin, OreConfiguration.TargetBlockState target) {
            BlockPos pos = origin.south(rand.nextInt(14)).east(rand.nextInt(14));
            while (pos.getY() < 50 || !level.isEmptyBlock(pos)) {
                pos = pos.above();
            }
            if (!placeOntop) {
                pos = pos.below();
            }
            BlockState matched = placeOntop ? level.getBlockState(pos.below()) : level.getBlockState(pos);
            return target.target.test(matched, rand) ? pos : null;
        }
    }

    record Tree() implements NestSearch
    {
        public static final MapCodec<Tree> CODEC = MapCodec.unit(Tree::new);

        @Override
        public String typeName() {
            return "tree";
        }

        @Override
        public @Nullable BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos origin, OreConfiguration.TargetBlockState target) {
            BlockPos pos = origin.atY(chunkGenerator.getSeaLevel());
            while (pos.getY() < 127 && !level.isEmptyBlock(pos)) {
                pos = pos.above();
            }
            pos = pos.above(rand.nextInt(4));

            BlockPos found = null;
            outer:
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos cand = pos.offset(x, 0, z);
                    if (target.target.test(level.getBlockState(cand), rand)) {
                        found = cand;
                        break outer;
                    }
                }
            }
            if (found == null) return null;

            // Walk east while still inside a thicc trunk.
            while (target.target.test(level.getBlockState(found.east(1)), rand)) {
                found = found.east(1);
            }
            return target.target.test(level.getBlockState(found), rand) ? found : null;
        }
    }

    record Reed() implements NestSearch
    {
        public static final MapCodec<Reed> CODEC = MapCodec.unit(Reed::new);

        @Override
        public String typeName() {
            return "reed";
        }

        @Override
        public @Nullable BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos origin, OreConfiguration.TargetBlockState target) {
            BlockPos pos = origin.above(chunkGenerator.getSpawnHeight(level));
            while (pos.getY() < 127 && !level.isEmptyBlock(pos)) {
                pos = pos.above();
            }
            pos = pos.above(rand.nextInt(2));

            BlockPos found = null;
            outer:
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos cand = pos.offset(x, 0, z);
                    if (target.target.test(level.getBlockState(cand), rand)) {
                        found = cand;
                        break outer;
                    }
                }
            }
            if (found == null) return null;

            if (!target.target.test(level.getBlockState(found.below()), rand)) {
                found = found.above();
            }
            return target.target.test(level.getBlockState(found), rand) ? found : null;
        }
    }

    record OreRange(int yMin, int yMax) implements NestSearch
    {
        public static final MapCodec<OreRange> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                Codec.INT.fieldOf("y_min").forGetter(OreRange::yMin),
                Codec.INT.fieldOf("y_max").forGetter(OreRange::yMax)
        ).apply(b, OreRange::new));

        @Override
        public String typeName() {
            return "ore_range";
        }

        @Override
        public @Nullable BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos origin, OreConfiguration.TargetBlockState target) {
            BlockPos pos = origin.south(rand.nextInt(14)).east(rand.nextInt(14)).above(yMin);
            while (pos.getY() < yMax) {
                pos = pos.above(2);
                if (target.target.test(level.getBlockState(pos), rand)) {
                    return pos;
                }
            }
            return null;
        }
    }

    record Structure(int offsetSpan) implements NestSearch
    {
        public static final MapCodec<Structure> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                Codec.INT.fieldOf("offset_span").forGetter(Structure::offsetSpan)
        ).apply(b, Structure::new));

        @Override
        public String typeName() {
            return "structure";
        }

        @Override
        public @Nullable BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos origin, OreConfiguration.TargetBlockState target) {
            BlockPos pos = origin.south(rand.nextInt(14)).east(rand.nextInt(14)).above(50);

            if (!target.target.test(level.getBlockState(pos), rand)) {
                boolean adjusted = false;
                for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
                    if (target.target.test(level.getBlockState(pos.relative(dir, 2)), rand)) {
                        pos = pos.relative(dir, 3);
                        adjusted = true;
                        break;
                    }
                }
                if (!adjusted) return null;
            }

            pos = pos.relative(Direction.UP, rand.nextInt(offsetSpan));

            edgeFinding:
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
                for (int i = 1; i <= 5; i++) {
                    if (level.isEmptyBlock(pos.relative(dir, i))) {
                        pos = pos.relative(dir, i - 1);
                        break edgeFinding;
                    }
                }
            }

            return target.target.test(level.getBlockState(pos), rand) ? pos : null;
        }
    }

    record Cavern(boolean top) implements NestSearch
    {
        public static final MapCodec<Cavern> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                Codec.BOOL.optionalFieldOf("top", false).forGetter(Cavern::top)
        ).apply(b, Cavern::new));

        @Override
        public String typeName() {
            return "cavern";
        }

        @Override
        public @Nullable BlockPos search(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos origin, OreConfiguration.TargetBlockState target) {
            BlockPos pos = origin.south(rand.nextInt(14)).east(rand.nextInt(14));
            while (pos.getY() < 127 && !target.target.test(level.getBlockState(pos), rand)) {
                pos = pos.above();
            }
            if (pos.getY() >= 127) return null;
            if (top) {
                while (pos.getY() < 127 && !level.isEmptyBlock(pos)) {
                    pos = pos.above();
                }
                pos = pos.below();
            }
            return target.target.test(level.getBlockState(pos), rand) ? pos : null;
        }
    }
}
