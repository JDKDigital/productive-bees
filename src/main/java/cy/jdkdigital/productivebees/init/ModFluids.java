package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.fluid.HoneyFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModFluids
{
    public static final Material MATERIAL_HONEY = (new Material.Builder(MaterialColor.ORANGE_TERRACOTTA)).doesNotBlockMovement().liquid().notSolid().replaceable().liquid().build();

    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, ProductiveBees.MODID);

    public static final RegistryObject<FlowingFluid> HONEY = createFluid("honey", HoneyFluid.Source::new, ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<FlowingFluid> HONEY_FLOWING = createFluid("flowing_honey", HoneyFluid.Flowing::new, ModItemGroups.PRODUCTIVE_BEES);

    public static <B extends Fluid> RegistryObject<B> createFluid(String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return FLUIDS.register(name, supplier);
    }
}
