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

public class FallingNectarParticle extends DripParticle
{
    public FallingNectarParticle(ClientLevel world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z, fluid);
        this.lifetime = (int) (16.0D / (ProductiveBees.rand.nextDouble() * 0.8D + 0.2D));
        this.gravity = 0.007F;
    }

    @Override
    protected void postMoveUpdate() {
        if (this.onGround) {
            this.remove();
        }
    }

    public static class FallingNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public FallingNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DripParticle dripparticle = new FallingNectarParticle(world, x, y, z, Fluids.EMPTY);

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
