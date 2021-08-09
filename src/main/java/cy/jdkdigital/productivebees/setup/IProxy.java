package cy.jdkdigital.productivebees.setup;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IProxy
{
    Level getWorld();

    Player getPlayer();
}