package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;

import javax.annotation.Nonnull;

public class PortalNectarParticle extends PortalParticle
{
    public PortalNectarParticle(ClientLevel world, double x, double y, double z, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(world, x, y, z, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    public static class PortalNectarFactory implements ParticleProvider<NectarParticleType>
    {
        protected final SpriteSet sprite;

        public PortalNectarFactory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(NectarParticleType typeIn, @Nonnull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PortalParticle dripparticle = new PortalNectarParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.pickSprite(this.sprite);

            return dripparticle;
        }
    }
}
