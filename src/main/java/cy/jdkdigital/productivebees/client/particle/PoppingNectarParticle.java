package cy.jdkdigital.productivebees.client.particle;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import javax.annotation.Nonnull;

public class PoppingNectarParticle extends LavaParticle
{
    public PoppingNectarParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);
        this.lifetime = (int) (10.0D / (world.random.nextDouble() * 0.8D + 0.2D));
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {
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
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PoppingNectarParticle dripparticle = new PoppingNectarParticle(world, x, y, z);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.pickSprite(this.sprite);

            return dripparticle;
        }
    }
}
