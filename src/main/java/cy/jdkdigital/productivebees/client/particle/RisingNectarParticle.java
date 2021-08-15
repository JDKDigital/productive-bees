package cy.jdkdigital.productivebees.client.particle;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import javax.annotation.Nonnull;

public class RisingNectarParticle extends FallingNectarParticle
{
    private final Fluid type;

    public RisingNectarParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z, fluid);
        this.lifetime = (int) (16.0D / (ProductiveBees.rand.nextDouble() * 0.8D + 0.2D));
        this.gravity = -0.017F;
        this.type = fluid;
    }

    public static class RisingNectarFactory implements IParticleFactory<NectarParticleType>
    {
        protected final IAnimatedSprite sprite;

        public RisingNectarFactory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DripParticle dripparticle = new RisingNectarParticle(world, x, y, z, Fluids.EMPTY);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }
            else {
                dripparticle.setColor(0.92F, 0.782F, 0.72F);
            }

            dripparticle.pickSprite(this.sprite);

            return dripparticle;
        }
    }
}
