package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.NectarParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ProductiveBees.MODID);

    public static final DeferredHolder<ParticleType<?>, NectarParticleType> COLORED_FALLING_NECTAR = register("colored_falling_nectar");
    public static final DeferredHolder<ParticleType<?>, NectarParticleType> COLORED_RISING_NECTAR = register("colored_rising_nectar");
    public static final DeferredHolder<ParticleType<?>, NectarParticleType> COLORED_LAVA_NECTAR = register("colored_lava_nectar");
    public static final DeferredHolder<ParticleType<?>, NectarParticleType> COLORED_POPPING_NECTAR = register("colored_popping_nectar");
    public static final DeferredHolder<ParticleType<?>, NectarParticleType> COLORED_PORTAL_NECTAR = register("colored_portal_nectar");

    private static DeferredHolder<ParticleType<?>, NectarParticleType> register(@Nonnull String key) {
        return PARTICLE_TYPES.register(key, NectarParticleType::new);
    }
}
