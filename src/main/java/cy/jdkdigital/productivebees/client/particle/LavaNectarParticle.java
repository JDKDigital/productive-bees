package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LavaNectarParticle extends LavaParticle
{
    public LavaNectarParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class LavaNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public LavaNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(@Nonnull NectarParticleType nectarParticleType, @Nonnull ClientLevel clientWorld, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LavaNectarParticle dripparticle = new LavaNectarParticle(clientWorld, x, y, z);

            float[] colors = nectarParticleType.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.pickSprite(this.sprite);

            return dripparticle;
        }
    }
}
