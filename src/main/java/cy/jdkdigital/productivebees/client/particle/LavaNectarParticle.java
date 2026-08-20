package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

public class LavaNectarParticle extends SingleQuadParticle
{
    public LavaNectarParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.gravity = 0.75F;
        this.xd *= 0.8;
        this.yd *= 0.8;
        this.zd *= 0.8;
        this.yd = Math.random() * 0.4D + 0.05D;
        this.quadSize *= Mth.randomBetween(level.getRandom(), 0.4F, 1.0F);
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
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = (float) this.age / (float) this.lifetime;
            if (this.level.getRandom().nextFloat() > f) {
                this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
            this.yd -= 0.03F * this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.999F;
            this.yd *= 0.999F;
            this.zd *= 0.999F;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public static class LavaNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public LavaNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType nectarParticleType, @Nonnull ClientLevel clientWorld, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull RandomSource random) {
            LavaNectarParticle particle = new LavaNectarParticle(clientWorld, x, y, z, this.sprite);

            float[] colors = nectarParticleType.getColor();
            if (colors != null) {
                particle.setColor(colors[0], colors[1], colors[2]);
            }

            return particle;
        }
    }
}
