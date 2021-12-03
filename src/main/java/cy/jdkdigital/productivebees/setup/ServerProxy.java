package cy.jdkdigital.productivebees.setup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ServerProxy implements IProxy
{
    @Override
    public Level getWorld() {
        ServerLevel world = null;
        try {
            world = ServerLifecycleHooks.getCurrentServer().overworld();
        } catch (Exception e) {
            // Ignore for now
        }
        return world;
    }

    @Override
    public Player getPlayer() {
        throw new IllegalStateException("Only run this on the client!");
    }
}