package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.fluid.HoneyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModFluids
{
    public static final Material MATERIAL_HONEY = (new Material.Builder(MaterialColor.TERRACOTTA_ORANGE)).noCollider().liquid().nonSolid().replaceable().liquid().build();

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ProductiveBees.MODID);

    public static final RegistryObject<ForgeFlowingFluid> HONEY = createFluid("honey", HoneyFluid.Source::new);
    public static final RegistryObject<ForgeFlowingFluid> HONEY_FLOWING = createFluid("flowing_honey", HoneyFluid.Flowing::new);

    private static <B extends Fluid> RegistryObject<B> createFluid(String name, Supplier<? extends B> supplier) {
        return FLUIDS.register(name, supplier);
    }
}
