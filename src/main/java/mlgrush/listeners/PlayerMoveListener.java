package mlgrush.listeners;

import mlgrush.MlgRush;
import mlgrush.MlgRushGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (e.getTo().y() < MlgRush.spectator_spawn.y() - 10) {
            MlgRushGame.killPlayer(p);
            return;
        }

        if (e.getTo().toVector().distance(MlgRush.spectator_spawn.toVector()) > 40) {
            MlgRushGame.killPlayer(p);
            return;
        }
    }
}
