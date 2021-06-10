package cy.jdkdigital.productivebees.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerProxy implements IProxy
{
    @Override
    public World getWorld() {
        ServerWorld world = null;
        try {
            world = ServerLifecycleHooks.getCurrentServer().overworld();
        } catch (Exception e) {
            // Ignore for now
        }
        return world;
    }

    @Override
    public PlayerEntity getPlayer() {
        throw new IllegalStateException("Only run this on the client!");
    }
}