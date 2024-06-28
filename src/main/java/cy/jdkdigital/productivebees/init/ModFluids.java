package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.fluid.HoneyFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ProductiveBees.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ProductiveBees.MODID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid> HONEY = createFluid("honey", HoneyFluid.Source::new);
    public static final DeferredHolder<Fluid, BaseFlowingFluid> HONEY_FLOWING = createFluid("flowing_honey", HoneyFluid.Flowing::new);

    private static <B extends Fluid> DeferredHolder<Fluid, B> createFluid(String name, Supplier<? extends B> supplier) {
        return FLUIDS.register(name, supplier);
    }

    public static DeferredHolder<FluidType, FluidType> HONEY_FLUID_TYPE = FLUID_TYPES.register("honey", () -> new FluidType(FluidType.Properties.create().canExtinguish(true).supportsBoating(true).motionScale(0.007D))
    {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return HoneyFluid.STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return HoneyFluid.FLOWING;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return HoneyFluid.OVERLAY;
                }

                @Override
                public int getTintColor() {
                    return 0xffffc916;
                }
            });
        }
    });
}
