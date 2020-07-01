package cy.jdkdigital.productivebees.fluid;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class HoneyFluid extends FlowingFluid
{
    public static final ResourceLocation STILL = new ResourceLocation(ProductiveBees.MODID, "block/honey/still");
    public static final ResourceLocation FLOWING = new ResourceLocation(ProductiveBees.MODID, "block/honey/flow");
    public static final ResourceLocation OVERLAY = new ResourceLocation(ProductiveBees.MODID, "block/honey/overlay");

    @Override
    public Fluid getFlowingFluid() {
        return ModFluids.HONEY_FLOWING.get();
    }

    @Override
    public Fluid getStillFluid() {
        return ModFluids.HONEY.get();
    }

    @Override
    public Item getFilledBucket() {
        return ModItems.HONEY_BUCKET.get();
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(World worldIn, BlockPos pos, FluidState state, Random random) {
        BlockPos blockpos = pos.up();
        if (worldIn.getBlockState(blockpos).isAir() && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos)) {
            if (random.nextInt(100) == 0) {
                worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }

    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.builder(STILL, FLOWING)
                .overlay(OVERLAY)
                .translationKey("fluid." + ProductiveBees.MODID + ".honey")
                .color(0xFFFF9116)
                .density(3000)
                .viscosity(6000)
                .build(ModFluids.HONEY.get());
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public IParticleData getDripParticleData() {
        return ParticleTypes.DRIPPING_HONEY;
    }

    protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState state) {
        this.triggerEffects(world, pos);
    }

    public int getSlopeFindDistance(IWorldReader worldIn) {
        return worldIn.func_230315_m_().func_236040_e_() ? 6 : 3;
    }

    @Override
    public BlockState getBlockState(FluidState state) {
        return ModBlocks.HONEY.get().getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
    }

    public boolean isEquivalentTo(Fluid fluidIn) {
        return fluidIn == ModFluids.HONEY.get() || fluidIn == ModFluids.HONEY_FLOWING.get();
    }

    public int getLevelDecreasePerBlock(IWorldReader worldIn) {
        return worldIn.func_230315_m_().func_236040_e_() ? 1 : 2;
    }

    public boolean canDisplace(FluidState state, IBlockReader worldIn, BlockPos pos, Fluid fluid, Direction direction) {
        return state.getActualHeight(worldIn, pos) >= 0.44444445F && fluid.isIn(FluidTags.WATER);
    }

    public int getTickRate(IWorldReader worldIn) {
        return worldIn.func_230315_m_().func_236040_e_() ? 10 : 30;
    }

    public int func_215667_a(World world, BlockPos pos, FluidState state, FluidState FluidState) {
        int i = this.getTickRate(world);
        if (!state.isEmpty() && !FluidState.isEmpty() && !state.get(FALLING) && !FluidState.get(FALLING) && FluidState.getActualHeight(world, pos) > state.getActualHeight(world, pos) && world.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    private void triggerEffects(IWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    protected boolean canSourcesMultiply() {
        return false;
    }

    protected float getExplosionResistance() {
        return 100.0F;
    }

    public static class Flowing extends HoneyFluid
    {
        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends HoneyFluid
    {
        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}