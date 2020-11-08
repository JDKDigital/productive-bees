package cy.jdkdigital.productivebees.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerProxy implements IProxy
{
    @Override
    public World getWorld() {
        return ServerLifecycleHooks.getCurrentServer().func_241755_D_();
    }

    @Override
    public PlayerEntity getPlayer() {
        throw new IllegalStateException("Only run this on the client!");
    }
}