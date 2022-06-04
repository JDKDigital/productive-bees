package cy.jdkdigital.productivebees.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class HoneyFluidBlock extends LiquidBlock
{
    public HoneyFluidBlock(Supplier<? extends ForgeFlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos position, Entity entity) {
        if (entity instanceof Bee) {
            ((Bee) entity).addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, true));
        }

        super.entityInside(state, world, position, entity);
    }
}
