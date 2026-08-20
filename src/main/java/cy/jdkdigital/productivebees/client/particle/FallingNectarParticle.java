package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

public class FallingNectarParticle extends SingleQuadParticle
{
    public FallingNectarParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.lifetime = (int) (16.0D / (level.getRandom().nextDouble() * 0.8D + 0.2D));
        this.gravity = 0.007F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        setSpriteFromAge(sprites);
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.98F;
            this.yd *= 0.98F;
            this.zd *= 0.98F;
            if (this.onGround) {
                this.remove();
            }
        }
    }

    public static class FallingNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public FallingNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull RandomSource random) {
            FallingNectarParticle particle = new FallingNectarParticle(world, x, y, z, this.sprite);

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
