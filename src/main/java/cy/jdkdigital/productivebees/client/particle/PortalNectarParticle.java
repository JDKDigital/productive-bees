package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

public class PortalNectarParticle extends PortalParticle
{
    public PortalNectarParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites.first());
        setSpriteFromAge(sprites);
    }

    public static class PortalNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public PortalNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull RandomSource random) {
            PortalNectarParticle particle = new PortalNectarParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                particle.setColor(colors[0], colors[1], colors[2]);
            }

            return particle;
        }
    }
}
