package cy.jdkdigital.productivebees.common.fluid;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class HoneyFluid extends ForgeFlowingFluid
{
    public static final ResourceLocation STILL = new ResourceLocation(ProductiveBees.MODID, "block/honey/still");
    public static final ResourceLocation FLOWING = new ResourceLocation(ProductiveBees.MODID, "block/honey/flow");
    public static final ResourceLocation OVERLAY = new ResourceLocation(ProductiveBees.MODID, "block/honey/overlay");

    protected HoneyFluid() {
        super(new ForgeFlowingFluid.Properties(
                ModFluids.HONEY,
                ModFluids.HONEY_FLOWING,
                FluidAttributes.builder(HoneyFluid.STILL, HoneyFluid.FLOWING)
                        .overlay(HoneyFluid.OVERLAY)
                        .translationKey("fluid." + ProductiveBees.MODID + ".honey")
                        .color(0xffffc916)
                        .density(3000)
                        .viscosity(6000)
        ).bucket(ModItems.HONEY_BUCKET));
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
    public void animateTick(Level worldIn, BlockPos pos, FluidState state, Random random) {
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