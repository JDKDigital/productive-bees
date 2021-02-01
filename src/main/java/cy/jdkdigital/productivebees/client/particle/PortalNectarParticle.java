package cy.jdkdigital.productivebees.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.world.ClientWorld;

public class PortalNectarParticle extends PortalParticle
{
    public PortalNectarParticle(ClientWorld world, double x, double y, double z, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(world, x, y, z, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    public static class PortalNectarFactory implements IParticleFactory<NectarParticleType>
    {
        protected final IAnimatedSprite sprite;

        public PortalNectarFactory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle makeParticle(NectarParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PortalParticle dripparticle = new PortalNectarParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);

            float[] colors = typeIn.getColor();
            if (colors != null) {
                dripparticle.setColor(colors[0], colors[1], colors[2]);
            }

            dripparticle.selectSpriteRandomly(this.sprite);

            return dripparticle;
        }
    }
}
