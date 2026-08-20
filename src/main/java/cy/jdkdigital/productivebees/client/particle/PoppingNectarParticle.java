package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

public class PoppingNectarParticle extends SingleQuadParticle
{
    public PoppingNectarParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.lifetime = (int) (10.0D / (level.getRandom().nextDouble() * 0.8D + 0.2D));
        setSpriteFromAge(sprites);
    }

    @Override
    public Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 240;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.8F;
            this.yd *= 0.8F;
            this.zd *= 0.8F;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public static class PoppingNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public PoppingNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull RandomSource random) {
            PoppingNectarParticle particle = new PoppingNectarParticle(world, x, y, z, this.sprite);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                particle.setColor(colors[0], colors[1], colors[2]);
            }

            return particle;
        }
    }
}
