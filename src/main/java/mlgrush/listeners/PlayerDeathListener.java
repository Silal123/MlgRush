package mlgrush.listeners;

import mlgrush.MlgRush;
import mlgrush.MlgRushGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();

        e.setCancelled(true);
        System.out.println("death");
        MlgRushGame.killPlayer(p);
    }
}
