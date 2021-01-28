package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class LavaNectarParticle extends LavaParticle
{
    public LavaNectarParticle(World world, double x, double y, double z) {
        super(world, x, y, z);
//        this.particleGravity = 0.007F;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class LavaNectarFactory implements IParticleFactory<NectarParticleType>
    {
        protected final IAnimatedSprite sprite;

        public LavaNectarFactory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle makeParticle(@Nonnull NectarParticleType typeIn, @Nonnull World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LavaNectarParticle dripparticle = new LavaNectarParticle(world, x, y, z);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.selectSpriteRandomly(this.sprite);

            return dripparticle;
        }
    }
}
