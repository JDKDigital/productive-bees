package cy.jdkdigital.productivebees.common.fluid;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import javax.annotation.Nullable;

public abstract class HoneyFluid extends BaseFlowingFluid
{
    public static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/honey/still");
    public static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/honey/flow");
    public static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "block/honey/overlay");

    protected HoneyFluid() {
        super(new BaseFlowingFluid.Properties(
                ModFluids.HONEY_FLUID_TYPE,
                ModFluids.HONEY,
                ModFluids.HONEY_FLOWING
        ).bucket(ModItems.HONEY_BUCKET).block(ModBlocks.HONEY));
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.HONEY_FLOWING.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.HONEY.get();
    }

    @Override
    public void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random) {
        BlockPos blockpos = pos.above();
        if (random.nextInt(100) == 0 && !worldIn.getBlockState(blockpos).getFluidState().equals(state)) {
            worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Nullable
    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_HONEY;
    }

    @Override
    public int getSlopeFindDistance(LevelReader worldIn) {
        return worldIn.dimensionType().ultraWarm() ? 6 : 3;
    }

    @Override
    public boolean isSame(Fluid fluidIn) {
        return fluidIn.is(ModTags.HONEY);
    }

    @Override
    public int getDropOff(LevelReader worldIn) {
        return worldIn.dimensionType().ultraWarm() ? 1 : 2;
    }

    @Override
    public int getTickDelay(LevelReader worldIn) {
        return worldIn.dimensionType().ultraWarm() ? 10 : 30;
    }

    @Override
    public int getSpreadDelay(Level world, BlockPos pos, FluidState state, FluidState FluidState) {
        int i = this.getTickDelay(world);
        if (!state.isEmpty() && !FluidState.isEmpty() && !state.getValue(FALLING) && !FluidState.getValue(FALLING) && FluidState.getHeight(world, pos) > state.getHeight(world, pos) && world.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

//    public boolean shouldFreeze(LevelReader pLevel, Biome biome, BlockPos pos) {
//        if (!biome.warmEnoughToRain(pos)) {
//            if (pos.getY() >= pLevel.getMinBuildHeight() && pos.getY() < pLevel.getMaxBuildHeight() && pLevel.getBrightness(LightLayer.BLOCK, pos) < 10) {
//                BlockState blockstate = pLevel.getBlockState(pos);
//                FluidState fluidstate = pLevel.getFluidState(pos);
//                if (fluidstate.getType() == ModFluids.HONEY.get() && blockstate.getBlock() instanceof LiquidBlock) {
//                    boolean flag = this.isHoneyAt(pLevel, pos.west()) && this.isHoneyAt(pLevel, pos.east()) && this.isHoneyAt(pLevel, pos.north()) && this.isHoneyAt(pLevel, pos.south());
//                    if (!flag) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean isHoneyAt(LevelReader level, BlockPos pPos) {
//        return level.getFluidState(pPos).is(ModTags.HONEY);
//    }

    public static class Flowing extends HoneyFluid
    {
        public Flowing() {
            super();
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends HoneyFluid
    {
        public Source() {
            super();
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}