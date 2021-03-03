package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.fluid.HoneyFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModFluids
{
    public static final Material MATERIAL_HONEY = (new Material.Builder(MaterialColor.ORANGE_TERRACOTTA)).doesNotBlockMovement().liquid().notSolid().replaceable().liquid().build();

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ProductiveBees.MODID);

    private static ForgeFlowingFluid.Properties properties() {
        return new ForgeFlowingFluid.Properties(
            HONEY,
            HONEY_FLOWING,
            FluidAttributes.builder(HoneyFluid.STILL, HoneyFluid.FLOWING)
                .overlay(HoneyFluid.OVERLAY)
                .translationKey("fluid." + ProductiveBees.MODID + ".honey")
                .color(0xffffc916)
                .density(3000)
                .viscosity(6000)
        );
    }

    public static final RegistryObject<ForgeFlowingFluid> HONEY = createFluid("honey", () -> new HoneyFluid.Source(properties()));
    public static final RegistryObject<ForgeFlowingFluid> HONEY_FLOWING = createFluid("flowing_honey", () -> new HoneyFluid.Flowing(properties()));

    private static <B extends Fluid> RegistryObject<B> createFluid(String name, Supplier<? extends B> supplier) {
        return FLUIDS.register(name, supplier);
    }
}
