package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PoppingNectarParticle extends LavaParticle
{
    public PoppingNectarParticle(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.maxAge = (int)(10.0D / (Math.random() * 0.8D + 0.2D));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
            if (this.onGround) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
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
        public Particle makeParticle(@Nonnull NectarParticleType typeIn, @Nonnull World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PoppingNectarParticle dripparticle = new PoppingNectarParticle(world, x, y, z);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.selectSpriteRandomly(this.sprite);

            return dripparticle;
        }
    }
}
