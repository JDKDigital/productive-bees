package cy.jdkdigital.productivebees.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class HoneyFluidBlock extends FlowingFluidBlock
{
    public HoneyFluidBlock(Supplier<? extends ForgeFlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Deprecated
    @Override
    public void entityInside(BlockState state, World world, BlockPos position, Entity entity) {
        if (entity instanceof BeeEntity) {
            ((BeeEntity) entity).addEffect(new EffectInstance(Effects.REGENERATION, 80, 0, false, true));
        }

        super.entityInside(state, world, position, entity);
    }
}
