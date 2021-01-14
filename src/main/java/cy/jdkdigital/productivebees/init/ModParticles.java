package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.NectarParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, ProductiveBees.MODID);

    public static final RegistryObject<NectarParticleType> COLORED_FALLING_NECTAR = register("colored_falling_nectar");
    public static final RegistryObject<NectarParticleType> COLORED_LAVA_NECTAR = register("colored_lava_nectar");
    public static final RegistryObject<NectarParticleType> COLORED_POPPING_NECTAR = register("colored_popping_nectar");

    private static RegistryObject<NectarParticleType> register(@Nonnull String key) {
        return PARTICLE_TYPES.register(key, NectarParticleType::new);
    }
}
