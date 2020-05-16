package cy.jdkdigital.productivebees.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class HoneyFluidBlock extends FlowingFluidBlock
{
    public HoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos position, Entity entity) {
        if (entity instanceof BeeEntity) {
            ((BeeEntity) entity).addPotionEffect(new EffectInstance(Effects.REGENERATION, 80, 0, false, true));
        }

        super.onEntityCollision(state, world, position, entity);
    }
}
