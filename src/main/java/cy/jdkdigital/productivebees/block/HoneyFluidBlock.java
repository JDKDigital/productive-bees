package cy.jdkdigital.productivebees.block;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

import java.util.function.Supplier;

public class HoneyFluidBlock extends FlowingFluidBlock
{
    public HoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

}
