package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LavaNectarParticle extends LavaParticle
{
    public LavaNectarParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
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

        @Nullable
        @Override
        public Particle makeParticle(@Nonnull NectarParticleType nectarParticleType, @Nonnull ClientWorld clientWorld, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LavaNectarParticle dripparticle = new LavaNectarParticle(clientWorld, x, y, z);

            float[] colors = nectarParticleType.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.selectSpriteRandomly(this.sprite);

            return dripparticle;
        }
    }
}
