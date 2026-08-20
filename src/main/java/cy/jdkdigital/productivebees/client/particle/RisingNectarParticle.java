package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

public class RisingNectarParticle extends FallingNectarParticle
{
    public RisingNectarParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites);
        this.lifetime = (int) (16.0D / (level.getRandom().nextDouble() * 0.8D + 0.2D));
        this.gravity = -0.007F;
    }

    public static class RisingNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public RisingNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull RandomSource random) {
            RisingNectarParticle particle = new RisingNectarParticle(world, x, y, z, this.sprite);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                particle.setColor(colors[0], colors[1], colors[2]);
            } else {
                particle.setColor(0.92F, 0.782F, 0.72F);
            }

            return particle;
        }
    }
}
