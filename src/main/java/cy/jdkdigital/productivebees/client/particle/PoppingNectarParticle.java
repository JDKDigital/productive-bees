package cy.jdkdigital.productivebees.client.particle;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

import javax.annotation.Nonnull;

public class PoppingNectarParticle extends LavaParticle
{
    public PoppingNectarParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        this.lifetime = (int) (10.0D / (ProductiveBees.rand.nextDouble() * 0.8D + 0.2D));
    }

    @Nonnull
    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
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

    public static class PoppingNectarFactory implements IParticleFactory<NectarParticleType>
    {
        protected final IAnimatedSprite sprite;

        public PoppingNectarFactory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@Nonnull NectarParticleType typeIn, @Nonnull ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
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
