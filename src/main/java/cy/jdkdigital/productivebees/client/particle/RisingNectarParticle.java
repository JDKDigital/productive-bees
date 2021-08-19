package cy.jdkdigital.productivebees.client.particle;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;

public class RisingNectarParticle extends FallingNectarParticle
{
    private final Fluid type;

    public RisingNectarParticle(ClientLevel world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z, fluid);
        this.lifetime = (int) (16.0D / (ProductiveBees.rand.nextDouble() * 0.8D + 0.2D));
        this.gravity = -0.007F;
        this.type = fluid;
    }

    public static class RisingNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public RisingNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DripParticle dripparticle = new RisingNectarParticle(world, x, y, z, Fluids.EMPTY);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            } else {
                dripparticle.setColor(0.92F, 0.782F, 0.72F);
            }

            dripparticle.pickSprite(this.sprite);

            return dripparticle;
        }
    }
}
