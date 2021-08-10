package cy.jdkdigital.productivebees.client.particle;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.material.Fluid;

public class RisingNectarParticle extends FallingNectarParticle
{
    public RisingNectarParticle(ClientLevel world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z, fluid);
        this.lifetime = (int) (16.0D / (ProductiveBees.rand.nextDouble() * 0.8D + 0.2D));
        this.gravity = -0.007F;
    }
}
